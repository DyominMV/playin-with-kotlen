package formallang.ast

import formallang.grammar.*

sealed class Node

class Fork(
  val nonTerminal: NonTerminal,
  val rule: SimplifiedRule,
  val children: List<Node>
) : Node(){
  public fun getString():String = children.fold("", {
    acc, nextNode -> acc + when (nextNode){
        is Leaf -> nextNode.getValue()
        is Fork -> nextNode.getString()
      }
  })
}

sealed class Leaf : Node(){
  public abstract fun getValue(): String
}

class TerminalLeaf(
  val terminal: Terminal
) : Leaf(){
  override public fun getValue() = terminal.value 
}

class SpecialLeaf(
  val specialSymbol: SpecialSymbol,
  val value: String
) : Leaf(){
  override public fun getValue() = value
}

object EndOfFileLeaf : Leaf(){
  override public fun getValue() = ""
}

class CompoundLeaf(
  val nonTerminal: NonTerminal, 
  val value: String
) : Leaf(){
  override public fun getValue() = value
}

class Ast(val root: Node)