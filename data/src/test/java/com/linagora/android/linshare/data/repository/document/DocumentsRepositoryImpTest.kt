package com.linagora.android.linshare.data.repository.document

import android.os.Build
import com.google.common.truth.Truth.assertThat
import com.linagora.android.linshare.data.DataFixtures.DOCUMENT_REQUEST
import com.linagora.android.linshare.data.datasource.DocumentDataSource
import com.linagora.android.linshare.domain.model.ErrorResponse
import com.linagora.android.linshare.domain.model.LinShareErrorCode
import com.linagora.android.linshare.domain.model.document.Document
import com.linagora.android.linshare.domain.model.upload.OnTransfer
import com.linagora.android.linshare.domain.model.upload.TotalBytes
import com.linagora.android.linshare.domain.model.upload.TransferredBytes
import com.linagora.android.linshare.domain.usecases.remove.RemoveDocumentException
import com.linagora.android.linshare.domain.usecases.upload.UploadException
import com.linagora.android.testshared.ShareFixtures.SHARE_1
import com.linagora.android.testshared.ShareFixtures.SHARE_2
import com.linagora.android.testshared.ShareFixtures.SHARE_CREATION_1
import com.linagora.android.testshared.ShareFixtures.SHARE_CREATION_2
import com.linagora.android.testshared.TestFixtures.Documents.DOCUMENT
import com.linagora.android.testshared.TestFixtures.Documents.DOCUMENT_2
import com.linagora.android.testshared.TestFixtures.Searchs.QUERY_STRING
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Test
import org.junit.jupiter.api.assertThrows
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.P])
class DocumentsRepositoryImpTest {

    companion object {
        val ERROR_RESPONSE = ErrorResponse("quota exceed", LinShareErrorCode(123))
    }

    @Mock
    lateinit var documentDataSource: DocumentDataSource

    private lateinit var documentRepositoryImp: DocumentRepositoryImp

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        documentRepositoryImp = DocumentRepositoryImp(documentDataSource)
    }

    @Test
    fun uploadShouldReturnADocumentWhenSuccess() {
        val onTransfer = object : OnTransfer {
            override fun invoke(transferredBytes: TransferredBytes, totalBytes: TotalBytes) {}
        }
        runBlockingTest {
            `when`(documentDataSource.upload(
                DOCUMENT_REQUEST,
                onTransfer
            )).thenAnswer { DOCUMENT }

            val document = documentRepositoryImp.upload(DOCUMENT_REQUEST, onTransfer)

            assertThat(document).isEqualTo(DOCUMENT)
        }
    }

    @Test
    fun uploadShouldThrowWhenDataSourceThrow() {
        val onTransfer = object : OnTransfer {
            override fun invoke(transferredBytes: TransferredBytes, totalBytes: TotalBytes) {}
        }
        runBlockingTest {
            `when`(documentDataSource.upload(
                DOCUMENT_REQUEST,
                onTransfer
            )).thenThrow(UploadException(ERROR_RESPONSE))

            val exception = assertThrows<UploadException> {
                runBlockingTest {
                    documentRepositoryImp.upload(DOCUMENT_REQUEST, onTransfer)
                }
            }
            assertThat(exception.errorResponse).isEqualTo(ERROR_RESPONSE)
        }
    }

    @Test
    fun getAllShouldReturnAllDocuments() {
        runBlockingTest {
            `when`(documentDataSource.getAll())
                .thenAnswer { listOf(DOCUMENT, DOCUMENT_2) }

            val documents = documentRepositoryImp.getAll()
            assertThat(documents).containsExactly(DOCUMENT, DOCUMENT_2)
        }
    }

    @Test
    fun getAllShouldReturnEmptyListWhenNoDocumentsExist() {
        runBlockingTest {
            `when`(documentDataSource.getAll())
                .thenAnswer { emptyList<Document>() }

            val documents = documentRepositoryImp.getAll()
            assertThat(documents).isEmpty()
        }
    }

    @Test
    fun removeDocumentSuccess() {
        runBlockingTest {
            `when`(documentRepositoryImp.remove(DOCUMENT.documentId))
                .thenAnswer { DOCUMENT }

            val document = documentRepositoryImp.remove(DOCUMENT.documentId)
            assertThat(document).isEqualTo(DOCUMENT)
        }
    }

    @Test
    fun removeShouldThrowWhenRemoveDocumentFailure() {
        runBlockingTest {
            `when`(documentDataSource.remove(DOCUMENT.documentId))
                .thenThrow(RemoveDocumentException(RuntimeException()))

            assertThrows<RemoveDocumentException> {
                runBlockingTest {
                    documentRepositoryImp.remove(DOCUMENT.documentId)
                }
            }
        }
    }

    @Test
    fun searchShouldReturnResultList() {
        runBlockingTest {
            `when`(documentDataSource.search(QUERY_STRING))
                .thenAnswer { listOf(DOCUMENT, DOCUMENT_2) }

            val documents = documentRepositoryImp.search(QUERY_STRING)
            assertThat(documents).containsExactly(DOCUMENT, DOCUMENT_2)
        }
    }

    @Test
    fun searchShouldReturnResultEmptyList() {
        runBlockingTest {
            `when`(documentDataSource.search(QUERY_STRING))
                .thenAnswer { emptyList<Document>() }

            val documents = documentRepositoryImp.search(QUERY_STRING)
            assertThat(documents).isEmpty()
        }
    }

    @Test
    fun shareShouldReturnShareWithOneRecipient() {
        runBlockingTest {
            `when`(documentDataSource.share(SHARE_CREATION_1))
                .thenAnswer { listOf(SHARE_1) }

            val shares = documentRepositoryImp.share(SHARE_CREATION_1)
            assertThat(shares).hasSize(1)
            assertThat(shares[0]).isEqualTo(SHARE_1)
        }
    }

    @Test
    fun shareShouldReturnSharesWithMultiRecipient() {
        runBlockingTest {
            `when`(documentDataSource.share(SHARE_CREATION_2))
                .thenAnswer { listOf(SHARE_1, SHARE_2) }

            val shares = documentRepositoryImp.share(SHARE_CREATION_2)
            assertThat(shares).hasSize(2)
            assertThat(shares).containsExactly(SHARE_1, SHARE_2)
        }
    }
}
