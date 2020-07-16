package formallang.grammar

class WrongTerminalException() : 
  Exception("Empty terminals are not allowed!")

class NoRuleFoundException(nonTerminal : NonTerminal):
  Exception("No rule found for non-terminal called $nonTerminal")

class LeftRecursionException(nonTerminal : NonTerminal, rule: SimplifiedRule):
  Exception("Left recursion found when checking rule:\n\t$nonTerminal = $rule")