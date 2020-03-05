package com.linagora.android.linshare.domain.usecases.quota

import android.os.Build
import com.google.common.truth.Truth.assertThat
import com.linagora.android.linshare.domain.DomainFixtures.DOCUMENT_REQUEST
import com.linagora.android.linshare.domain.DomainFixtures.DOCUMENT_REQUEST_BIG_SIZE
import com.linagora.android.linshare.domain.repository.user.QuotaRepository
import com.linagora.android.linshare.domain.repository.user.UserRepository
import com.linagora.android.testshared.TestFixtures.Accounts.LINSHARE_USER
import com.linagora.android.testshared.TestFixtures.Accounts.LOW_QUOTA
import com.linagora.android.testshared.TestFixtures.Accounts.QUOTA
import com.linagora.android.testshared.TestFixtures.State.EXCEED_MAX_FILE_SIZE
import com.linagora.android.testshared.TestFixtures.State.INIT_STATE
import com.linagora.android.testshared.TestFixtures.State.LOADING_STATE
import com.linagora.android.testshared.TestFixtures.State.QUOTA_ACCOUNT_NO_MORE_AVAILABLE_SPACE
import com.linagora.android.testshared.TestFixtures.State.VALID_QUOTA_ACCOUNT_STATE
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.P])
class EnoughAccountQuotaInteractorTest {

    @Mock
    lateinit var userRepository: UserRepository

    @Mock
    lateinit var quotaRepository: QuotaRepository

    lateinit var enoughAccountQuotaInteractor: EnoughAccountQuotaInteractor

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        enoughAccountQuotaInteractor = EnoughAccountQuotaInteractor(userRepository, quotaRepository)
    }

    @Test
    fun enoughQuotaShouldReturnValidAccountQuotaWhenEnoughQuotaForDocument() {
        runBlockingTest {
            Mockito.`when`(userRepository.getAuthorizedUser())
                .thenAnswer { LINSHARE_USER }
            Mockito.`when`(quotaRepository.findQuota(LINSHARE_USER.quotaUuid.toString()))
                .thenAnswer { QUOTA }

            val states = enoughAccountQuotaInteractor(DOCUMENT_REQUEST)
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
            Mockito.`when`(userRepository.getAuthorizedUser())
                .thenAnswer { LINSHARE_USER }
            Mockito.`when`(quotaRepository.findQuota(LINSHARE_USER.quotaUuid.toString()))
                .thenAnswer { LOW_QUOTA }

            val states = enoughAccountQuotaInteractor(DOCUMENT_REQUEST_BIG_SIZE)
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
            Mockito.`when`(userRepository.getAuthorizedUser())
                .thenAnswer { LINSHARE_USER }
            Mockito.`when`(quotaRepository.findQuota(LINSHARE_USER.quotaUuid.toString()))
                .thenAnswer { QUOTA }

            val states = enoughAccountQuotaInteractor(DOCUMENT_REQUEST_BIG_SIZE)
                .toList(ArrayList())

            assertThat(states).hasSize(2)
            assertThat(states[0](INIT_STATE))
                .isEqualTo(LOADING_STATE)

            assertThat(states[1](LOADING_STATE))
                .isEqualTo(QUOTA_ACCOUNT_NO_MORE_AVAILABLE_SPACE)
        }
    }
}
