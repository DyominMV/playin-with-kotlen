package forkable

import kotlinx.coroutines.*
import kotlinx.coroutines.sync.*

interface IForkableStream<T> : IForkable<IForkableStream<T>> {
  public suspend fun next(): T? // null is for EOF
}

private open class SharedNode<T>(
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

private class SharedArrayNode<T>(
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

class ForkableStream<T> private constructor(
  node: SharedNode<ArrayList<T?>>,
  var elementIndex: Int
) : IForkableStream<T> {

  private var node: SharedNode<ArrayList<T?>> = node
  
  constructor(producer: () -> T?) : this(
    SharedArrayNode<T>(arrayListOf(), producer),
    -1
  ) {
    runBlocking {
      node = node.getNextNode()
    }
  }

  private var blocked: Boolean = false

  override suspend fun next(): T? {
    if (blocked) throw AlreadyForkedException()
    if (elementIndex >= node.value.size) {
      elementIndex = -1
      node = node.getNextNode()
    }
    elementIndex = elementIndex + 1
    return node.value[elementIndex]
  }

  override suspend fun fork(count: Int): Iterable<ForkableStream<T>> {
    if (blocked) throw AlreadyForkedException()
    val list = ArrayList<ForkableStream<T>>(count)
    repeat(count) {
      list.add(ForkableStream<T>(node, elementIndex))
    }
    blocked = true
    return list
  }
}
