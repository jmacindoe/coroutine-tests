import junit.framework.Assert.fail
import kotlinx.coroutines.*
import org.hamcrest.CoreMatchers.instanceOf
import org.junit.Assert.assertThat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

object CancellationTests : Spek({
    describe("Cancelling coroutines") {
        describe("join()") {
            it("rethrows CancellationException") {
                runBlocking {
                    GlobalScope.launch {
                        val childJob = launch {
                            throw OhNoException()
                        }

                        try {
                            childJob.join()
                            fail()
                        } catch (e: CancellationException) {
                            assertThat(e.cause, instanceOf(OhNoException::class.java))
                        }
                    }.join()
                }
            }
        }
    }
})