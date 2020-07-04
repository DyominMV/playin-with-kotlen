class DumbMachiene<T : kotlin.Enum<T>> (
  val transition: (T) -> List<T>,
  val isFinite: (T) -> Boolean
){
  public fun runMachiene(startState: T): T{
    var currentStates  = HashSet<T>()
    currentStates.add(startState)
    var nextStates = HashSet<T>()
    while (true){
      for (state in currentStates){    
        val transitioned = transition(state)
        val finites = transitioned.filter(isFinite)
        if (finites.size > 0) return finites.get(0)
        nextStates.addAll(transitioned)
      }
      currentStates = nextStates
      nextStates = HashSet<T>()
    }
  } 
}
