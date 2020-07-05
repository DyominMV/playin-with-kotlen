import kotlinx.coroutines.*
import java.util.Collections
import java.util.concurrent.ConcurrentHashMap

class AsyncMachiene<T : kotlin.Enum<T>> (
  val transition: (T) -> List<T>,
  val isFinite: (T) -> Boolean
) {

  private suspend fun runTransition(state: T, nextStates : MutableSet<T>): T? {
    val transitioned = transition(state)
    val finites = transitioned.filter(isFinite)
    if (finites.size > 0) return finites.get(0)
    nextStates.addAll(transitioned)
    return null
  }
  
  private fun newConcurrentSet(): MutableSet<T> = Collections.newSetFromMap(ConcurrentHashMap<T,Boolean>())

  public fun runMachiene(startState: T): T {
    var nextStates = newConcurrentSet()
    var currentStates = newConcurrentSet()
    currentStates.add(startState)
    while (true) {
      val finiteStates = ArrayList<Deferred<T?>>()
      for (state in currentStates) { 
        finiteStates.add(GlobalScope.async{ runTransition(state, nextStates)})
      }
      var nonNulls : List<T?>
      runBlocking{
        nonNulls = finiteStates.map({it.await()}).filter{it != null} 
      }
      if (nonNulls.size >0) return nonNulls.get(0)!!
      currentStates = nextStates
      nextStates = newConcurrentSet()
    }
  }
}
