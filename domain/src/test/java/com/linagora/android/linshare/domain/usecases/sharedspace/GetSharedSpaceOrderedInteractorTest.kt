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
import com.google.common.truth.Truth
import com.linagora.android.linshare.domain.model.order.OrderListConfigurationType
import com.linagora.android.linshare.domain.model.sharedspace.SharedSpaceNodeNested
import com.linagora.android.linshare.domain.repository.sharedspace.SharedSpaceRepository
import com.linagora.android.testshared.SharedSpaceFixtures
import com.linagora.android.testshared.TestFixtures
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runBlockingTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations

class GetSharedSpaceOrderedInteractorTest {
    @Mock
    private lateinit var sharedSpaceRepository: SharedSpaceRepository

    private lateinit var getSharedSpaceOrderedInteractor: GetSharedSpaceOrderedInteractor

    @BeforeEach
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        getSharedSpaceOrderedInteractor = GetSharedSpaceOrderedInteractor(sharedSpaceRepository)
    }

    @Test
    fun getSharedSpaceListShouldSuccessWithSharedSpaceListOrderedByNameAscending() {
        runBlockingTest {
            Mockito.`when`(sharedSpaceRepository.getSharedSpaces())
                .thenAnswer { listOf(SharedSpaceFixtures.SHARED_SPACE_1, SharedSpaceFixtures.SHARED_SPACE_5,
                    SharedSpaceFixtures.SHARED_SPACE_2) }

            val sortedAscendingName = Either.right(SharedSpaceViewState(listOf(
                SharedSpaceFixtures.SHARED_SPACE_1,
                SharedSpaceFixtures.SHARED_SPACE_2,
                SharedSpaceFixtures.SHARED_SPACE_5
            )))

            Truth.assertThat(getSharedSpaceOrderedInteractor(OrderListConfigurationType.AscendingName)
                    .map { it(TestFixtures.State.INIT_STATE) }
                    .toList(ArrayList()))
                .containsExactly(TestFixtures.State.LOADING_STATE, sortedAscendingName)
        }
    }

    @Test
    fun getSharedSpaceListShouldSuccessWithSharedSpaceListOrderedByNameDescending() {
        runBlockingTest {
            Mockito.`when`(sharedSpaceRepository.getSharedSpaces())
                .thenAnswer { listOf(SharedSpaceFixtures.SHARED_SPACE_1, SharedSpaceFixtures.SHARED_SPACE_5,
                    SharedSpaceFixtures.SHARED_SPACE_2) }

            val sortedDescendingName = Either.right(SharedSpaceViewState(listOf(
                SharedSpaceFixtures.SHARED_SPACE_5,
                SharedSpaceFixtures.SHARED_SPACE_2,
                SharedSpaceFixtures.SHARED_SPACE_1
            )))

            Truth.assertThat(getSharedSpaceOrderedInteractor(OrderListConfigurationType.DescendingName)
                    .map { it(TestFixtures.State.INIT_STATE) }
                    .toList(ArrayList()))
                .containsExactly(TestFixtures.State.LOADING_STATE, sortedDescendingName)
        }
    }

    @Test
    fun getSharedSpaceListShouldSuccessWithEmptySharedSpaceListOrderedByNameAscending() {
        runBlockingTest {
            Mockito.`when`(sharedSpaceRepository.getSharedSpaces())
                .thenAnswer { emptyList<SharedSpaceNodeNested>() }

            Truth.assertThat(getSharedSpaceOrderedInteractor(OrderListConfigurationType.AscendingName)
                    .map { it(TestFixtures.State.INIT_STATE) }
                    .toList(ArrayList()))
                .containsExactly(
                    TestFixtures.State.LOADING_STATE,
                    SharedSpaceFixtures.EMPTY_SHARED_SPACE_LIST_VIEW_STATE
                )
        }
    }

    @Test
    fun getSharedSpaceListShouldSuccessWithEmptySharedSpaceListOrderedByNameDescending() {
        runBlockingTest {
            Mockito.`when`(sharedSpaceRepository.getSharedSpaces())
                .thenAnswer { emptyList<SharedSpaceNodeNested>() }

            Truth.assertThat(getSharedSpaceOrderedInteractor(OrderListConfigurationType.DescendingName)
                    .map { it(TestFixtures.State.INIT_STATE) }
                    .toList(ArrayList()))
                .containsExactly(
                    TestFixtures.State.LOADING_STATE,
                    SharedSpaceFixtures.EMPTY_SHARED_SPACE_LIST_VIEW_STATE
                )
        }
    }

    @Test
    fun getSharedSpaceListShouldFailedWhenGetSharedSpaceListFailed() {
        runBlockingTest {
            val exception = RuntimeException("get shared space failed")

            Mockito.`when`(sharedSpaceRepository.getSharedSpaces())
                .thenThrow(exception)

            Truth.assertThat(getSharedSpaceOrderedInteractor(OrderListConfigurationType.DescendingName)
                    .map { it(TestFixtures.State.INIT_STATE) }
                    .toList(ArrayList()))
                .containsExactly(TestFixtures.State.LOADING_STATE, Either.Left(SharedSpaceFailure(exception)))
        }
    }
}
