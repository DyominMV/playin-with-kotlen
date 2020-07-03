package grammar

data class SimplifiedRule(val symbols: List<Symbol>) {
  override fun toString(): String = symbols.fold("", { acc, e -> acc + " " + e })
  constructor(vararg syms: Symbol) : this(syms.toList())
}

data class SimplifiedGrammar(val rules: Map<NonTerminal, List<SimplifiedRule>>) {
  constructor(vararg theRules: Pair<NonTerminal, List<SimplifiedRule>>) : this(theRules.associateBy({ it.first }, { it.second }))
  override fun toString(): String = rules.entries.fold("", { acc, entry -> "" + acc + entry.key + " = " + entry.value.fold("", { a, b -> a + "\n\t" + b }) })
  public fun toGrammar(): Grammar = Grammar(rules.mapValues { entry -> Choise(entry.value.map { Sequence(it.symbols) }) })

  private static fun putRule(resultMap: MutableMap<NonTerminal, ArrayList<SimplifiedRule>>, nonTerminal: NonTerminal, rule: SimplifiedRule){
    val list = resultMap.get(nonTerminal) ?: resultMap.put(nonTerminal, ArrayList<>())
    list!!.add(rule)
  }

  private static fun processRule(resultMap: MutableMap<NonTerminal, ArrayList<SimplifiedRule>>, nonTerminal : NonTerminal, expression: Expression){
    when (expression){
      is Symbol -> putRule(resultMap, nonTerminal, SimplifiedRule(expression))
      is Sequence -> TODO("Очень сложно!!!!")
      is Maybe -> TODO("Очень сложно!!!!")
      is Repeat -> TODO("Очень сложно!!!!")
      is Choise -> TODO("Очень сложно!!!!")
      else -> TODO("Очень сложно!!!!")
    }
  }

  public static fun fromGrammar(grammar: Grammar): SimplifiedGrammar{
    val rules = java.util.concurrent.ConcurrentHashMap<NonTerminal, ArrayList<SimplifiedRule>>()
    for ((nonTerminal, expression) in grammar.rules){
      processRule(rules, nonTerminal, expression)
    }
    return SimplifiedGrammar(rules)
  }
}
