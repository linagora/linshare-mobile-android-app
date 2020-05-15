package com.linagora.android.linshare.data.network

import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.test.runBlockingTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.MockitoAnnotations

class NetworkExecutorTest {

    private lateinit var networkExecutor: NetworkExecutor

    private fun fakeRequest(value: Int): Int {
        require(value < 5) { "invalid value" }
        return value
    }

    @BeforeEach
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        networkExecutor = NetworkExecutor()
    }

    @Test
    fun executeShouldReturnASuccessRequest() = runBlockingTest {
        assertThat(networkExecutor.execute(
                networkRequest = { fakeRequest(3) },
                onFailure = { if (it is IllegalArgumentException) throw RuntimeException("failed request") }))
            .isEqualTo(3)
    }

    @Test
    fun executeShouldThrownAnExceptionOnFailure() {
        assertThrows<IllegalArgumentException> { runBlockingTest {
            networkExecutor.execute(networkRequest = { fakeRequest(7) })
        } }
    }

    @Test
    fun executeShouldThrownAnExceptionWhenThrowExceptionOnFailure() {
        assertThrows<RuntimeException> { runBlockingTest {
            networkExecutor.execute(
                networkRequest = { fakeRequest(7) },
                onFailure = { if (it is IllegalArgumentException) throw RuntimeException("failed request") }
            ) }
        }
    }
}
