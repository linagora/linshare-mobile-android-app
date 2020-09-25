/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 *
 * Copyright (C) 2020 LINAGORA
 *
 * This program is free software: you can redistribute it and/or modify it under the
 * terms of the GNU Affero General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later version,
 * provided you comply with the Additional Terms applicable for LinShare software by
 * Linagora pursuant to Section 7 of the GNU Affero General Public License,
 * subsections (b), (c), and (e), pursuant to which you must notably (i) retain the
 * display in the interface of the “LinShare™” trademark/logo, the "Libre & Free" mention,
 * the words “You are using the Free and Open Source version of LinShare™, powered by
 * Linagora © 2009–2020. Contribute to Linshare R&D by subscribing to an Enterprise
 * offer!”. You must also retain the latter notice in all asynchronous messages such as
 * e-mails sent with the Program, (ii) retain all hypertext links between LinShare and
 * http://www.linshare.org, between linagora.com and Linagora, and (iii) refrain from
 * infringing Linagora intellectual property rights over its trademarks and commercial
 * brands. Other Additional Terms apply, see
 * <http://www.linshare.org/licenses/LinShare-License_AfferoGPL-v3.pdf>
 * for more details.
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for
 * more details.
 * You should have received a copy of the GNU Affero General Public License and its
 * applicable Additional Terms for LinShare along with this program. If not, see
 * <http://www.gnu.org/licenses/> for the GNU Affero General Public License version
 *  3 and <http://www.linshare.org/licenses/LinShare-License_AfferoGPL-v3.pdf> for
 *  the Additional Terms applicable to LinShare software.
 */

package com.linagora.android.linshare.data.repository.document

import android.os.Build
import com.google.common.truth.Truth.assertThat
import com.linagora.android.linshare.data.DataFixtures.DOCUMENT_REQUEST
import com.linagora.android.linshare.data.datasource.DocumentDataSource
import com.linagora.android.linshare.domain.model.ErrorResponse
import com.linagora.android.linshare.domain.model.LinShareErrorCode
import com.linagora.android.linshare.domain.model.document.Document
import com.linagora.android.linshare.domain.model.document.DocumentRenameRequest
import com.linagora.android.linshare.domain.model.upload.OnTransfer
import com.linagora.android.linshare.domain.model.upload.TotalBytes
import com.linagora.android.linshare.domain.model.upload.TransferredBytes
import com.linagora.android.linshare.domain.usecases.copy.CopyException
import com.linagora.android.linshare.domain.usecases.remove.RemoveDocumentException
import com.linagora.android.linshare.domain.usecases.upload.UploadException
import com.linagora.android.linshare.domain.utils.BusinessErrorCode
import com.linagora.android.testshared.CopyFixtures.COPY_REQUEST_1
import com.linagora.android.testshared.ShareFixtures.SHARE_1
import com.linagora.android.testshared.ShareFixtures.SHARE_2
import com.linagora.android.testshared.ShareFixtures.SHARE_CREATION_1
import com.linagora.android.testshared.ShareFixtures.SHARE_CREATION_2
import com.linagora.android.testshared.TestFixtures
import com.linagora.android.testshared.TestFixtures.Documents.DOCUMENT
import com.linagora.android.testshared.TestFixtures.Documents.DOCUMENT_2
import com.linagora.android.testshared.TestFixtures.Documents.DOCUMENT_ID
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

    @Test
    fun copyShouldSuccess() = runBlockingTest {
        `when`(documentDataSource.copy(COPY_REQUEST_1))
            .thenAnswer { listOf(DOCUMENT) }

        val documents = documentRepositoryImp.copy(COPY_REQUEST_1)
        assertThat(documents).containsExactly(DOCUMENT)
    }

    @Test
    fun copyShouldThrowWhenAccountQuotaReach() = runBlockingTest {
        val copyException = CopyException(
            ErrorResponse(
                "account quota reach",
                BusinessErrorCode.QuotaAccountNoMoreSpaceErrorCode))

        `when`(documentDataSource.copy(COPY_REQUEST_1))
            .thenThrow(copyException)

        assertThrows<CopyException> {
            runBlockingTest { documentRepositoryImp.copy(COPY_REQUEST_1) }
        }
    }

    @Test
    fun getDocumentShouldSuccessWithValidDocumentId() = runBlockingTest {
        `when`(documentDataSource.get(DOCUMENT_ID))
            .thenAnswer { DOCUMENT }

        assertThat(documentRepositoryImp.get(DOCUMENT_ID)).isEqualTo(DOCUMENT)
    }

    @Test
    fun getDocumentShouldSuccessWhenGetDocumentHaveAFailure() = runBlockingTest {
        val exception = RuntimeException("can not get document")
        `when`(documentDataSource.get(DOCUMENT_ID))
            .thenThrow(exception)

        assertThrows<RuntimeException> {
            runBlockingTest { documentRepositoryImp.get(DOCUMENT_ID) }
        }
    }

    @Test
    fun renameDocumentShouldSuccessWithValidDocumentName() = runBlockingTest {
        `when`(documentDataSource.renameDocument(
                TestFixtures.Documents.DOCUMENT_3.documentId,
                DocumentRenameRequest("New Name.txt")))
            .thenAnswer { TestFixtures.Documents.DOCUMENT_3 }

        assertThat(documentDataSource.renameDocument(
                TestFixtures.Documents.DOCUMENT_3.documentId,
                DocumentRenameRequest("New Name.txt")))
            .isEqualTo(TestFixtures.Documents.DOCUMENT_3)
    }

    @Test
    fun renameDocumentShouldFailWhenRenameDocumentHasAFailure() = runBlockingTest {
        val exception = RuntimeException("Can not rename the document")
        `when`((documentDataSource.renameDocument(
                TestFixtures.Documents.DOCUMENT_3.documentId,
                DocumentRenameRequest("New Name.txt"))))
            .thenThrow(exception)

        assertThrows<RuntimeException> {
            runBlockingTest { documentDataSource.renameDocument(
                TestFixtures.Documents.DOCUMENT_3.documentId,
                DocumentRenameRequest("New Name.txt")) }
        }
    }
}
