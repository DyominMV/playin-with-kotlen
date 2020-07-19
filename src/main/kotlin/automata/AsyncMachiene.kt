package automata

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.*
import kotlinx.coroutines.sync.*

/**
 * Этот класс должен уметь распределять обработку состояний по корутинам
 * 
 * На самом деле, если бы у нас ВСЕГДА было успешное состояние в конце, можно было бы 
 * завести канал и всеми корутинами впихивать в него их (корутин) результат, а 
 * в runMachiene сделать ожидание навроде 
 *    while(channel.recieve()....getType() != SUCCESS)
 * 
 * А вот что делать в ситуации, когда работа может закончиться неуспешно - я не знаю.
 */
class AsyncMachiene<T : State> (
  val transition: suspend (T) -> Iterable<T>
) : StateMachiene<T> {

  override fun runMachiene(startState: T): T? = TODO("async machiene")
}

class AsyncMachieneFactory<T : State> : StateMachieneFactory<T> {
  public override fun getMachiene(transition: (T) -> Iterable<T>): StateMachiene<T> =
    AsyncMachiene({ transition(it) })
  public override fun getMachieneSuspend(transition: suspend (T) -> Iterable<T>): StateMachiene<T> =
    AsyncMachiene(transition)
}
