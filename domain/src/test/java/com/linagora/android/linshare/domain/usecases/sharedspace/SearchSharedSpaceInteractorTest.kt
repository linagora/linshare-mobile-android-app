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
import com.linagora.android.linshare.domain.model.document.Document
import com.linagora.android.linshare.domain.model.order.OrderListConfigurationType
import com.linagora.android.linshare.domain.model.search.QueryString
import com.linagora.android.linshare.domain.repository.sharedspace.SharedSpaceRepository
import com.linagora.android.testshared.SharedSpaceFixtures.NOT_FOUND_SHARED_SPACE_STATE
import com.linagora.android.testshared.SharedSpaceFixtures.QUERY_STRING_SHARED_SPACE
import com.linagora.android.testshared.SharedSpaceFixtures.SEARCH_SHARED_SPACE_LIST_VIEW_STATE
import com.linagora.android.testshared.SharedSpaceFixtures.SHARED_SPACE_1
import com.linagora.android.testshared.SharedSpaceFixtures.SHARED_SPACE_2
import com.linagora.android.testshared.SharedSpaceFixtures.SHARED_SPACE_5
import com.linagora.android.testshared.SharedSpaceFixtures.SHARED_SPACE_6
import com.linagora.android.testshared.TestFixtures.Searchs.QUERY_STRING
import com.linagora.android.testshared.TestFixtures.State.INIT_STATE
import com.linagora.android.testshared.TestFixtures.State.LOADING_STATE
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runBlockingTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations

class SearchSharedSpaceInteractorTest {

    @Mock
    lateinit var sharedSpaceRepository: SharedSpaceRepository

    private lateinit var searchSharedSpaceInteractor: SearchSharedSpaceInteractor

