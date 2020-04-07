import kotlinx.coroutines.*
import mocks.CoroutineExceptionHandlerMock
import mocks.UncaughtExceptionHandlerMock
import org.hamcrest.CoreMatchers.instanceOf
import org.junit.Assert.assertThat
import org.junit.Assert.fail
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import kotlin.test.assertFailsWith

object CoroutineErrorTest : Spek({
    describe("Errors thrown during coroutine execution") {
        describe("runBlocking") {
            it("propagates thrown exceptions") {
                assertFailsWith<OhNoException> {
                    runBlocking<Unit> {
                        throw OhNoException()
                    }
                }
            }
        }

        describe("launch") {
            it("uses CoroutineExceptionHandler in context for unhandled exceptions") {
                val mock = CoroutineExceptionHandlerMock()
                runBlocking {
                    (GlobalScope + mock.handler).launch {
                        throw OhNoException()
                    }.join()
                }
                assertThat(mock.thrown, instanceOf(OhNoException::class.java))
            }

            it("uses Thread UncaughtExceptionHandler if no handler in context") {
                val mock = UncaughtExceptionHandlerMock()
                Thread.setDefaultUncaughtExceptionHandler(mock.handler)
                runBlocking {
                    GlobalScope.launch {
                        throw OhNoException()
                    }.join()
                }
                assertThat(mock.thrown, instanceOf(OhNoException::class.java))
                Thread.setDefaultUncaughtExceptionHandler(null)
            }

            it("uses exception handler inside inner launch") {
                val mock = CoroutineExceptionHandlerMock()
                runBlocking {
                    GlobalScope.launch {
                        (GlobalScope + mock.handler).launch {
                            throw OhNoException()
                        }.join()
                    }.join()
                }
                assertThat(mock.thrown, instanceOf(OhNoException::class.java))
            }
        }

        describe("async") {
            it("throws errors when calling await") {
                runBlocking {
                    val deferred: Deferred<String> = GlobalScope.async {
                        throw OhNoException()
                    }

                    try {
                        deferred.await()
                        fail()
                    } catch (e: OhNoException) {
                        // Test passes
                    }
                }
            }

            it("doesn't throw the error if we don't call await") {
                runBlocking {
                    val deferred: Deferred<String> = GlobalScope.async {
                        throw OhNoException()
                    }

                    deferred.join()
                }
            }

            it("propagates failure to a parent, even if await isn't called") {
                val mock = CoroutineExceptionHandlerMock()
                runBlocking {
                    GlobalScope.launch(mock.handler) {
                        async {
                            throw OhNoException()
                        }
                    }.join()
                }
                assertThat(mock.thrown, instanceOf(OhNoException::class.java))
            }

        }
    }
})

