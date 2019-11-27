package com.linagora.android.linshare.data.repository.document

import android.os.Build
import com.google.common.truth.Truth.assertThat
import com.linagora.android.linshare.data.datasource.DocumentDataSource
import com.linagora.android.linshare.domain.usecases.upload.UploadException
import com.linagora.android.testshared.TestFixtures.Documents.DOCUMENT
import com.linagora.android.testshared.TestFixtures.Documents.DOCUMENT_REQUEST
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
        runBlockingTest {
            `when`(documentDataSource.upload(DOCUMENT_REQUEST))
                .thenAnswer { DOCUMENT }

            val document = documentRepositoryImp.upload(DOCUMENT_REQUEST)

            assertThat(document).isEqualTo(DOCUMENT)
        }
    }

    @Test
    fun uploadShouldThrowWhenDataSourceThrow() {
        runBlockingTest {
            `when`(documentDataSource.upload(DOCUMENT_REQUEST))
                .thenThrow(UploadException("upload error"))

            val exception = assertThrows<UploadException> {
                runBlockingTest { documentRepositoryImp.upload(DOCUMENT_REQUEST) }
            }
            assertThat(exception.message).isEqualTo("upload error")
        }
    }
}
