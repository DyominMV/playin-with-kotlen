package grammar

/**
 * Генерирует тексты по грамматикам. 
 * !!! специальные символы (@see SpecialSymbol) порождают пустую строку !!! 
 */
class TextGenerator(val grammar: Grammar, val repeatBound: Int) {
  private val random = java.util.Random()

  fun generateText(start: Expression): String = when (start) {
    is Terminal -> start.value
    is NonTerminal -> generateText(grammar.rules.get(start)!!)
    is Maybe -> if (random.nextBoolean()) generateText(start.possible) else ""
    is Sequence -> start.parts.fold("", {acc, e -> acc + generateText(e)})
    is Repeat -> Array(random.nextInt(repeatBound), {generateText(start.repeatable)}).fold("", String::plus)
    is Choise -> generateText(start.variants.get(random.nextInt(start.variants.size)))
    else -> "" // нельзя сгенерировать текст по специальному символу  @see SpecialSymbol
  }
}
