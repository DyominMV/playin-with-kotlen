import grammar.*
import kotlinx.coroutines.*

class Main {
  companion object {
    @JvmStatic public fun main(args: Array<String>) {
      val a = NonTerminal("a")
      val b = NonTerminal("b")
      val c = NonTerminal("c")
      val A = Terminal("a")
      val B = Terminal("b")
      val C = Terminal("c")
      val grammar = Grammar(
        a to Sequence(A, b, c),
        b to Choise(Sequence(B, c), Sequence(c, B), Repeat(B)),
        c to Sequence(C, Maybe(a))
      )
      println(grammar)
      val simplifiedGrammar = SimplifiedGrammar.fromGrammar(grammar)
      println(simplifiedGrammar)
    }
  }
}
