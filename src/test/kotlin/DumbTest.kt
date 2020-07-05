import org.junit.Test
import org.junit.Assert.assertTrue
import kotlinx.coroutines.*

class DumbTest {
  @Test
  fun dumbTest() {
    val time1 : Long = kotlin.system.measureTimeMillis{
      val dumbMachiene = DumbMachiene<DumbState>( {it.transition()}, DumbState::isFinite)
      assertTrue(dumbMachiene.runMachiene(DumbState.STATE1) == DumbState.STATE5)
    }

    val time2 : Long = kotlin.system.measureTimeMillis{
      val asyncMachiene = AsyncMachiene<DumbState>( {it.transition()}, DumbState::isFinite)
      assertTrue(asyncMachiene.runMachiene(DumbState.STATE1) == DumbState.STATE5)
    }

    println(time1)
    println(time2)
    assertTrue(time1 > time2)
  }
}
