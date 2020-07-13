package formallang.metainf

import formallang.ast.*
import formallang.grammar.*

class StringifyNonTerminals(nonTerminals: Set<NonTerminal>) :
  TaggedNonTerminalSet(
    nonTerminals,
    {
      when (it) {
        is Leaf -> listOf(it)
        is Fork -> listOf(CompoundLeaf(it.nonTerminal, it.getString()))
      }
    },
    ProcessingDirection.ROOT_FIRST
  ) {
  constructor(vararg nonTerminalList: NonTerminal) : this(hashSetOf<NonTerminal>()) {
    (nonTerminals as MutableSet<NonTerminal>).addAll(nonTerminalList)
  }
}

class UnfoldNonTerminals(nonTerminals: Set<NonTerminal>) :
  TaggedNonTerminalSet(
    nonTerminals,
    {
      when (it) {
        is Leaf -> listOf(it)
        is Fork -> it.children
      }
    },
    ProcessingDirection.ROOT_LAST
  ) {
  constructor(vararg nonTerminalList: NonTerminal) : this(hashSetOf<NonTerminal>()) {
    (nonTerminals as MutableSet<NonTerminal>).addAll(nonTerminalList)
  }
}
