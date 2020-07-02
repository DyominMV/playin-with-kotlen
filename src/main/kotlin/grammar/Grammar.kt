package grammar

interface Expression

interface Symbol : Expression

data class NonTerminal(val name: String): Symbol{
  override fun toString(): String = name
}

data class Terminal(val value: String): Symbol{
  override fun toString(): String = "\'"+ value.replace("\'", "\\\'") + "\'"
}

data class Sequence(val parts: List<Expression>): Expression{
  constructor(vararg exprs: Expression) : this(exprs.toList())
  override fun toString(): String = parts.fold("", {acc, e -> acc + " " + e})
}

data class Choise(val variants: List<Expression>): Expression{
  constructor(vararg exprs: Expression) : this(exprs.toList())
  override fun toString(): String = variants.fold("( ", {acc, e -> acc + e + " | "}).substringBeforeLast("|") + ")"
}

data class Repeat(val repeatable: Expression): Expression{
  override fun toString(): String = "{ " + repeatable + " }"
}

data class Maybe(val possible: Expression): Expression{  
  override fun toString(): String = "[ " + possible + " ]"
}

data class Grammar(val rules: Map<NonTerminal, Expression>){
  constructor(vararg pairs: Pair<NonTerminal, Expression>) : this(pairs.associateBy({it.first}, {it.second})) 
  override fun toString(): String = rules.entries.fold("", {acc, entry -> acc + entry.key + " = " + entry.value + "\n"})
}
