// package formallang.parser

// import formallang.ast.*
// import formallang.grammar.*
// import formallang.metainf.*
// import automata.*
// import java.io.InputStreamReader

// class ParsingState: State{
//   public override fun getType()= TODO("State class")
  
//   companion object {
//     public fun transition(parsingState: ParsingState): Iterable<ParsingState>{
//       TODO("add transition")
//     }
//   }
// }

// class Parser private constructor(
//   stateMachieneFactory: StateMachieneFactory<ParsingState>,
//   val grammar: SimplifiedGrammar,
//   val mainSymbol: Symbol,
//   val processors: Iterable<AstProcessor>
// ) {
//   val stateMachiene : StateMachiene<ParsingState>
  
//   init {
//     stateMachiene = stateMachieneFactory.getMachiene(ParsingState::transition)
//   }

//   constructor(
//     stateMachieneFactory: StateMachieneFactory<ParsingState>,
//     extendedGrammar: Grammar,
//     mainSymbol: Symbol,
//     vararg processorsList: AstProcessor
//   ) : this(
//     stateMachieneFactory,  
//     SimplifiedGrammar.fromGrammar(extendedGrammar), 
//     mainSymbol, 
//     ArrayList<AstProcessor>()
//   ) {
//     val procList = (this.processors as MutableList)
//     procList.add(UnfoldNonTerminals(this.grammar.rules.keys - extendedGrammar.rules.keys))
//     procList.addAll(processorsList)
//   }

//   constructor(
//     stateMachieneFactory: StateMachieneFactory<ParsingState>,
//     simplifiedGrammar: SimplifiedGrammar,
//     mainSymbol: Symbol,
//     vararg processorsList: AstProcessor
//   ) : this(
//     stateMachieneFactory,
//     simplifiedGrammar, 
//     mainSymbol, 
//     ArrayList<AstProcessor>()
//   ) {
//     val procList = (this.processors as MutableList)
//     procList.addAll(processorsList)
//   }

//   private fun justParse(reader: InputStreamReader): Ast {
//     TODO("parse the text")
//   }

//   public fun parse(reader: InputStreamReader): Ast {
//     return processors.fold(justParse(reader)) {
//       acc, nextProc -> nextProc.process(acc)
//     }
//   }
// }
