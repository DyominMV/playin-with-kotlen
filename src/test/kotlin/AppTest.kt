import org.junit.Test
import org.junit.Assert.assertTrue

class AppTest {
	@Test
	fun yourTest() {
		val dumbMachiene = DumbMachiene<DumbState>(::dumbTransition, DumbState::isFinite)
		assertTrue(dumbMachiene.runMachiene(DumbState.STATE1) == DumbState.STATE5)

	}
}
