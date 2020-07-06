package grammar

/**
 * Тут описана грамматика в расширенной форме Бэкуса - Наура
 */

/**
 * позволяет комбинировать различные выражения
 */
sealed class Expression

/**
 * символ - основной элемент грамматики. может быть 
 * терминалом (@see Terminal) и нетерминалом (@see NonTerminal).
 * специальный символ (@see SpecialSymbol)- аналог терминала, полезен во время разбора текста
 */
sealed class Symbol : Expression()

/**
 * Нетерминалы по сути описывают названия правил
 * В данных грамматиках в левой стороне правила может быть только нетерминал, 
 * т.к. грамматики эти контекстно- свободны
 */
data class NonTerminal(val name: String) : Symbol() {
  override fun toString(): String = name
}

/**
 * Терминалы- это символы из которых состоит алфавит языка, порождаемого грамматикой.
 * Терминалы не заменяются правилами
 */
data class Terminal(val value: String) : Symbol() {
  override fun toString(): String = "\'" + value.replace("\'", "\\\'") + "\'"
}

/**
 * Специальные символы - это такие терминалы, по которым нельзя сгенерировать текст, 
 * но которые позволяют текст разбирать
 */
class SpecialSymbol(val name: String, val filter: (Char) -> Boolean) : Symbol() {
  override fun toString(): String = name
  operator fun not(): SpecialSymbol = SpecialSymbol(name, { !filter(it) })
  operator fun plus(other: SpecialSymbol): SpecialSymbol =
    SpecialSymbol(name + other.name, { filter(it) || other.filter(it) })
}

/**
 * Последовательность - правило вида:
 * X = A B C ...
 */
data class Sequence(val parts: List<Expression>) : Expression() {
  constructor(vararg exprs: Expression) : this(exprs.toList())
  override fun toString(): String = parts.fold("", { acc, e -> acc + " " + e })
}

/**
 * Выбор варианта - правило вида
 * X = (A | B | C)
 */
data class Choise(val variants: List<Expression>) : Expression() {
  constructor(vararg exprs: Expression) : this(exprs.toList())
  override fun toString(): String = variants.fold("( ", { acc, e -> acc + e + " | " }).substringBeforeLast("|") + ")"
}

/**
 * Повторение - правило вида
 * X = { A }
 */
data class Repeat(val repeatable: Expression) : Expression() {
  override fun toString(): String = "{ " + repeatable + " }"
}

/**
 * Опциональность - 
 * X = [ A ]
 */
data class Maybe(val possible: Expression) : Expression() {
  override fun toString(): String = "[ " + possible + " ]"
}

/**
 * Грамматика  - набор правил вида X = ... 
 * Поскольку существует выражение выбора (@see Choise), 
 * символы с левой стороны правил могут не повторяться.
 * Это позволяет хранить правила в мапе
 */
data class Grammar(val rules: Map<NonTerminal, Expression>) {
  constructor(vararg pairs: Pair<NonTerminal, Expression>) : this(pairs.associateBy({ it.first }, { it.second }))
  override fun toString(): String = rules.entries.fold("", { acc, entry -> acc + entry.key + " = " + entry.value + "\n" })
}
