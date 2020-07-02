fun main(args: Array<String>) {
  val grammar = Grammar(
    NonTerminal("a") to Choise(NonTerminal("a"), Terminal("b"), Repeat(Terminal("B"))),
    NonTerminal("b") to Choise(NonTerminal("b"), Terminal("b"), Repeat(Terminal("B"))),
    NonTerminal("c") to Choise(NonTerminal("c"), Terminal("b"), Repeat(Terminal("B")))
  )
  println(TextGenerator(grammar, 4).generateText(Sequence(NonTerminal("a"), NonTerminal("b"), NonTerminal("c"))))
}
