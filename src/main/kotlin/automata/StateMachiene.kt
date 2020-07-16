package automata

interface State{
  public enum class StateType{
    UNFINISHED, FAIL, SUCCESS
  }
  public fun getType(): StateType
}

interface StateMachiene<T: State> {
  public fun runMachiene(startState: T): T? 
}

interface StateMachieneFactory<T:State>{
  public fun getMachiene(transition: (T) -> Iterable<T>) : StateMachiene<T>
}