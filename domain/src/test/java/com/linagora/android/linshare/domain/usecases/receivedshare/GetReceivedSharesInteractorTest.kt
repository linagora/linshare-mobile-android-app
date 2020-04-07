package com.linagora.android.linshare.domain.usecases.receivedshare

import arrow.core.Either
import com.google.common.truth.Truth.assertThat
import com.linagora.android.linshare.domain.repository.share.ReceivedShareRepository
import com.linagora.android.testshared.ShareFixtures.ALL_RECEIVED_STATE
import com.linagora.android.testshared.ShareFixtures.SHARE_1
import com.linagora.android.testshared.ShareFixtures.SHARE_2
import com.linagora.android.testshared.TestFixtures.State.INIT_STATE
import com.linagora.android.testshared.TestFixtures.State.LOADING_STATE
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runBlockingTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations

class GetReceivedSharesInteractorTest {

    @Mock
    private lateinit var receivedRepository: ReceivedShareRepository

    private lateinit var getReceivedSharesInteractor: GetReceivedSharesInteractor

    @BeforeEach
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        getReceivedSharesInteractor = GetReceivedSharesInteractor(receivedRepository)
    }

    @Test
    fun getReceivedListShouldSuccessWithReceivedList() {
        runBlockingTest {
            `when`(receivedRepository.getReceivedShares())
                .thenAnswer { listOf(SHARE_1, SHARE_2) }

            assertThat(getReceivedSharesInteractor()
                .map { it(INIT_STATE) }
                .toList(ArrayList()))
                .containsExactly(LOADING_STATE, ALL_RECEIVED_STATE)
        }
    }

    @Test
    fun getReceivedListShouldFailedWhenGetReceivedListFailed() {
        runBlockingTest {
            val exception = RuntimeException("get list received failed")

            `when`(receivedRepository.getReceivedShares())
                .thenThrow(exception)

            assertThat(getReceivedSharesInteractor()
                .map { it(INIT_STATE) }
                .toList(ArrayList()))
                .containsExactly(LOADING_STATE, Either.Left(ReceivedSharesFailure(exception)))
        }
    }
}
