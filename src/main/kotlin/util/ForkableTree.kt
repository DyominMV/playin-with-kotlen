package util

interface INode<BranchData, LeafData> {
  public fun getParent(): INode<BranchData, LeafData>?
}

interface IBranch<BranchData, LeafData> : INode<BranchData, LeafData> {
  public fun getChildren(): List<INode<BranchData, LeafData>>
  public fun addChild(newChild: INode<BranchData, LeafData>)
  public fun getData(): BranchData
  public fun setData(data: BranchData)
}

interface ILeaf<BranchData, LeafData> : INode<BranchData, LeafData> {
  public fun getData(): LeafData
  public fun setData(data: LeafData)
}

interface ITree<BranchData, LeafData> {
  public fun getRoot(): INode<BranchData, LeafData>
}

class Node<BranchData, LeafData>(val parent: INode<BranchData, LeafData>?) :
    INode<BranchData, LeafData> {
  override fun getParent(): INode<BranchData, LeafData>? = parent
  // also information about trees it belongs to
}

class ForkableTree<BranchData, LeafData>() : IForkable<ForkableTree<BranchData, LeafData>>, ITree<BranchData, LeafData> {
  override suspend fun fork(count: Int): Iterable<ForkableTree<BranchData, LeafData>> =
    TODO()
  override fun getRoot(): INode<BranchData, LeafData> = TODO()
  // also information to identify own nodes
}
