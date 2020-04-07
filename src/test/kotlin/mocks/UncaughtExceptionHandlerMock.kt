package mocks

class UncaughtExceptionHandlerMock {
    var thrown: Throwable? = null

    val handler = Thread.UncaughtExceptionHandler { _, t ->
        thrown = t
    }
}