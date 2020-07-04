import org.junit.Test
import org.junit.Assert.assertTrue

class DumbTest {
	@Test
	fun dumbTest() {
		val dumbMachiene = DumbMachiene<DumbState>( {it.transition()}, DumbState::isFinite)
		assertTrue(dumbMachiene.runMachiene(DumbState.STATE1) == DumbState.STATE5)
	}
}
