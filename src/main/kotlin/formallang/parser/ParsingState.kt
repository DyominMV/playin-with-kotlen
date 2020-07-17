package formallang.parser

import automata.*
import forkable.*
import formallang.ast.*
import formallang.grammar.*
import kotlinx.coroutines.*

private class LeafData(
  val symbol: Symbol,
  val value: String
)

private class BranchData(
  val nonTerminal: NonTerminal,
  val rule: SimplifiedRule
)

private typealias ParsingTree = ForkableTree<BranchData, LeafData>

private typealias CharacterStream = ForkableStream<Char>

private fun ParsingTree.toAst(): Ast {
  return Ast(toAstNode(this.getRoot()))
}

private fun toAstNode(node: ITreeNode<BranchData, LeafData>): Node =
  when (node) {
    is IBranch ->
      if (node.getChildren().size == 0)
        EmptyLeaf
      else
        Branch(
          node.getData().nonTerminal,
          node.getData().rule,
          node.getChildren().map(::toAstNode)
        )
    is ILeaf -> {
      val nodeData = node.getData()
      when (nodeData.symbol) {
        is Terminal -> TerminalLeaf(nodeData.symbol)
        is SpecialSymbol -> SpecialLeaf(nodeData.symbol, nodeData.value.get(0))
        is EndOfFile -> EndOfFileLeaf
        is NonTerminal -> if (0 < nodeData.value.length)
            CompoundLeaf(nodeData.symbol, nodeData.value)
          else
            EmptyLeaf
      }
    }
    else -> throw InvalidTreeException()
  }

sealed class ParsingState() : State {
  public abstract fun transition(): Iterable<ParsingState>
}

private class RegularState constructor(
  val grammar: SimplifiedGrammar,
  val parsingTree: ParsingTree,
  val inputStream: CharacterStream,
  val knownChar: Char?
) : ParsingState() {
  public override fun getType(): State.StateType = State.StateType.UNFINISHED

  fun getTree(): Ast = parsingTree.toAst()

  private fun endOfFileTransition(marker: IBranch<BranchData, LeafData>):
      Iterable<ParsingState> {
    if (null == knownChar) {
      marker.addLeafChild(LeafData(EndOfFile(), ""))
      return arrayListOf(this)
    } else return arrayListOf(FailedState())
  }

  private fun specialSymbolTransition(
    symbol: SpecialSymbol,
    marker: IBranch<BranchData, LeafData>
  ): Iterable<ParsingState> {
    if (symbol.filter(knownChar)) {
      marker.addLeafChild(LeafData(symbol, knownChar.toString()))
      return listOf(RegularState(grammar, parsingTree, inputStream,
          inputStream.next()))
    } else return arrayListOf(FailedState())
  }

  private fun terminalTransition(
    symbol: Terminal,
    marker: IBranch<BranchData, LeafData>
  ): Iterable<ParsingState> {
    var char = knownChar
    for (i in 0..(symbol.value.length - 1)) {
      if (symbol.value.get(i) == char)
        char = inputStream.next()
      else
        return listOf(FailedState())
    }
    marker.addLeafChild(LeafData(symbol, ""))
    return listOf(RegularState(grammar, parsingTree, inputStream, char))
  }

  private fun nonTerminalTransition(symbol: NonTerminal):
      Iterable<ParsingState> {
    val branchList = grammar.getPossibleRules(symbol, knownChar)
      .map({ BranchData(symbol, it) })
    if (branchList.isEmpty()) return listOf(FailedState())
    val count = branchList.size
    val trees = parsingTree.fork(count).iterator()
    val streams = inputStream.fork(count).iterator()
    val branches = branchList.iterator()
    val states = ArrayList<ParsingState>()
    for (i in 1..count) {
      val newState =
        RegularState(grammar, trees.next(), streams.next(), knownChar)
      states.add(newState)
      val newBranch = branches.next()
      val marker = newState.parsingTree.getMarker() as IBranch
      marker.addBranchChild(newBranch)
      newState.parsingTree.setMarker(marker.getChildren().first())
      states.add(newState)
    }
    return states
  }

  override fun transition(): Iterable<ParsingState> {
    val marker = parsingTree.getMarker()
    if (!(marker is IBranch))
      throw IllegalStateException("We do not mark leaves")
    val nextSymbolIndex = marker.getChildren().size
    if (marker.getData().rule.symbols.size == nextSymbolIndex) {
      val parent = marker.getParent()
      if (null == parent) return listOf(SuccessState(this))
      parsingTree.setMarker(parent)
      return listOf(this)
    }
    if (marker.getData().rule.symbols.size > nextSymbolIndex) {
      val nextSymbol = marker.getData().rule.symbols.get(nextSymbolIndex)
      when (nextSymbol) {
        is EndOfFile -> return endOfFileTransition(marker)
        is SpecialSymbol -> return specialSymbolTransition(nextSymbol, marker)
        is Terminal -> return terminalTransition(nextSymbol, marker)
        is NonTerminal -> return nonTerminalTransition(nextSymbol)
      }
    } else throw IllegalStateException("Children count cannot be more than symbols's count")
  }

  public constructor (
    grammar: SimplifiedGrammar,
    inputStream: CharacterStream,
    mainSymbol: Symbol
  ) : this(
    grammar,
    ParsingTree.branchTree(
      BranchData(NonTerminal(""),
      SimplifiedRule(mainSymbol))
    ),
    inputStream,
    runBlocking { inputStream.next() }
  )
}

private class SuccessState(
  val regularState: RegularState
) : ParsingState() {
  public override fun getType(): State.StateType = State.StateType.SUCCESS
  override fun transition(): Iterable<ParsingState> = listOf(this)
  public fun getAst(): Ast = regularState.getTree()
}

private class FailedState : ParsingState() {
  public override fun getType(): State.StateType = State.StateType.FAIL
  override fun transition(): Iterable<ParsingState> = listOf(this)
}