    @BeforeEach
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        searchSharedSpaceInteractor = SearchSharedSpaceInteractor(sharedSpaceRepository)
    }

    @Test
    fun searchShouldSuccessReturnSearchResults() {
        runBlockingTest {
            `when`(sharedSpaceRepository.search(QUERY_STRING))
                .thenAnswer { listOf(SHARED_SPACE_1, SHARED_SPACE_2) }

            val states = searchSharedSpaceInteractor(QUERY_STRING, OrderListConfigurationType.AscendingName)
                .toList(ArrayList())

            assertThat(states).hasSize(2)
            assertThat(states[0](INIT_STATE))
                .isEqualTo(LOADING_STATE)
            assertThat(states[1](LOADING_STATE))
                .isEqualTo(SEARCH_SHARED_SPACE_LIST_VIEW_STATE)
        }
    }

    @Test
    fun searchShouldReturnSearchInitialWhileQueryLengthIsLowerThanThree() {
        runBlockingTest {
            val states = searchSharedSpaceInteractor(QueryString("qu"), OrderListConfigurationType.AscendingName)
                .toList(ArrayList())

            assertThat(states).hasSize(1)
            assertThat(states[0](INIT_STATE))
                .isEqualTo(Either.right(SearchSharedSpaceInitial))
        }
    }

    @Test
    fun searchShouldReturnNotFound() {
        runBlockingTest {
            `when`(sharedSpaceRepository.search(QUERY_STRING))
                .thenAnswer { emptyList<Document>() }

            val states = searchSharedSpaceInteractor(QUERY_STRING_SHARED_SPACE, OrderListConfigurationType.AscendingName)
                .toList(ArrayList())

            assertThat(states).hasSize(2)
            assertThat(states[0](INIT_STATE))
                .isEqualTo(LOADING_STATE)
            assertThat(states[1](LOADING_STATE))
                .isEqualTo(NOT_FOUND_SHARED_SPACE_STATE)
        }
    }

    @Test
    fun searchShouldSuccessReturnSearchResultsWithAscendingName() {
        runBlockingTest {
            `when`(sharedSpaceRepository.search(QUERY_STRING))
                .thenAnswer { listOf(SHARED_SPACE_1, SHARED_SPACE_5, SHARED_SPACE_2) }

            val sortedAscendingName = Either.right(SearchSharedSpaceViewState(listOf(
                SHARED_SPACE_1,
                SHARED_SPACE_2,
                SHARED_SPACE_5
            )))

            val states = searchSharedSpaceInteractor(QUERY_STRING, OrderListConfigurationType.AscendingName)
                .toList(ArrayList())

            assertThat(states).hasSize(2)
            assertThat(states[0](INIT_STATE))
                .isEqualTo(LOADING_STATE)
            assertThat(states[1](LOADING_STATE))
                .isEqualTo(sortedAscendingName)
        }
    }

    @Test
    fun searchShouldSuccessReturnSearchResultsWithDescendingName() {
        runBlockingTest {
            `when`(sharedSpaceRepository.search(QUERY_STRING))
                .thenAnswer { listOf(SHARED_SPACE_1, SHARED_SPACE_5, SHARED_SPACE_2) }

            val sortedAscendingName = Either.right(SearchSharedSpaceViewState(listOf(
                SHARED_SPACE_5,
                SHARED_SPACE_2,
                SHARED_SPACE_1
            )))

            val states = searchSharedSpaceInteractor(QUERY_STRING, OrderListConfigurationType.DescendingName)
                .toList(ArrayList())

            assertThat(states).hasSize(2)
            assertThat(states[0](INIT_STATE))
                .isEqualTo(LOADING_STATE)
            assertThat(states[1](LOADING_STATE))
                .isEqualTo(sortedAscendingName)
        }
    }

    @Test
    fun searchShouldSuccessReturnSearchResultsWithAscendingCreationDate() {
        runBlockingTest {
            `when`(sharedSpaceRepository.search(QUERY_STRING))
                .thenAnswer { listOf(SHARED_SPACE_1, SHARED_SPACE_5, SHARED_SPACE_6) }

            val sortedAscendingName = Either.right(SearchSharedSpaceViewState(listOf(
                SHARED_SPACE_1,
                SHARED_SPACE_5,
                SHARED_SPACE_6
            )))

            val states = searchSharedSpaceInteractor(QUERY_STRING, OrderListConfigurationType.AscendingCreationDate)
                .toList(ArrayList())

            assertThat(states).hasSize(2)
            assertThat(states[0](INIT_STATE))
                .isEqualTo(LOADING_STATE)
            assertThat(states[1](LOADING_STATE))
                .isEqualTo(sortedAscendingName)
        }
    }

    @Test
    fun searchShouldSuccessReturnSearchResultsWithDescendingCreationDate() {
        runBlockingTest {
            `when`(sharedSpaceRepository.search(QUERY_STRING))
                .thenAnswer { listOf(SHARED_SPACE_1, SHARED_SPACE_5, SHARED_SPACE_6) }

            val sortedAscendingName = Either.right(SearchSharedSpaceViewState(listOf(
                SHARED_SPACE_6,
                SHARED_SPACE_1,
                SHARED_SPACE_5
            )))

            val states = searchSharedSpaceInteractor(QUERY_STRING, OrderListConfigurationType.DescendingCreationDate)
                .toList(ArrayList())

            assertThat(states).hasSize(2)
            assertThat(states[0](INIT_STATE))
                .isEqualTo(LOADING_STATE)
            assertThat(states[1](LOADING_STATE))
                .isEqualTo(sortedAscendingName)
        }
    }

    @Test
    fun searchShouldSuccessReturnSearchResultsWithAscendingModificationDate() {
        runBlockingTest {
            `when`(sharedSpaceRepository.search(QUERY_STRING))
                .thenAnswer { listOf(SHARED_SPACE_1, SHARED_SPACE_5, SHARED_SPACE_6) }

            val sortedAscendingName = Either.right(SearchSharedSpaceViewState(listOf(
                SHARED_SPACE_1,
                SHARED_SPACE_5,
                SHARED_SPACE_6
            )))

            val states = searchSharedSpaceInteractor(QUERY_STRING, OrderListConfigurationType.AscendingModificationDate)
                .toList(ArrayList())

            assertThat(states).hasSize(2)
            assertThat(states[0](INIT_STATE))
                .isEqualTo(LOADING_STATE)
            assertThat(states[1](LOADING_STATE))
                .isEqualTo(sortedAscendingName)
        }
    }

    @Test
    fun searchShouldSuccessReturnSearchResultsWithDescendingModificationDate() {
        runBlockingTest {
            `when`(sharedSpaceRepository.search(QUERY_STRING))
                .thenAnswer { listOf(SHARED_SPACE_1, SHARED_SPACE_5, SHARED_SPACE_6) }

            val sortedAscendingName = Either.right(SearchSharedSpaceViewState(listOf(
                SHARED_SPACE_6,
                SHARED_SPACE_1,
                SHARED_SPACE_5
            )))

            val states = searchSharedSpaceInteractor(QUERY_STRING, OrderListConfigurationType.DescendingModificationDate)
                .toList(ArrayList())

            assertThat(states).hasSize(2)
            assertThat(states[0](INIT_STATE))
                .isEqualTo(LOADING_STATE)
            assertThat(states[1](LOADING_STATE))
                .isEqualTo(sortedAscendingName)
        }
    }
}
