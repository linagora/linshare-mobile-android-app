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

package com.linagora.android.linshare.domain.usecases.sharedspace

import arrow.core.Either
import com.google.common.truth.Truth.assertThat
import com.linagora.android.linshare.domain.model.order.OrderListConfigurationType
import com.linagora.android.linshare.domain.model.sharedspace.WorkGroupNode
import com.linagora.android.linshare.domain.repository.sharedspacesdocument.SharedSpacesDocumentRepository
import com.linagora.android.testshared.SharedSpaceDocumentFixtures.PARENT_NODE_ID_1
import com.linagora.android.testshared.SharedSpaceDocumentFixtures.QUERY_SHARED_SPACE_DOCUMENT
import com.linagora.android.testshared.SharedSpaceDocumentFixtures.SEARCH_SHARED_SPACE_DOCUMENT_STATE
import com.linagora.android.testshared.SharedSpaceDocumentFixtures.SHARED_SPACE_ID_1
import com.linagora.android.testshared.SharedSpaceDocumentFixtures.WORK_GROUP_DOCUMENT_1
import com.linagora.android.testshared.SharedSpaceDocumentFixtures.WORK_GROUP_DOCUMENT_2
import com.linagora.android.testshared.TestFixtures.State.INIT_STATE
import com.linagora.android.testshared.TestFixtures.State.LOADING_STATE
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runBlockingTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations

class SearchSharedSpaceDocumentInteractorTest {
    @Mock
    lateinit var sharedSpacesDocumentRepository: SharedSpacesDocumentRepository

    private lateinit var searchSharedSpaceDocumentInteractor: SearchSharedSpaceDocumentInteractor

