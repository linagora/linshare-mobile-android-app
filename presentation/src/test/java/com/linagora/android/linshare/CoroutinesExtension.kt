package com.linagora.android.linshare

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.extension.AfterAllCallback
import org.junit.jupiter.api.extension.BeforeAllCallback
import org.junit.jupiter.api.extension.ExtensionContext

class CoroutinesExtension(
    val testDispatcher: TestCoroutineDispatcher = TestCoroutineDispatcher()
) : BeforeAllCallback, AfterAllCallback {

    override fun beforeAll(context: ExtensionContext?) {
        Dispatchers.setMain(testDispatcher)
    }

    override fun afterAll(context: ExtensionContext?) {
        Dispatchers.resetMain()
        testDispatcher.cleanupTestCoroutines()
    }
}

fun CoroutinesExtension.runBlockingTest(block: suspend () -> Unit) = this.testDispatcher.runBlockingTest {
    block()
}
