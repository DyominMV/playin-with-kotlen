package formallang.ast

import formallang.grammar.*

sealed class Node

class Branch(
  val nonTerminal: NonTerminal,
  val rule: SimplifiedRule,
  val children: List<Node>
) : Node(){
  public fun getString():String = children.fold("", {
    acc, nextNode -> acc + when (nextNode){
        is Leaf -> nextNode.getStringValue()
        is Branch -> nextNode.getString()
      }
  })
}

sealed class Leaf : Node(){
  public abstract fun getStringValue(): String
}

class TerminalLeaf(
  val terminal: Terminal
) : Leaf(){
  override public fun getStringValue() = terminal.value 
}

class SpecialLeaf(
  val specialSymbol: SpecialSymbol,
  val value: Char
) : Leaf(){
  override public fun getStringValue() = value.toString()
}

object EndOfFileLeaf : Leaf(){
  override public fun getStringValue() = ""
}

object EmptyLeaf : Leaf(){
  override public fun getStringValue() = ""
}

class CompoundLeaf(
  val nonTerminal: NonTerminal, 
  val value: String
) : Leaf(){
  override public fun getStringValue() = value
}

class Ast(val root: Node)