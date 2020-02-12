package com.linagora.android.linshare.domain.usecases.upload

import android.os.Build
import arrow.core.Either
import com.google.common.truth.Truth.assertThat
import com.linagora.android.linshare.domain.model.ErrorResponse
import com.linagora.android.linshare.domain.repository.document.DocumentRepository
import com.linagora.android.linshare.domain.usecases.quota.EnoughAccountQuotaInteractor
import com.linagora.android.linshare.domain.usecases.utils.Failure
import com.linagora.android.linshare.domain.usecases.utils.State
import com.linagora.android.linshare.domain.usecases.utils.Success
import com.linagora.android.linshare.domain.utils.BusinessErrorCode
import com.linagora.android.linshare.domain.utils.emitState
import com.linagora.android.testshared.TestFixtures.DocumentRequests.DOCUMENT_REQUEST
import com.linagora.android.testshared.TestFixtures.DocumentRequests.DOCUMENT_REQUEST_BIG_SIZE
import com.linagora.android.testshared.TestFixtures.Documents.DOCUMENT
import com.linagora.android.testshared.TestFixtures.State.EXCEED_MAX_FILE_SIZE
import com.linagora.android.testshared.TestFixtures.State.INIT_STATE
import com.linagora.android.testshared.TestFixtures.State.LOADING_STATE
import com.linagora.android.testshared.TestFixtures.State.QUOTA_ACCOUNT_NO_MORE_AVAILABLE_SPACE
import com.linagora.android.testshared.TestFixtures.State.UPLOAD_SUCCESS_VIEW_STATE
import com.linagora.android.testshared.TestFixtures.State.VALID_QUOTA_ACCOUNT_STATE
import com.linagora.android.testshared.extension.MockitoUtils.any
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runBlockingTest
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

    @Mock
    lateinit var enoughAccountQuotaInteractor: EnoughAccountQuotaInteractor

    @Mock
    lateinit var documentRepository: DocumentRepository

    lateinit var uploadInteractor: UploadInteractor

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        uploadInteractor = UploadInteractor(enoughAccountQuotaInteractor, documentRepository)
    }

    @Test
    fun uploadShouldFailedWhenPreCheckExceedMaxFileSize() {
        runBlockingTest {
            `when`(enoughAccountQuotaInteractor(DOCUMENT_REQUEST))
                .then {
                    flow<State<Either<Failure, Success>>> {
                        emitState { LOADING_STATE }
                        emitState { EXCEED_MAX_FILE_SIZE }
                    }
                }

            val states = uploadInteractor(DOCUMENT_REQUEST)
                .toList(ArrayList())

            assertThat(states).hasSize(2)
            assertThat(states[0](INIT_STATE))
                .isEqualTo(LOADING_STATE)

            assertThat(states[1](LOADING_STATE))
                .isEqualTo(EXCEED_MAX_FILE_SIZE)
        }
    }

    @Test
    fun uploadShouldFailedWhenPreCheckNoMoreSpaceAvailable() {
        runBlockingTest {
            `when`(enoughAccountQuotaInteractor(DOCUMENT_REQUEST_BIG_SIZE))
                .then {
                    flow<State<Either<Failure, Success>>> {
                        emitState { LOADING_STATE }
                        emitState { QUOTA_ACCOUNT_NO_MORE_AVAILABLE_SPACE }
                    }
                }

            val states = uploadInteractor(DOCUMENT_REQUEST_BIG_SIZE)
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
            `when`(enoughAccountQuotaInteractor(DOCUMENT_REQUEST_BIG_SIZE))
                .then {
                    flow<State<Either<Failure, Success>>> {
                        emitState { LOADING_STATE }
                        emitState { VALID_QUOTA_ACCOUNT_STATE }
                    }
                }

            `when`(documentRepository.upload(any(), any()))
                .thenThrow(UploadException(
                    ErrorResponse("quota exceeded", BusinessErrorCode.QuotaAccountNoMoreSpaceErrorCode)
                ))

            val states = uploadInteractor(DOCUMENT_REQUEST_BIG_SIZE)
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
            `when`(enoughAccountQuotaInteractor(DOCUMENT_REQUEST_BIG_SIZE))
                .then {
                    flow<State<Either<Failure, Success>>> {
                        emitState { LOADING_STATE }
                        emitState { VALID_QUOTA_ACCOUNT_STATE }
                    }
                }

            `when`(documentRepository.upload(any(), any()))
                .thenAnswer { DOCUMENT }

            val states = uploadInteractor(DOCUMENT_REQUEST_BIG_SIZE)
                .toList(ArrayList())

            assertThat(states).hasSize(2)

            assertThat(states[0](INIT_STATE))
                .isEqualTo(LOADING_STATE)

            assertThat(states[1](LOADING_STATE))
                .isEqualTo(UPLOAD_SUCCESS_VIEW_STATE)
        }
    }
}
