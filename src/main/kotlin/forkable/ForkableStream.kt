package forkable

import kotlinx.coroutines.*
import kotlinx.coroutines.sync.*

interface IForkableStream<T> : IForkable<IForkableStream<T>> {
  public fun next(): T? // null is for EOF
}

private open class StreamNode<T>(
  val value: T,
  nextProducer: () -> T
) {
  private val nextProducer: () -> T = nextProducer
  private var next: StreamNode<T>? = null
  private val nextMutex: Mutex = Mutex()

  public fun getNextNode(): StreamNode<T> {
    if (null == next) {
      runBlocking { nextMutex.withLock() {
        if (null == next)
          next = StreamNode<T>(nextProducer.invoke(), nextProducer)
      } }
    }
    return next!!
  }
}

private class ArrayStreamNode<T>(
  value: ArrayList<T?>,
  val producer: () -> T?
) : StreamNode<ArrayList<T?>>(
  value, {
      val list = ArrayList<T?>(ArrayStreamNode.ARRAY_SIZE)
      for (i in 1..ArrayStreamNode.ARRAY_SIZE)
        list.add(producer())
      list
    }
) {
  companion object {
    val ARRAY_SIZE: Int = 10
  }
}

class ForkableStream<T> private constructor(
  node: StreamNode<ArrayList<T?>>,
  var elementIndex: Int
) : IForkableStream<T> {

  private var node: StreamNode<ArrayList<T?>> = node

  constructor(producer: () -> T?) : this(
    ArrayStreamNode<T>(arrayListOf(), producer),
    -1
  ) {
    runBlocking {
      node = node.getNextNode()
    }
  }

  private var blocked: Boolean = false

  override fun next(): T? {
    if (blocked) throw AlreadyForkedException()
    elementIndex = elementIndex + 1
    if (elementIndex >= node.value.size) {
      elementIndex = 0
      node = node.getNextNode()
    }
    return node.value[elementIndex]
  }

  override fun fork(count: Int): Iterable<ForkableStream<T>> {
    if (blocked) throw AlreadyForkedException()
    val list = ArrayList<ForkableStream<T>>(count)
    repeat(count) {
      list.add(ForkableStream<T>(node, elementIndex))
    }
    blocked = true
    return list
  }
}
