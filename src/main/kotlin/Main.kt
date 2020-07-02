fun main(args: Array<String>) {
  val grammar = Grammar(
    NonTerminal("a") to Choise(NonTerminal("a"), Terminal("b"), Repeat(Terminal("B"))),
    NonTerminal("b") to Choise(NonTerminal("b"), Terminal("b"), Repeat(Terminal("B"))),
    NonTerminal("c") to Choise(NonTerminal("c"), Terminal("b"), Repeat(Terminal("B")))
  )
  println(TextGenerator(grammar, 4).generateText(Sequence(NonTerminal("a"), NonTerminal("b"), NonTerminal("c"))))
}

class TextGenerator(val grammar: Grammar, val repeatBound: Int) {
  private val random = java.util.Random()

  fun generateText(start: Expression): String = when (start) {
    is Terminal -> start.value
    is NonTerminal -> generateText(grammar.rules.get(start)!!)
    is Maybe -> if (random.nextBoolean()) generateText(start.possible) else ""
    is Sequence -> start.parts.fold("", {acc, e -> acc + generateText(e)})
    is Repeat -> Array(random.nextInt(repeatBound), {generateText(start.repeatable)}).fold("", String::plus)
    is Choise -> generateText(start.variants.get(random.nextInt(start.variants.size)))
    else -> "" 
  }
}
