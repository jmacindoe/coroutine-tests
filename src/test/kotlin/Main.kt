import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import kotlin.test.assertTrue

object MainTest : Spek({
    describe("testing stuff") {
        it("works") {
            assertTrue(true)
        }
    }
})