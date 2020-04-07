package errors

import OhNoException
import kotlinx.coroutines.*
import mocks.CoroutineExceptionHandlerMock
import mocks.UncaughtExceptionHandlerMock
import org.hamcrest.CoreMatchers.instanceOf
import org.junit.Assert.assertThat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import kotlin.test.assertFailsWith

object CoroutineErrorsTest : Spek({
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
    }
})

