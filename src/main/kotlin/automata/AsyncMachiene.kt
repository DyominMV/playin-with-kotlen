package automata

import kotlinx.coroutines.*
import java.util.Collections
import java.util.concurrent.ConcurrentHashMap

/**
 * Очень простецкий недетерминированный конечный автомат С КОРУТИНКАМИ
 * при этом конечное состояние возвращается только одно.
 * <tt>ТАК НАДО</tt> 
 * По сути то же что @see DumbMachiene , но при этом немного сложнее (параллельность, ууу)
 * Бутылочное горлышко на каждом шаге, но зато если граф функции перехода будет 
 * каких-то кошмарных размеров, мб получим выигрыш в скорости
 */
class AsyncMachiene<T> (
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

  /**
   * Запустить автомат и выдать первое достигнутое конечное состояние
   */
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
