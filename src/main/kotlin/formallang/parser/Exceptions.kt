package formallang.parser

class InvalidTreeException: 
  Exception("used ForkableTree cannot be transformed to AST")