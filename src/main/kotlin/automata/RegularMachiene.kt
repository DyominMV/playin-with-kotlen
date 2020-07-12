package automata

import kotlinx.coroutines.*

class RegularMachiene<T : State>(
  transition: (T) -> Iterable<T>
) : StateMachiene<T>(transition) {

  private var currentStates: List<T> = ArrayList<T>()

  private fun stepTransitions(): State.StateType {
    currentStates = currentStates.map(transition).fold(ArrayList<T>(),{
      acc, newList -> 
      acc.addAll(newList)
      acc
    }).filter({it.getType() != State.StateType.FAIL})
    if (currentStates.size == 0) 
      return State.StateType.FAIL
    var possibleState = currentStates.find {it.getType() == State.StateType.SUCCESS}
    if (possibleState != null){
      currentStates = listOf(possibleState)
      return State.StateType.SUCCESS
    }
    currentStates.filter {it.getType() == State.StateType.UNFINISHED}
    return State.StateType.UNFINISHED
  }

  override fun runMachiene(startState: T): T? {
    currentStates = arrayListOf(startState)
    var currentStateType = startState.getType()
    while (State.StateType.UNFINISHED == currentStateType){
      currentStateType = stepTransitions()
    }
    return when (currentStateType){
      State.StateType.SUCCESS -> currentStates.get(0)
      else ->  null
    }
  }
}
