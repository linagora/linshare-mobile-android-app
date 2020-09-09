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

package com.linagora.android.linshare.domain.usecases.myspace

import arrow.core.Either
import com.google.common.truth.Truth
import com.linagora.android.linshare.domain.model.document.Document
import com.linagora.android.linshare.domain.model.order.OrderListConfigurationType
import com.linagora.android.linshare.domain.repository.document.DocumentRepository
import com.linagora.android.testshared.TestFixtures
import com.linagora.android.testshared.TestFixtures.Documents.DOCUMENT
import com.linagora.android.testshared.TestFixtures.Documents.DOCUMENT_2
import com.linagora.android.testshared.TestFixtures.Documents.DOCUMENT_3
import com.linagora.android.testshared.TestFixtures.Documents.DOCUMENT_4
import com.linagora.android.testshared.TestFixtures.MySpaces.EMPTY_DOCUMENTS_STATE
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runBlockingTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations

class GetAllDocumentsInteractorOrderedTest {

    @Mock
    private lateinit var documentRepository: DocumentRepository

    private lateinit var getAllDocumentsOrderedInteractor: GetAllDocumentsOrderedInteractor

    @BeforeEach
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        getAllDocumentsOrderedInteractor = GetAllDocumentsOrderedInteractor(documentRepository)
    }

    @Test
    fun getAllDocumentsShouldSuccessWithAllDocuments() {
        runBlockingTest {
            Mockito.`when`(documentRepository.getAll())
                .thenAnswer { listOf(DOCUMENT, DOCUMENT_2) }

            Truth.assertThat(getAllDocumentsOrderedInteractor(OrderListConfigurationType.AscendingName)
                .map { it(TestFixtures.State.INIT_STATE) }
                .toList(ArrayList()))
                .containsExactly(TestFixtures.State.LOADING_STATE, TestFixtures.MySpaces.ALL_DOCUMENTS_STATE)
        }
    }

    @Test
    fun getAllDocumentsShouldFailedWhenGetAllFailed() {
        runBlockingTest {
            val exception = RuntimeException("get list documents failed")

            Mockito.`when`(documentRepository.getAll())
                .thenThrow(exception)

            Truth.assertThat(getAllDocumentsOrderedInteractor(OrderListConfigurationType.AscendingName)
                .map { it(TestFixtures.State.INIT_STATE) }
                .toList(ArrayList()))
                .containsExactly(TestFixtures.State.LOADING_STATE, Either.Left(MySpaceFailure(exception)))
        }
    }

    @Test
    fun getAllDocumentsShouldSuccessWithSortedListAscendingName() {
        runBlockingTest {
            Mockito.`when`(documentRepository.getAll())
                .thenAnswer { listOf(DOCUMENT, DOCUMENT_2, DOCUMENT_3, DOCUMENT_4) }

            val expectedState = Either.right(MySpaceViewState(listOf(DOCUMENT_3, DOCUMENT_4, DOCUMENT, DOCUMENT_2)))

            Truth.assertThat(getAllDocumentsOrderedInteractor(OrderListConfigurationType.AscendingName)
                .map { it(TestFixtures.State.INIT_STATE) }
                .toList(ArrayList()))
                .containsExactly(TestFixtures.State.LOADING_STATE, expectedState)
        }
    }

    @Test
    fun getAllDocumentsShouldSuccessWithSortedListDescendingName() {
        runBlockingTest {
            Mockito.`when`(documentRepository.getAll())
                .thenAnswer { listOf(DOCUMENT, DOCUMENT_2, DOCUMENT_3, DOCUMENT_4) }

            val expectedState = Either.right(MySpaceViewState(listOf(DOCUMENT_2, DOCUMENT, DOCUMENT_4, DOCUMENT_3)))

            Truth.assertThat(getAllDocumentsOrderedInteractor(OrderListConfigurationType.DescendingName)
                .map { it(TestFixtures.State.INIT_STATE) }
                .toList(ArrayList()))
                .containsExactly(TestFixtures.State.LOADING_STATE, expectedState)
        }
    }

    @Test
    fun getAllDocumentsShouldSuccessWithSortedListAscendingCreationDate() {
        runBlockingTest {
            Mockito.`when`(documentRepository.getAll())
                .thenAnswer { listOf(DOCUMENT, DOCUMENT_2, DOCUMENT_3, DOCUMENT_4) }

            val expectedState = Either.right(MySpaceViewState(listOf(DOCUMENT_4, DOCUMENT_3, DOCUMENT, DOCUMENT_2)))

            Truth.assertThat(getAllDocumentsOrderedInteractor(OrderListConfigurationType.AscendingCreationDate)
                .map { it(TestFixtures.State.INIT_STATE) }
                .toList(ArrayList()))
                .containsExactly(TestFixtures.State.LOADING_STATE, expectedState)
        }
    }

    @Test
    fun getAllDocumentsShouldSuccessWithSortedListDescendingCreationDate() {
        runBlockingTest {
            Mockito.`when`(documentRepository.getAll())
                .thenAnswer { listOf(DOCUMENT, DOCUMENT_2, DOCUMENT_3, DOCUMENT_4) }

            val expectedState = Either.right(MySpaceViewState(listOf(DOCUMENT, DOCUMENT_2, DOCUMENT_3, DOCUMENT_4)))

            Truth.assertThat(getAllDocumentsOrderedInteractor(OrderListConfigurationType.DescendingCreationDate)
                .map { it(TestFixtures.State.INIT_STATE) }
                .toList(ArrayList()))
                .containsExactly(TestFixtures.State.LOADING_STATE, expectedState)
        }
    }

    @Test
    fun getAllDocumentsShouldSuccessWithSortedListAscendingModificationDate() {
        runBlockingTest {
            Mockito.`when`(documentRepository.getAll())
                .thenAnswer { listOf(DOCUMENT, DOCUMENT_2, DOCUMENT_3, DOCUMENT_4) }

            val expectedState = Either.right(MySpaceViewState(listOf(DOCUMENT_4, DOCUMENT_3, DOCUMENT, DOCUMENT_2)))

            Truth.assertThat(getAllDocumentsOrderedInteractor(OrderListConfigurationType.AscendingModificationDate)
                .map { it(TestFixtures.State.INIT_STATE) }
                .toList(ArrayList()))
                .containsExactly(TestFixtures.State.LOADING_STATE, expectedState)
        }
    }

    @Test
    fun getAllDocumentsShouldSuccessWithSortedListDescendingModificationDate() {
        runBlockingTest {
            Mockito.`when`(documentRepository.getAll())
                .thenAnswer { listOf(DOCUMENT, DOCUMENT_2, DOCUMENT_3, DOCUMENT_4) }

            val expectedState = Either.right(MySpaceViewState(listOf(DOCUMENT, DOCUMENT_2, DOCUMENT_3, DOCUMENT_4)))

            Truth.assertThat(getAllDocumentsOrderedInteractor(OrderListConfigurationType.DescendingModificationDate)
                .map { it(TestFixtures.State.INIT_STATE) }
                .toList(ArrayList()))
                .containsExactly(TestFixtures.State.LOADING_STATE, expectedState)
        }
    }

    @Test
    fun getAllDocumentsShouldSuccessWithSortedListAscendingFileSize() {
        runBlockingTest {
            Mockito.`when`(documentRepository.getAll())
                .thenAnswer { listOf(DOCUMENT, DOCUMENT_2, DOCUMENT_3, DOCUMENT_4) }

            val expectedState = Either.right(MySpaceViewState(listOf(DOCUMENT, DOCUMENT_2, DOCUMENT_3, DOCUMENT_4)))

            Truth.assertThat(getAllDocumentsOrderedInteractor(OrderListConfigurationType.AscendingFileSize)
                .map { it(TestFixtures.State.INIT_STATE) }
                .toList(ArrayList()))
                .containsExactly(TestFixtures.State.LOADING_STATE, expectedState)
        }
    }

    @Test
    fun getAllDocumentsShouldSuccessWithSortedListDescendingFileSize() {
        runBlockingTest {
            Mockito.`when`(documentRepository.getAll())
                .thenAnswer { listOf(DOCUMENT, DOCUMENT_2, DOCUMENT_3, DOCUMENT_4) }

            val expectedState = Either.right(MySpaceViewState(listOf(DOCUMENT_4, DOCUMENT_3, DOCUMENT, DOCUMENT_2)))

            Truth.assertThat(getAllDocumentsOrderedInteractor(OrderListConfigurationType.DescendingFileSize)
                .map { it(TestFixtures.State.INIT_STATE) }
                .toList(ArrayList()))
                .containsExactly(TestFixtures.State.LOADING_STATE, expectedState)
        }
    }

    @Test
    fun getAllDocumentsShouldSuccessWithSortedListAscendingShared() {
        runBlockingTest {
            Mockito.`when`(documentRepository.getAll())
                .thenAnswer { listOf(DOCUMENT, DOCUMENT_2, DOCUMENT_3, DOCUMENT_4) }

            val expectedState = Either.right(MySpaceViewState(listOf(DOCUMENT, DOCUMENT_2, DOCUMENT_3, DOCUMENT_4)))

            Truth.assertThat(getAllDocumentsOrderedInteractor(OrderListConfigurationType.AscendingShared)
                .map { it(TestFixtures.State.INIT_STATE) }
                .toList(ArrayList()))
                .containsExactly(TestFixtures.State.LOADING_STATE, expectedState)
        }
    }

    @Test
    fun getAllDocumentsShouldSuccessWithSortedListDescendingShared() {
        runBlockingTest {
            Mockito.`when`(documentRepository.getAll())
                .thenAnswer { listOf(DOCUMENT, DOCUMENT_2, DOCUMENT_3, DOCUMENT_4) }

            val expectedState = Either.right(MySpaceViewState(listOf(DOCUMENT_4, DOCUMENT_3, DOCUMENT, DOCUMENT_2)))

            Truth.assertThat(getAllDocumentsOrderedInteractor(OrderListConfigurationType.DescendingShared)
                .map { it(TestFixtures.State.INIT_STATE) }
                .toList(ArrayList()))
                .containsExactly(TestFixtures.State.LOADING_STATE, expectedState)
        }
    }

    @Test
    fun getAllDocumentsShouldSuccessWithEmptyMySpaceState() {
        runBlockingTest {
            Mockito.`when`(documentRepository.getAll())
                .thenAnswer { emptyList<Document>() }

            Truth.assertThat(getAllDocumentsOrderedInteractor(OrderListConfigurationType.DescendingShared)
                .map { it(TestFixtures.State.INIT_STATE) }
                .toList(ArrayList()))
                .containsExactly(TestFixtures.State.LOADING_STATE, EMPTY_DOCUMENTS_STATE)
        }
    }
}
