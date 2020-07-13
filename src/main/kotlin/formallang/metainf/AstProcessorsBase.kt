package formallang.metainf

import formallang.ast.*
import formallang.grammar.*

interface AstProcessor {
  public fun process(ast: Ast): Ast
}

open class TaggedSet(
  val isTagged: (Node) -> Boolean,
  val nodeProcessor: (Node) -> List<Node>,
  val direction: ProcessingDirection
) : AstProcessor {
  public enum class ProcessingDirection {
    ROOT_FIRST, ROOT_LAST
  }

  private final fun processNodeRL(node: Node): List<Node> {
    var newNode = node 
    if (node is Fork){
      val newChildren = ArrayList<Node>()
      for (child in node.children) newChildren.addAll(processNodeRL(child))
      newNode = Fork(node.nonTerminal, node.rule, newChildren)
    }
    return if (isTagged(newNode)) nodeProcessor(newNode) else listOf(newNode)
  }

  private final fun processNodeRF(node: Node): List<Node> {
    if (isTagged(node))
      return nodeProcessor(node)
      var newNode = node
      if (node is Fork){
        val newChildren = ArrayList<Node>() 
        for (child in node.children) newChildren.addAll(processNodeRF(child))
        newNode = Fork(node.nonTerminal, node.rule, newChildren)
      }
    return listOf(newNode)
  }

  public final override fun process(ast: Ast): Ast =
    when (direction) {
      ProcessingDirection.ROOT_FIRST -> Ast(processNodeRF(ast.root).get(0))
      ProcessingDirection.ROOT_LAST -> Ast(processNodeRL(ast.root).get(0))
    }
}

open class TaggedNonTerminalSet(
  val nonTerminals: Set<NonTerminal>,
  nodeProcessor: (Node) -> List<Node>,
  direction: ProcessingDirection
) : TaggedSet(
  { (it is Fork) && nonTerminals.contains(it.nonTerminal) },
  nodeProcessor,
  direction
)

open class TaggedRuleSet(
  val rules: Set<SimplifiedRule>,
  nodeProcessor: (Node) -> List<Node>,
  direction: ProcessingDirection
) : TaggedSet(
  { (it is Fork) && rules.contains(it.rule) },
  nodeProcessor,
  direction
)
