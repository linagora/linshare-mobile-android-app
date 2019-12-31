package com.linagora.android.linshare.domain.usecases.myspace

import arrow.core.Either
import com.google.common.truth.Truth.assertThat
import com.linagora.android.linshare.domain.repository.document.DocumentRepository
import com.linagora.android.testshared.TestFixtures.Documents.DOCUMENT
import com.linagora.android.testshared.TestFixtures.Documents.DOCUMENT_2
import com.linagora.android.testshared.TestFixtures.MySpaces.ALL_DOCUMENTS_STATE
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

class GetAllDocumentsInteractorTest {

    @Mock
    private lateinit var documentRepository: DocumentRepository

    private lateinit var getAllDocumentsInteractor: GetAllDocumentsInteractor

    @BeforeEach
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        getAllDocumentsInteractor = GetAllDocumentsInteractor(documentRepository)
    }

    @Test
    fun getAllDocumentsShouldSuccessWithAllDocuments() {
        runBlockingTest {
            `when`(documentRepository.getAll())
                .thenAnswer { listOf(DOCUMENT, DOCUMENT_2) }

            assertThat(getAllDocumentsInteractor()
                    .map { it(INIT_STATE) }
                    .toList(ArrayList()))
                .containsExactly(LOADING_STATE, ALL_DOCUMENTS_STATE)
        }
    }

    @Test
    fun getAllDocumentsShouldFailedWhenGetAllFailed() {
        runBlockingTest {
            val exception = RuntimeException("get list documents failed")

            `when`(documentRepository.getAll())
                .thenThrow(exception)

            assertThat(getAllDocumentsInteractor()
                    .map { it(INIT_STATE) }
                    .toList(ArrayList()))
                .containsExactly(LOADING_STATE, Either.Left(MySpaceFailure(exception)))
        }
    }
}
