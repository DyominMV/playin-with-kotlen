package ast

import grammar.*

sealed class Node

class Fork(
  val nonTerminal: NonTerminal,
  val rule: SimplifiedRule,
  val children: MutableList<Node>
) : Node()

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