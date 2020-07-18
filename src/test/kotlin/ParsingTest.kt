import org.junit.Test
import org.junit.Assert.*
import formallang.grammar.*
import formallang.ast.*
import formallang.metainf.*
import formallang.parser.*
import automata.*

class ParsingTest{

  val digit = SpecialSymbol("digit") 
    { if (null == it) false else Character.isDigit(it) }
  val whitespace = SpecialSymbol("whitespace") 
    { if (null == it) false else Character.isWhitespace(it) }
  val plus = Terminal("+")
  val leftP = Terminal("(")
  val rightP = Terminal(")")
  val number = NonTerminal("Number")
  val numberRule = Sequence(digit, Repeat(digit))
  val spaces = NonTerminal("Spaces")
  val spacesRule = Repeat(whitespace)
  val expression = NonTerminal("expression")
  val expressionRule = Choise(
    number,
    Sequence(leftP, spaces, expression, spaces, 
      Repeat(Sequence(plus, spaces, expression)), spaces, rightP)
  ) // ( Number | '(' expression {'+' expression} ')'
  val mainSymbol = NonTerminal("Main")
  val mainRule = Sequence(expression, EndOfFile())

  val grammar = Grammar(
    number to numberRule,
    spaces to spacesRule,
    expression to expressionRule,
    mainSymbol to mainRule
  )

  @Test
  fun shouldNotThrowExceptions(){
    val parser = Parser(RegularMachieneFactory<ParsingState>(), grammar, mainSymbol, 
      RemoveEmpties, 
      StringifyNonTerminals(number)
    )
    val normalTree = parser.parse("((12+13) + 158   )".reader())
    assertNotNull(normalTree) 
    println(normalTree)
    val brokenTree = parser.parse("((12+13) + 158   ".reader())
    assertNull(brokenTree) 
    println(brokenTree)
  }
}