package com.linagora.android.linshare.domain.usecases.quota

import com.google.common.truth.Truth.assertThat
import com.linagora.android.linshare.domain.DomainFixtures.DOCUMENT_REQUEST
import com.linagora.android.linshare.domain.DomainFixtures.DOCUMENT_REQUEST_BIG_SIZE
import com.linagora.android.linshare.domain.repository.user.QuotaRepository
import com.linagora.android.testshared.TestFixtures.Accounts.LOW_QUOTA
import com.linagora.android.testshared.TestFixtures.Accounts.QUOTA
import com.linagora.android.testshared.TestFixtures.Accounts.QUOTA_UUID
import com.linagora.android.testshared.TestFixtures.State.EXCEED_MAX_FILE_SIZE
import com.linagora.android.testshared.TestFixtures.State.INIT_STATE
import com.linagora.android.testshared.TestFixtures.State.LOADING_STATE
import com.linagora.android.testshared.TestFixtures.State.QUOTA_ACCOUNT_NO_MORE_AVAILABLE_SPACE
import com.linagora.android.testshared.TestFixtures.State.VALID_QUOTA_ACCOUNT_STATE
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runBlockingTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations

class EnoughQuotaToUploadInteractorTest {

    @Mock
    lateinit var quotaRepository: QuotaRepository

    private lateinit var enoughQuotaToUploadInteractor: EnoughQuotaToUploadInteractor

    @BeforeEach
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        enoughQuotaToUploadInteractor = EnoughQuotaToUploadInteractor(quotaRepository)
    }

    @Test
    fun enoughQuotaShouldReturnValidAccountQuotaWhenEnoughQuotaForDocument() {
        runBlockingTest {
            Mockito.`when`(quotaRepository.findQuota(QUOTA_UUID))
                .thenAnswer { QUOTA }

            val states = enoughQuotaToUploadInteractor(QUOTA_UUID, DOCUMENT_REQUEST)
                .toList(ArrayList())

            assertThat(states).hasSize(2)
            assertThat(states[0](INIT_STATE))
                .isEqualTo(LOADING_STATE)

            assertThat(states[1](LOADING_STATE))
                .isEqualTo(VALID_QUOTA_ACCOUNT_STATE)
        }
    }

    @Test
    fun enoughQuotaShouldReturnExceedMaxFileSizeWhenDocumentSizeExceed() {
        runBlockingTest {
            Mockito.`when`(quotaRepository.findQuota(QUOTA_UUID))
                .thenAnswer { LOW_QUOTA }

            val states = enoughQuotaToUploadInteractor(QUOTA_UUID, DOCUMENT_REQUEST_BIG_SIZE)
                .toList(ArrayList())

            assertThat(states).hasSize(2)
            assertThat(states[0](INIT_STATE))
                .isEqualTo(LOADING_STATE)

            assertThat(states[1](LOADING_STATE))
                .isEqualTo(EXCEED_MAX_FILE_SIZE)
        }
    }

    @Test
    fun enoughQuotaShouldReturnNotEnoughAccountQuotaWhenNoMoreSpaceAvailable() {
        runBlockingTest {
            Mockito.`when`(quotaRepository.findQuota(QUOTA_UUID))
                .thenAnswer { QUOTA }

            val states = enoughQuotaToUploadInteractor(QUOTA_UUID, DOCUMENT_REQUEST_BIG_SIZE)
                .toList(ArrayList())

            assertThat(states).hasSize(2)
            assertThat(states[0](INIT_STATE))
                .isEqualTo(LOADING_STATE)

            assertThat(states[1](LOADING_STATE))
                .isEqualTo(QUOTA_ACCOUNT_NO_MORE_AVAILABLE_SPACE)
        }
    }
}
