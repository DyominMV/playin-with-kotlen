package forkable

interface IForkableStack<T> : IForkable<IForkableStack<T>> {
  public fun push(element: T)
  public fun peek(): T? // null means that stack is empty 
  public fun pull(): T? // null means that stack is empty
}

private open class StackNode<T>(
  val data: T,
  val parent: StackNode<T>?
)

class ForkableStack<T> private constructor(
  currentNode: StackNode<T>?
) : IForkableStack<T> {

  private var currentNode: StackNode<T>? = currentNode
  
  override fun push(element: T) {
    if (blocked) throw AlreadyForkedException()
    currentNode = StackNode(element, currentNode)
  }

  override fun peek(): T? = if (blocked) throw AlreadyForkedException() 
    else currentNode?.data

  override fun pull(): T? {
    if (blocked) throw AlreadyForkedException()
    val result = currentNode?.data
    currentNode = currentNode?.parent
    return result
  }

  private var blocked = false

  override fun fork(count: Int): Iterable<IForkableStack<T>> {
    val list = ArrayList<IForkableStack<T>>(count)
    repeat(count){
      list.add(ForkableStack(currentNode))
    }
    return list
  }
}
