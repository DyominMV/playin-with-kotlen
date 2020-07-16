package util

import java.io.InputStreamReader
import kotlinx.coroutines.*
import kotlinx.coroutines.sync.*

interface IForkableStream<T> : IForkable<IForkableStream<T>> {
  public suspend fun next(): T? // null is for EOF
}

open class SharedNode<T>(
  val value: T,
  nextProducer: () -> T
) {
  private val nextProducer: () -> T = nextProducer
  private var next: SharedNode<T>? = null
  private val nextMutex: Mutex = Mutex()

  public suspend fun getNextNode(): SharedNode<T> {
    if (null == next) {
      nextMutex.withLock() {
        if (null == next)
          next = SharedNode<T>(nextProducer.invoke(), nextProducer)
      }
    }
    return next!!
  }
}

class SharedArrayNode<T>(
  value: ArrayList<T?>,
  val producer: () -> T?
) : SharedNode<ArrayList<T?>>(
  value, {
      val list = ArrayList<T?>(SharedArrayNode.ARRAY_SIZE)
      for (i in 1..SharedArrayNode.ARRAY_SIZE)
        list.add(producer())
      list
    }
) {
  companion object {
    val ARRAY_SIZE: Int = 10
  }
}

class ForkableStream<T>(
  var node: SharedNode<ArrayList<T?>>,
  var elementIndex: Int
) : IForkableStream<T> {

  constructor(producer: ()->T?) : this(
    SharedArrayNode<T>(arrayListOf(), producer),
    -1
  ) {
    runBlocking {
      node = node.getNextNode()
    }
  }

  override suspend fun next(): T? {
    if (elementIndex >= node.value.size) {
      elementIndex = -1
      node = node.getNextNode()
    }
    elementIndex = elementIndex + 1
    return node.value[elementIndex]
  }

  /**
   * Первый элемент всегда this
   */
  override suspend fun fork(count: Int): Iterable<ForkableStream<T>> {
    val list = arrayListOf(this)
    repeat(count-1){
      list.add(ForkableStream<T>(node, elementIndex))
    }
    return list
  }
}