package mocks

import kotlinx.coroutines.CoroutineExceptionHandler
import kotlin.coroutines.CoroutineContext

class CoroutineExceptionHandlerMock {
    var thrown: Throwable? = null

    val handler = CoroutineExceptionHandler { _: CoroutineContext, t: Throwable ->
        thrown = t
    }
}

