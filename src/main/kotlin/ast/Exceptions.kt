package ast

class InvalidForkException(fork: Fork) : 
  Exception("""
    fork of nonTerminal ${fork.nonTerminal} with rule ${fork.rule} must contain 
    ${fork.rule.symbols.size} children, but contains only ${fork.children.size} children
  """)