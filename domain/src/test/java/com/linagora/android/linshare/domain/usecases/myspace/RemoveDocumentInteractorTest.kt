package com.linagora.android.linshare.domain.usecases.myspace

import arrow.core.Either
import com.google.common.truth.Truth.assertThat
import com.linagora.android.linshare.domain.repository.document.DocumentRepository
import com.linagora.android.testshared.TestFixtures.Documents.DOCUMENT
import com.linagora.android.testshared.TestFixtures.MySpaces.REMOVE_DOCUMENT_SUCCESS_VIEW_STATE
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

class RemoveDocumentInteractorTest {

    @Mock
    private lateinit var documentRepository: DocumentRepository

    private lateinit var removeDocumentInteractor: RemoveDocumentInteractor

    @BeforeEach
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        removeDocumentInteractor = RemoveDocumentInteractor(documentRepository)
    }

    @Test
    fun removeDocumentsShouldSuccess() {
        runBlockingTest {
            `when`(documentRepository.remove(DOCUMENT.uuid))
                .thenAnswer { DOCUMENT }

            assertThat(removeDocumentInteractor(DOCUMENT.uuid)
                .map { it(INIT_STATE) }
                .toList(ArrayList()))
                .containsExactly(LOADING_STATE, REMOVE_DOCUMENT_SUCCESS_VIEW_STATE)
        }
    }

    @Test
    fun removeDocumentsShouldFailed() {
        runBlockingTest {
            val exception = RuntimeException("get list documents failed")

            `when`(documentRepository.remove(DOCUMENT.uuid))
                .thenThrow(exception)

            assertThat(removeDocumentInteractor(DOCUMENT.uuid)
                .map { it(INIT_STATE) }
                .toList(ArrayList()))
                .containsExactly(LOADING_STATE, Either.Left(RemoveDocumentFailure(exception)))
        }
    }
}
