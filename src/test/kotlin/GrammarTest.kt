import org.junit.Test
import org.junit.Assert.*
import grammar.*

class GrammarTest{
  @Test
  fun grammarTest(){
    val a =  NonTerminal("a")
    val b =  NonTerminal("b")
    val c =  NonTerminal("c")
    val A =  Terminal("a")
    val B =  Terminal("b")
    val C =  Terminal("c")
    val grammar = Grammar(
      a to Sequence(A,b,c),
      b to Choise(Sequence(B,c), Sequence(c,B), Repeat(B)),
      c to Sequence(C, Maybe(a))
    )    
    println(grammar)
    println( kotlin.system.measureTimeMillis {
      val simplifiedGrammar = SimplifiedGrammar.fromGrammar(grammar)
      println(simplifiedGrammar)
    })
  }
}