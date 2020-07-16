package forkable

import java.util.WeakHashMap
import java.util.Collections

interface INode<BranchData, LeafData> {
  public fun getParent(): INode<BranchData, LeafData>?
}

interface IBranch<BranchData, LeafData> : INode<BranchData, LeafData> {
  public fun getChildren(): List<INode<BranchData, LeafData>>
  public fun addBranchChild(branchData: BranchData)
  public fun addLeafChild(leafData: LeafData)
  public fun getData(): BranchData
  public fun setData(data: BranchData)
}

interface ILeaf<BranchData, LeafData> : INode<BranchData, LeafData> {
  public fun getData(): LeafData
  public fun setData(data: LeafData)
}

interface ITree<BranchData, LeafData> {
  public fun getRoot(): INode<BranchData, LeafData>
  public fun getMarker(): INode<BranchData, LeafData>
  public fun setMarker(node: INode<BranchData, LeafData>) 
}

open class Node<BranchData, LeafData>(
  val parent: INode<BranchData, LeafData>?
) : INode<BranchData, LeafData> {
  override fun getParent(): INode<BranchData, LeafData>? = parent
}

class Leaf<BranchData, LeafData>(
  parent: INode<BranchData, LeafData>?,
  var data: LeafData
) : Node<BranchData, LeafData>(parent), ILeaf<BranchData, LeafData> {
  override fun getData(): LeafData = data
  override fun setData(data: LeafData) {this.data = data}
}

class RealBranch<BranchData, LeafData>(
  parent: INode<BranchData, LeafData>?
  var data: BranchData
) : Node<BranchData, LeafData>(parent){
  private val children = Collections.synchronizedMap(
   WeakHashMap<ForkableTree<BranchData, LeafData>, MutableList<Node<BranchData, LeafData>>>()
  )
  fun getData(): BranchData = data
  fun setData(data: BranchData) {this.data = data}

  fun getChildren(caller: ForkableTree<BranchData, LeafData>): List<INode<BranchData, LeafData>> =
    ((caller.inherited union hashSetOf(caller)) intersect (children.keys)).map {children.get(it)!!}
      .fold(ArrayList<INode<BranchData, LeafData>>(), { acc, next -> 
          acc.addAll(next) 
          acc 
        }).map { if (it is RealBranch) Branch(it, caller) else it }
  
  private fun getOrCreateChildrenList(caller: ForkableTree<BranchData, LeafData>) : 
      MutableList<INode<BranchData, LeafData>> = (
          children.get(caller) ?: {
          val list = arrayListOf<Node<BranchData, LeafData>>()
          children.put(caller, list)
          list
        }()) as MutableList<INode<BranchData, LeafData>>

  fun addLeafChild(caller: ForkableTree<BranchData, LeafData>, data: LeafData){
    getOrCreateChildrenList(caller).add(Leaf(this, data))
  }

  fun addBranchChild(caller: ForkableTree<BranchData, LeafData>, data: BranchData){
    getOrCreateChildrenList(caller).add(
      RealBranch(this, data)
    )
  }
}

class Branch<BranchData, LeafData> (
  val realBranch: RealBranch<BranchData, LeafData>
  val caller: ForkableTree<BranchData, LeafData>
) : IBranch<BranchData, LeafData> {
  override fun getChildren(): List<INode<BranchData, LeafData>> =
    realBranch.getChildren(caller)
  override fun addBranchChild(branchData: BranchData) = 
    realBranch.addBranchChild(caller, branchData)
  override fun addLeafChild(leafData: LeafData) =
    realBranch.addLeafChild(caller, leafData)
  override fun getData(): BranchData = 
    realBranch.getData()
  override fun setData(data: BranchData) =
    realBranch.setData(data)
  override fun getParent(): INode<BranchData, LeafData>? = 
    realBranch.parent
}

class ForkableTree<BranchData, LeafData> private constructor(
  val inherited: Set<ForkableTree<BranchData, LeafData>>
) : IForkable<ForkableTree<BranchData, LeafData>>, ITree<BranchData, LeafData> {
  
  lateinit var root: INode<BranchData, LeafData>
  
  private constructor(
    inherited: Set<ForkableTree<BranchData, LeafData>>, 
    root: INode<BranchData, LeafData>
  ): this(inherited){
    this.root = root
  }

  companion object{
    public fun <BranchData,LeafData> leafTree(rootData: LeafData): 
        ForkableTree<BranchData,LeafData>{
      val tree = ForkableTree(HashSet<ForkableTree<BranchData, LeafData>>())
      tree.root = Leaf(null, rootData)
      return tree
    }
    public fun <BranchData,LeafData> branchTree(rootData: BranchData): 
        ForkableTree<BranchData,LeafData> {
      val tree = ForkableTree(HashSet<ForkableTree<BranchData, LeafData>>())
      tree.root = Branch(RealBranch(null, rootData), tree)
      return tree
    }
  }
  
  private var blocked: Boolean = false

  private var marker = root
  override fun getMarker() = 
    if (blocked) throw AlreayForkedException() else marker
  override fun setMarker(node: INode<BranchData, LeafData>) {
    if (blocked) throw AlreayForkedException()
    this.marker = node
  }

  override fun getRoot(): INode<BranchData, LeafData> = 
    if (blocked) throw AlreayForkedException() else root

  override suspend fun fork(count: Int): Iterable<ForkableTree<BranchData, LeafData>>{
    if (blocked) throw AlreayForkedException()
    val list = ArrayList<ForkableTree<BranchData, LeafData>>(count)
    repeat(count){
      val tree =ForkableTree(this.inherited union hashSetOf(this), root)
      if (marker is Branch){
        tree.setMarker(Branch((marker as Branch).realBranch , tree))
      } else {  
        tree.setMarker(marker)
      }
      list.add(tree)
    }
    blocked = true
    return list
  }
}
