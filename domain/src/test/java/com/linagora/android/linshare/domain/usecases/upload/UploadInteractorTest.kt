package com.linagora.android.linshare.domain.usecases.upload

import android.net.Uri
import android.os.Build
import com.google.common.truth.Truth.assertThat
import com.linagora.android.linshare.domain.model.ErrorResponse
import com.linagora.android.linshare.domain.model.document.DocumentRequest
import com.linagora.android.linshare.domain.repository.document.DocumentRepository
import com.linagora.android.linshare.domain.repository.user.QuotaRepository
import com.linagora.android.linshare.domain.repository.user.UserRepository
import com.linagora.android.linshare.domain.utils.BusinessErrorCode
import com.linagora.android.testshared.TestFixtures.Accounts.LINSHARE_USER
import com.linagora.android.testshared.TestFixtures.Accounts.LOW_QUOTA
import com.linagora.android.testshared.TestFixtures.Accounts.QUOTA
import com.linagora.android.testshared.TestFixtures.Documents.DOCUMENT
import com.linagora.android.testshared.TestFixtures.State.INIT_STATE
import com.linagora.android.testshared.TestFixtures.State.LOADING_STATE
import com.linagora.android.testshared.TestFixtures.State.QUOTA_ACCOUNT_NO_MORE_AVAILABLE_SPACE
import com.linagora.android.testshared.TestFixtures.State.UPLOAD_SUCCESS_VIEW_STATE
import com.linagora.android.testshared.extension.MockitoUtils.any
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runBlockingTest
import okhttp3.MediaType
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.P])
class UploadInteractorTest {

    companion object {
        val DOCUMENT_BIG_FILE = DocumentRequest(
            uri = Uri.parse("content://0@media/external/file/276"),
            fileName = "document.txt",
            fileSize = 2000,
            mediaType = MediaType.get("text/plain")
        )
    }

    @Mock
    lateinit var userRepository: UserRepository

    @Mock
    lateinit var quotaRepository: QuotaRepository

    @Mock
    lateinit var documentRepository: DocumentRepository

    lateinit var uploadInteractor: UploadInteractor

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        uploadInteractor = UploadInteractor(userRepository, quotaRepository, documentRepository)
    }

    @Test
    fun uploadShouldFailedWhenPreCheckNotEnoughQuota() {
        runBlockingTest {
            `when`(userRepository.getAuthorizedUser())
                .thenAnswer { LINSHARE_USER }
            `when`(quotaRepository.findQuota(LINSHARE_USER.quotaUuid.toString()))
                .thenAnswer { LOW_QUOTA }

            val states = uploadInteractor(DOCUMENT_BIG_FILE)
                .toList(ArrayList())

            assertThat(states).hasSize(2)
            assertThat(states[0](INIT_STATE))
                .isEqualTo(LOADING_STATE)

            assertThat(states[1](LOADING_STATE))
                .isEqualTo(QUOTA_ACCOUNT_NO_MORE_AVAILABLE_SPACE)
        }
    }

    @Test
    fun uploadShouldFailedWhenNotEnoughQuota() {
        runBlockingTest {
            `when`(userRepository.getAuthorizedUser())
                .thenAnswer { LINSHARE_USER }
            `when`(quotaRepository.findQuota(LINSHARE_USER.quotaUuid.toString()))
                .thenAnswer { QUOTA }

            `when`(documentRepository.upload(any(), any()))
                .thenThrow(UploadException(
                    ErrorResponse("quota exceeded", BusinessErrorCode.QuotaAccountNoMoreSpaceErrorCode)))

            val states = uploadInteractor(DOCUMENT_BIG_FILE)
                .toList(ArrayList())

            assertThat(states).hasSize(2)

            assertThat(states[0](INIT_STATE))
                .isEqualTo(LOADING_STATE)

            assertThat(states[1](LOADING_STATE))
                .isEqualTo(QUOTA_ACCOUNT_NO_MORE_AVAILABLE_SPACE)
        }
    }

    @Test
    fun uploadShouldSuccess() {
        runBlockingTest {
            `when`(userRepository.getAuthorizedUser())
                .thenAnswer { LINSHARE_USER }
            `when`(quotaRepository.findQuota(LINSHARE_USER.quotaUuid.toString()))
                .thenAnswer { QUOTA }

            `when`(documentRepository.upload(any(), any()))
                .thenAnswer { DOCUMENT }

            val states = uploadInteractor(DOCUMENT_BIG_FILE)
                .toList(ArrayList())

            assertThat(states).hasSize(2)

            assertThat(states[0](INIT_STATE))
                .isEqualTo(LOADING_STATE)

            assertThat(states[1](LOADING_STATE))
                .isEqualTo(UPLOAD_SUCCESS_VIEW_STATE)
        }
    }
}
