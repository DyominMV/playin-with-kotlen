package automata

interface State{
  enum class StateType{
    UNFINISHED, FAIL, SUCCESS
  }
  public fun getType(): StateType
}

abstract class StateMachiene<T: State> (val transition: (T) -> Iterable<T>) {
  abstract public fun runMachiene(startState: T): T? 
}