    @BeforeEach
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        searchSharedSpaceDocumentInteractor = SearchSharedSpaceDocumentInteractor(sharedSpacesDocumentRepository)
    }

    @Test
    fun searchShouldReturnSuccessStateWithMatchedListDocument() = runBlockingTest {
        `when`(sharedSpacesDocumentRepository.searchSharedSpaceDocuments(
                SHARED_SPACE_ID_1, PARENT_NODE_ID_1, QUERY_SHARED_SPACE_DOCUMENT))
            .thenAnswer { listOf(WORK_GROUP_DOCUMENT_1, WORK_GROUP_DOCUMENT_2) }

        val states = searchSharedSpaceDocumentInteractor(
                SHARED_SPACE_ID_1, PARENT_NODE_ID_1, QUERY_SHARED_SPACE_DOCUMENT, OrderListConfigurationType.DescendingName)
            .toList(ArrayList())

        assertThat(states).hasSize(2)
        assertThat(states[0](INIT_STATE))
            .isEqualTo(LOADING_STATE)
        assertThat(states[1](LOADING_STATE))
            .isEqualTo(SEARCH_SHARED_SPACE_DOCUMENT_STATE)
    }

    @Test
    fun searchShouldReturnEmptyStateWhenNoResultMatched() = runBlockingTest {
        `when`(sharedSpacesDocumentRepository.searchSharedSpaceDocuments(
                SHARED_SPACE_ID_1, PARENT_NODE_ID_1, QUERY_SHARED_SPACE_DOCUMENT))
            .thenAnswer { emptyList<WorkGroupNode>() }

        val states = searchSharedSpaceDocumentInteractor(
                SHARED_SPACE_ID_1, PARENT_NODE_ID_1, QUERY_SHARED_SPACE_DOCUMENT, OrderListConfigurationType.DescendingName)
            .toList(ArrayList())

        assertThat(states).hasSize(2)
        assertThat(states[0](INIT_STATE))
            .isEqualTo(LOADING_STATE)
        assertThat(states[1](LOADING_STATE))
            .isEqualTo(Either.Left(SearchSharedSpaceDocumentNoResult))
    }

    @Test
    fun searchShouldReturnFailureStateWhenSearchHaveError() = runBlockingTest {
        val exception = RuntimeException("Search document failed")
        `when`(sharedSpacesDocumentRepository.searchSharedSpaceDocuments(
                SHARED_SPACE_ID_1, PARENT_NODE_ID_1, QUERY_SHARED_SPACE_DOCUMENT))
            .thenThrow(exception)

        val states = searchSharedSpaceDocumentInteractor(
                SHARED_SPACE_ID_1, PARENT_NODE_ID_1, QUERY_SHARED_SPACE_DOCUMENT, OrderListConfigurationType.DescendingName)
            .toList(ArrayList())

        assertThat(states).hasSize(2)
        assertThat(states[0](INIT_STATE))
            .isEqualTo(LOADING_STATE)
        assertThat(states[1](LOADING_STATE))
            .isEqualTo(Either.left(SharedSpaceDocumentFailure(exception)))
    }

    @Test
    fun searchShouldReturnSuccessStateWithAscendingNameMatchedListDocument() = runBlockingTest {
        `when`(sharedSpacesDocumentRepository.searchSharedSpaceDocuments(
            SHARED_SPACE_ID_1, PARENT_NODE_ID_1, QUERY_SHARED_SPACE_DOCUMENT))
            .thenAnswer { listOf(WORK_GROUP_DOCUMENT_1, WORK_GROUP_DOCUMENT_2) }

        val expectedState = Either.right(SearchSharedSpaceDocumentViewState(listOf(WORK_GROUP_DOCUMENT_2, WORK_GROUP_DOCUMENT_1)))

        val states = searchSharedSpaceDocumentInteractor(
            SHARED_SPACE_ID_1, PARENT_NODE_ID_1, QUERY_SHARED_SPACE_DOCUMENT, OrderListConfigurationType.AscendingName)
            .toList(ArrayList())

        assertThat(states).hasSize(2)
        assertThat(states[0](INIT_STATE))
            .isEqualTo(LOADING_STATE)
        assertThat(states[1](LOADING_STATE))
            .isEqualTo(expectedState)
    }

    @Test
    fun searchShouldReturnSuccessStateWithDescendingNameMatchedListDocument() = runBlockingTest {
        `when`(sharedSpacesDocumentRepository.searchSharedSpaceDocuments(
            SHARED_SPACE_ID_1, PARENT_NODE_ID_1, QUERY_SHARED_SPACE_DOCUMENT))
            .thenAnswer { listOf(WORK_GROUP_DOCUMENT_1, WORK_GROUP_DOCUMENT_2) }

        val expectedState = Either.right(SearchSharedSpaceDocumentViewState(listOf(WORK_GROUP_DOCUMENT_1, WORK_GROUP_DOCUMENT_2)))

        val states = searchSharedSpaceDocumentInteractor(
            SHARED_SPACE_ID_1, PARENT_NODE_ID_1, QUERY_SHARED_SPACE_DOCUMENT, OrderListConfigurationType.DescendingName)
            .toList(ArrayList())

        assertThat(states).hasSize(2)
        assertThat(states[0](INIT_STATE))
            .isEqualTo(LOADING_STATE)
        assertThat(states[1](LOADING_STATE))
            .isEqualTo(expectedState)
    }

    @Test
    fun searchShouldReturnSuccessStateWithAscendingModificationDateMatchedListDocument() = runBlockingTest {
        `when`(sharedSpacesDocumentRepository.searchSharedSpaceDocuments(
            SHARED_SPACE_ID_1, PARENT_NODE_ID_1, QUERY_SHARED_SPACE_DOCUMENT))
            .thenAnswer { listOf(WORK_GROUP_DOCUMENT_1, WORK_GROUP_DOCUMENT_2) }

        val expectedState = Either.right(SearchSharedSpaceDocumentViewState(listOf(WORK_GROUP_DOCUMENT_1, WORK_GROUP_DOCUMENT_2)))

        val states = searchSharedSpaceDocumentInteractor(
            SHARED_SPACE_ID_1, PARENT_NODE_ID_1, QUERY_SHARED_SPACE_DOCUMENT, OrderListConfigurationType.AscendingModificationDate)
            .toList(ArrayList())

        assertThat(states).hasSize(2)
        assertThat(states[0](INIT_STATE))
            .isEqualTo(LOADING_STATE)
        assertThat(states[1](LOADING_STATE))
            .isEqualTo(expectedState)
    }

    @Test
    fun searchShouldReturnSuccessStateWithDescendingModificationDateMatchedListDocument() = runBlockingTest {
        `when`(sharedSpacesDocumentRepository.searchSharedSpaceDocuments(
            SHARED_SPACE_ID_1, PARENT_NODE_ID_1, QUERY_SHARED_SPACE_DOCUMENT))
            .thenAnswer { listOf(WORK_GROUP_DOCUMENT_1, WORK_GROUP_DOCUMENT_2) }

        val expectedState = Either.right(SearchSharedSpaceDocumentViewState(listOf(WORK_GROUP_DOCUMENT_2, WORK_GROUP_DOCUMENT_1)))

        val states = searchSharedSpaceDocumentInteractor(
            SHARED_SPACE_ID_1, PARENT_NODE_ID_1, QUERY_SHARED_SPACE_DOCUMENT, OrderListConfigurationType.DescendingModificationDate)
            .toList(ArrayList())

        assertThat(states).hasSize(2)
        assertThat(states[0](INIT_STATE))
            .isEqualTo(LOADING_STATE)
        assertThat(states[1](LOADING_STATE))
            .isEqualTo(expectedState)
    }

    @Test
    fun searchShouldReturnSuccessStateWithAscendingCreationDateMatchedListDocument() = runBlockingTest {
        `when`(sharedSpacesDocumentRepository.searchSharedSpaceDocuments(
            SHARED_SPACE_ID_1, PARENT_NODE_ID_1, QUERY_SHARED_SPACE_DOCUMENT))
            .thenAnswer { listOf(WORK_GROUP_DOCUMENT_1, WORK_GROUP_DOCUMENT_2) }

        val expectedState = Either.right(SearchSharedSpaceDocumentViewState(listOf(WORK_GROUP_DOCUMENT_1, WORK_GROUP_DOCUMENT_2)))

        val states = searchSharedSpaceDocumentInteractor(
            SHARED_SPACE_ID_1, PARENT_NODE_ID_1, QUERY_SHARED_SPACE_DOCUMENT, OrderListConfigurationType.AscendingCreationDate)
            .toList(ArrayList())

        assertThat(states).hasSize(2)
        assertThat(states[0](INIT_STATE))
            .isEqualTo(LOADING_STATE)
        assertThat(states[1](LOADING_STATE))
            .isEqualTo(expectedState)
    }

    @Test
    fun searchShouldReturnSuccessStateWithDescendingCreationDateMatchedListDocument() = runBlockingTest {
        `when`(sharedSpacesDocumentRepository.searchSharedSpaceDocuments(
            SHARED_SPACE_ID_1, PARENT_NODE_ID_1, QUERY_SHARED_SPACE_DOCUMENT))
            .thenAnswer { listOf(WORK_GROUP_DOCUMENT_1, WORK_GROUP_DOCUMENT_2) }

        val expectedState = Either.right(SearchSharedSpaceDocumentViewState(listOf(WORK_GROUP_DOCUMENT_2, WORK_GROUP_DOCUMENT_1)))

        val states = searchSharedSpaceDocumentInteractor(
            SHARED_SPACE_ID_1, PARENT_NODE_ID_1, QUERY_SHARED_SPACE_DOCUMENT, OrderListConfigurationType.DescendingCreationDate)
            .toList(ArrayList())

        assertThat(states).hasSize(2)
        assertThat(states[0](INIT_STATE))
            .isEqualTo(LOADING_STATE)
        assertThat(states[1](LOADING_STATE))
            .isEqualTo(expectedState)
    }

    @Test
    fun searchShouldReturnSuccessStateWithAscendingFileSizeMatchedListDocument() = runBlockingTest {
        `when`(sharedSpacesDocumentRepository.searchSharedSpaceDocuments(
            SHARED_SPACE_ID_1, PARENT_NODE_ID_1, QUERY_SHARED_SPACE_DOCUMENT))
            .thenAnswer { listOf(WORK_GROUP_DOCUMENT_1, WORK_GROUP_DOCUMENT_2) }

        val expectedState = Either.right(SearchSharedSpaceDocumentViewState(listOf(WORK_GROUP_DOCUMENT_1, WORK_GROUP_DOCUMENT_2)))

        val states = searchSharedSpaceDocumentInteractor(
            SHARED_SPACE_ID_1, PARENT_NODE_ID_1, QUERY_SHARED_SPACE_DOCUMENT, OrderListConfigurationType.AscendingFileSize)
            .toList(ArrayList())

        assertThat(states).hasSize(2)
        assertThat(states[0](INIT_STATE))
            .isEqualTo(LOADING_STATE)
        assertThat(states[1](LOADING_STATE))
            .isEqualTo(expectedState)
    }

    @Test
    fun searchShouldReturnSuccessStateWithDescendingFileSizeMatchedListDocument() = runBlockingTest {
        `when`(sharedSpacesDocumentRepository.searchSharedSpaceDocuments(
            SHARED_SPACE_ID_1, PARENT_NODE_ID_1, QUERY_SHARED_SPACE_DOCUMENT))
            .thenAnswer { listOf(WORK_GROUP_DOCUMENT_1, WORK_GROUP_DOCUMENT_2) }

        val expectedState = Either.right(SearchSharedSpaceDocumentViewState(listOf(WORK_GROUP_DOCUMENT_2, WORK_GROUP_DOCUMENT_1)))

        val states = searchSharedSpaceDocumentInteractor(
            SHARED_SPACE_ID_1, PARENT_NODE_ID_1, QUERY_SHARED_SPACE_DOCUMENT, OrderListConfigurationType.DescendingFileSize)
            .toList(ArrayList())

        assertThat(states).hasSize(2)
        assertThat(states[0](INIT_STATE))
            .isEqualTo(LOADING_STATE)
        assertThat(states[1](LOADING_STATE))
            .isEqualTo(expectedState)
    }
}
