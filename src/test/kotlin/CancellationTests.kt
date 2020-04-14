import junit.framework.Assert.fail
import kotlinx.coroutines.*
import org.hamcrest.CoreMatchers.instanceOf
import org.junit.Assert.assertThat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import kotlin.test.assertEquals

object CancellationTests : Spek({
    describe("Cancelling coroutines") {
        it("cancels all children") {
            val results = mutableListOf<Int>()

            runBlocking {
                GlobalScope.launch {
                    launch {
                        delay(200)
                        results.add(1)
                    }

                    results.add(2)
                    delay(100)
                    cancel()
                }.join()
            }

            assertEquals(listOf(2), results)
        }

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