import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.runBlocking
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import kotlin.test.assertEquals

object FlowTests : Spek({
    describe("flow") {
        it("suspends on collect()") {
            val results = mutableListOf<Int>()

            runBlocking {
                flow {
                    emit(1)
                    delay(10)
                    emit(2)
                }
                    .collect { results.add(it) }

                results.add(3)
            }

            assertEquals(listOf(1, 2, 3), results)
        }

        it("doesn't suspend when using launchIn()") {
            val results = mutableListOf<Int>()

            runBlocking {
                flow {
                    emit(1)
                    delay(10)
                    emit(2)
                }
                    .onEach { results.add(it) }
                    .launchIn(this)

                results.add(3)
            }

            assertEquals(listOf(3, 1, 2), results)
        }

        it("stops upstream after failure") {
            runBlocking {
                val results = flow {
                    emit("1")
                    emit("2")
                    emit("3")
                }
                    .onEach { if (it == "2") throw OhNoException() }
                    .catch { emit("got an exception") }
                    .toList()

                assertEquals(listOf("1", "got an exception"), results)
            }
        }
    }
})