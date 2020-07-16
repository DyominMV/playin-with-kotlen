package formallang.grammar

class WrongTerminalException() : 
  Exception("Empty terminals are not allowed!")

class NoRuleFoundException(nonTerminal : NonTerminal):
  Exception("No rule found for non-terminal called $nonTerminal")

class LeftRecursionException(nonTerminal : NonTerminal):
  Exception("Left recursion found when checking nonTerminal: $nonTerminal")