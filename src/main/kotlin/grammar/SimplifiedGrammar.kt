package grammar

data class SimplifiedRule(val symbols: List<Symbol>) {
  override fun toString(): String = symbols.fold("", { acc, e -> acc + " " + e })
  constructor(vararg syms: Symbol) : this(syms.toList())
}

data class SimplifiedGrammar(val rules: Map<NonTerminal, List<SimplifiedRule>>) {
  constructor(vararg theRules: Pair<NonTerminal, List<SimplifiedRule>>) : this(theRules.associateBy({ it.first }, { it.second }))
  override fun toString(): String = rules.entries.fold("", { acc, entry -> "" + acc + entry.key + " = " + entry.value.fold("", { a, b -> a + "\n\t" + b }) })
  public fun toGrammar(): Grammar = Grammar(rules.mapValues { entry -> Choise(entry.value.map { Sequence(it.symbols) }) })
  
  companion object {
    public fun fromGrammar(grammar: Grammar) : SimplifiedGrammar = TODO("запилить таки упрощалку")
  }
  
}
