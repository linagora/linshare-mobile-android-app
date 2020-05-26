package com.linagora.android.linshare.domain.usecases.copy

import arrow.core.Either
import com.google.common.truth.Truth.assertThat
import com.linagora.android.linshare.domain.model.ErrorResponse
import com.linagora.android.linshare.domain.repository.document.DocumentRepository
import com.linagora.android.linshare.domain.usecases.InteractorHandler
import com.linagora.android.linshare.domain.usecases.myspace.CopyFailedWithFileSizeExceed
import com.linagora.android.linshare.domain.usecases.myspace.CopyFailedWithQuotaReach
import com.linagora.android.linshare.domain.utils.BusinessErrorCode
import com.linagora.android.testshared.CopyFixtures.COPY_REQUEST_1
import com.linagora.android.testshared.CopyFixtures.COPY_SUCCESS_STATE_1
import com.linagora.android.testshared.TestFixtures.Documents.DOCUMENT
import com.linagora.android.testshared.TestFixtures.State.INIT_STATE
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runBlockingTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations

class CopyInMySpaceInteractorTest {

    @Mock
    lateinit var documentRepository: DocumentRepository

    private lateinit var interactorHandler: InteractorHandler

    private lateinit var copyInMySpaceInteractor: CopyInMySpaceInteractor

    @BeforeEach
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        interactorHandler = InteractorHandler()
        copyInMySpaceInteractor = CopyInMySpaceInteractor(interactorHandler, documentRepository)
    }

    @Test
    fun copyShouldSuccess() = runBlockingTest {
        `when`(documentRepository.copy(COPY_REQUEST_1))
            .thenAnswer { listOf(DOCUMENT) }

        val copyStates = copyInMySpaceInteractor(COPY_REQUEST_1)
            .toList(ArrayList())

        assertThat(copyStates).hasSize(1)
        assertThat(copyStates[0](INIT_STATE))
            .isEqualTo(COPY_SUCCESS_STATE_1)
    }

    @Test
    fun copyShouldGenerateFailedStateWhenAccountQuotaReach() = runBlockingTest {
        `when`(documentRepository.copy(COPY_REQUEST_1))
            .thenThrow(CopyException(ErrorResponse(
                "quota exceed", BusinessErrorCode.QuotaAccountNoMoreSpaceErrorCode)))

        val states = copyInMySpaceInteractor(COPY_REQUEST_1)
            .toList(ArrayList())

        assertThat(states).hasSize(1)
        assertThat(states[0](INIT_STATE)).isEqualTo(Either.left(CopyFailedWithQuotaReach))
    }

    @Test
    fun copyShouldGenerateFailedStateWhenExceedMaxFileSize() = runBlockingTest {
        `when`(documentRepository.copy(COPY_REQUEST_1))
            .thenThrow(CopyException(ErrorResponse(
                "file size exceed", BusinessErrorCode.FileSizeIsGreaterThanMaxFileSize)))

        val states = copyInMySpaceInteractor(COPY_REQUEST_1)
            .toList(ArrayList())

        assertThat(states).hasSize(1)
        assertThat(states[0](INIT_STATE)).isEqualTo(Either.left(CopyFailedWithFileSizeExceed))
    }
}
