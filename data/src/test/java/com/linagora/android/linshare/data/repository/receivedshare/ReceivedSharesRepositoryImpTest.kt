package com.linagora.android.linshare.data.repository.receivedshare

import com.google.common.truth.Truth.assertThat
import com.linagora.android.linshare.data.datasource.ReceivedShareDataSource
import com.linagora.android.linshare.data.repository.share.ReceivedSharesRepositoryImp
import com.linagora.android.linshare.domain.model.share.Share
import com.linagora.android.testshared.ShareFixtures.SHARE_1
import com.linagora.android.testshared.ShareFixtures.SHARE_2
import kotlinx.coroutines.test.runBlockingTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations

class ReceivedSharesRepositoryImpTest {

    @Mock
    lateinit var receivedShareDataSource: ReceivedShareDataSource

    private lateinit var sharesRepositoryImp: ReceivedSharesRepositoryImp

    @BeforeEach
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        sharesRepositoryImp = ReceivedSharesRepositoryImp(receivedShareDataSource)
    }

    @Test
    fun getAllShouldReturnReceivedList() {
        runBlockingTest {
            `when`(receivedShareDataSource.getReceivedShares())
                .thenAnswer { listOf(SHARE_1, SHARE_2) }

            val receivedList = sharesRepositoryImp.getReceivedShares()
            assertThat(receivedList).containsExactly(SHARE_1, SHARE_2)
        }
    }

    @Test
    fun getAllShouldReturnEmptyListWhenNoReceivedListExist() {
        runBlockingTest {
            `when`(receivedShareDataSource.getReceivedShares())
                .thenAnswer { emptyList<Share>() }

            val receivedList = sharesRepositoryImp.getReceivedShares()
            assertThat(receivedList).isEmpty()
        }
    }
}
