package formallang.grammar

import java.util.concurrent.ConcurrentHashMap
import kotlinx.coroutines.*

 /**
  * упрощённое правило состояит только из символов (то есть не включает выражения)
  */
data class SimplifiedRule(val symbols: List<Symbol>) {
  override fun toString(): String =
    if (symbols.size> 0) symbols.fold("", { acc, e -> acc + e + " " }) else "_"
  constructor(vararg syms: Symbol) : this(syms.toList())
  /*пустое правило обчно называется эпсилон-правилом */
  fun isEpsilon(): Boolean = symbols.size == 0
}

/**
 * экономим буквы
 */
typealias RulesMap = MutableMap<NonTerminal, ArrayList<SimplifiedRule>>

/**
 * поскольку в упрощённой грамматике нет выражений, все правила сгруппированы по левой части в списки
 */
data class SimplifiedGrammar(val rules: Map<NonTerminal, List<SimplifiedRule>>) {
  constructor(vararg theRules: Pair<NonTerminal, List<SimplifiedRule>>) :
    this(theRules.associateBy({ it.first }, { it.second }))
  override fun toString(): String =
    rules.entries.fold("", { acc, entry ->
        val rightRule = entry.value.fold("", { a, b -> "$a\n\t$b" })
        "$acc\n${entry.key} = $rightRule"
      })
  public fun toGrammar(): Grammar = 
    Grammar(rules.mapValues { entry -> 
      Choise(entry.value.map { Sequence(it.symbols) }) 
    })
 
  private val epsilons: Set<NonTerminal> = HashSet()  
  private val firstChars: Map<SimplifiedRule, FirstCharacter> = HashMap()

  init {
    TODO("Find Epsilons")
    TODO("Find first chars")
    
  }

  public fun getPossibleRules(nonTerminal: NonTerminal, character: Char): List<SimplifiedRule>  = (
    (rules.get(nonTerminal)?.filter{ 
        firstChars.get(it)?.suitable(character) ?: false 
      } ?: emptyList()) +  (if (epsilons.contains(nonTerminal)) listOf(SimplifiedRule()) else emptyList())
  )

  companion object {
    /* получить список правил для указанного нетерминала */
    private fun getOrCreateList(nonTerminal: NonTerminal, targetRules: RulesMap): ArrayList<SimplifiedRule> =
        targetRules.get(nonTerminal) ?: {
        val newList = ArrayList<SimplifiedRule>()
        targetRules.put(nonTerminal, newList)
        newList
        }()

    /* добавить правиол в указанную мапу */
    private fun addRule(rule: SimplifiedRule, nonTerminal: NonTerminal, targetRules: RulesMap) =
      getOrCreateList(nonTerminal, targetRules).add(rule)

    /* обработать обычное правило, превратив его в набор упрощённых */
    private fun processRule(nonTerminal: NonTerminal, rule: Expression, targetRules: RulesMap) {
      when (rule) {
        is Symbol -> addRule(SimplifiedRule(rule), nonTerminal, targetRules)
        is Choise -> {
          var i = 0
          for (variant in rule.variants) {
            if (variant is Repeat || variant is Maybe) {
              val newNonTerminal = NonTerminal(nonTerminal.name + "_" + i)
              i += 1
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
          val newSymbolsList: MutableList<Symbol> = ArrayList<Symbol>()
          var i = 0
          for (expr in rule.parts) {
            if (expr is Symbol)
            newSymbolsList.add(expr)
            else {
              val newNonTerminal = NonTerminal(nonTerminal.name + "_" + i)
              i += 1
              processRule(newNonTerminal, expr, targetRules)
              newSymbolsList.add(newNonTerminal)
            }
          }
          addRule(SimplifiedRule(newSymbolsList), nonTerminal, targetRules)
        }
      }
    }

    /**
     * Построить упрощённую грамматику из расширенной
     */
    public fun fromGrammar(grammar: Grammar): SimplifiedGrammar {
      val targetRules = ConcurrentHashMap<NonTerminal, ArrayList<SimplifiedRule>>()
      val jobList = ArrayList<Job>()
      for ((nonTerminal, rule) in grammar.rules) {
        jobList.add(GlobalScope.launch {
          processRule(nonTerminal, rule, targetRules)
        })
      }
      runBlocking {
        for (job in jobList)
          job.join()
      }
      return SimplifiedGrammar(targetRules)
    }
  }
}
