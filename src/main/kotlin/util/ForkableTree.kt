package util

interface INode<ForkData, LeafData> {
  public fun getParent(): INode<ForkData, LeafData>?
}

interface IFork<ForkData, LeafData> : INode<ForkData, LeafData> {
  public fun getChildren(): List<INode<ForkData, LeafData>>
  public fun addChild(newChild: INode<ForkData, LeafData>)
  public fun getData(): ForkData
  public fun setData(data: ForkData)
}

interface ILeaf<ForkData, LeafData> : INode<ForkData, LeafData> {
  public fun getData(): LeafData
  public fun setData(data: LeafData)
}

interface ITree<ForkData, LeafData> {
  public fun getRoot(): INode<ForkData, LeafData>
}

class Node<ForkData, LeafData>(val parent: INode<ForkData, LeafData>?) :
    INode<ForkData, LeafData> {
  override fun getParent(): INode<ForkData, LeafData>? = parent
  // also information about trees it belongs to
}

class ForkableTree<ForkData, LeafData>() : IForkable<ForkableTree<ForkData, LeafData>>, ITree<ForkData, LeafData> {
  override suspend fun fork(count: Int): Iterable<ForkableTree<ForkData, LeafData>> =
    TODO()
  override fun getRoot(): INode<ForkData, LeafData> = TODO()
  // also information to identify own nodes
}
