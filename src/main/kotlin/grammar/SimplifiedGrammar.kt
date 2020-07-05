package grammar

data class SimplifiedRule(val symbols: List<Symbol>) {
  override fun toString(): String =
    if (symbols.size> 0) symbols.fold("", { acc, e -> acc + " " + e }) else "_"
  constructor(vararg syms: Symbol) : this(syms.toList())
  fun isEpsilon(): Boolean = symbols.size == 0
}

typealias RulesMap = HashMap<NonTerminal, ArrayList<SimplifiedRule>>

data class SimplifiedGrammar(val rules: Map<NonTerminal, List<SimplifiedRule>>) {
  constructor(vararg theRules: Pair<NonTerminal, List<SimplifiedRule>>) : this(theRules.associateBy({ it.first }, { it.second }))
  override fun toString(): String = rules.entries.fold("", { acc, entry -> "" + acc + "\n" + entry.key + " = " + entry.value.fold("", { a, b -> a + "\n\t" + b }) })
  public fun toGrammar(): Grammar = Grammar(rules.mapValues { entry -> Choise(entry.value.map { Sequence(it.symbols) }) })

  companion object {
    private fun getList(nonTerminal: NonTerminal, targetRules: RulesMap): ArrayList<SimplifiedRule> =
        targetRules.get(nonTerminal) ?: {
        val newList = ArrayList<SimplifiedRule>()
        targetRules.put(nonTerminal, newList)
        newList
        }()

    private fun addRule(rule: SimplifiedRule, nonTerminal: NonTerminal, targetRules: RulesMap) =
      getList(nonTerminal, targetRules).add(rule)

    private fun processRule(nonTerminal: NonTerminal, rule: Expression, targetRules: RulesMap) {
      when (rule) {
        is Symbol -> addRule(SimplifiedRule(rule), nonTerminal, targetRules)
        is Choise -> {
          var i = 0
          for (variant in rule.variants) {
            i += 1
            if (variant is Repeat || variant is Maybe) {
                val newNonTerminal = NonTerminal(nonTerminal.name + "_" + i)
                processRule(newNonTerminal, variant, targetRules)
                addRule(SimplifiedRule(newNonTerminal), nonTerminal, targetRules)
            } else processRule(nonTerminal, variant, targetRules)
          }
        }
        is Repeat -> {
          processRule(nonTerminal, Sequence(rule.repeatable, nonTerminal), targetRules)
          addRule(SimplifiedRule(), nonTerminal, targetRules)
        }
        is Maybe -> {
          processRule(nonTerminal, rule.possible, targetRules)
          addRule(SimplifiedRule(), nonTerminal, targetRules)
        }
        is Sequence -> {
          val newRuleList: MutableList<Symbol> = ArrayList<Symbol>()
          var i = 0
          for (expr in rule.parts) {
            i += 1
            when (expr) {
              is Symbol -> newRuleList.add(expr)
              else -> {
                val newNonTerminal = NonTerminal(nonTerminal.name + "_" + i)
                processRule(newNonTerminal, expr, targetRules)
                newRuleList.add(newNonTerminal)
              }
            }
          }
          addRule(SimplifiedRule(newRuleList), nonTerminal, targetRules)
        }
      }
    }

    public fun fromGrammar(grammar: Grammar): SimplifiedGrammar {
      val targetRules = HashMap<NonTerminal, ArrayList<SimplifiedRule>>()
      for ((nonTerminal, rule) in grammar.rules) {
        processRule(nonTerminal, rule, targetRules)
      }
      return SimplifiedGrammar(targetRules)
    }
  }
}
