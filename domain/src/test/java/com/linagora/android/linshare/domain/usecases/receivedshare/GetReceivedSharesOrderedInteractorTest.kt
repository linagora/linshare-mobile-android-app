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

package com.linagora.android.linshare.domain.usecases.receivedshare

import arrow.core.Either
import com.google.common.truth.Truth
import com.linagora.android.linshare.domain.model.order.OrderListConfigurationType
import com.linagora.android.linshare.domain.model.share.Share
import com.linagora.android.linshare.domain.repository.share.ReceivedShareRepository
import com.linagora.android.testshared.ShareFixtures
import com.linagora.android.testshared.ShareFixtures.SHARE_1
import com.linagora.android.testshared.ShareFixtures.SHARE_3
import com.linagora.android.testshared.ShareFixtures.SHARE_4
import com.linagora.android.testshared.TestFixtures
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runBlockingTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations

class GetReceivedSharesOrderedInteractorTest {

    @Mock
    private lateinit var receivedRepository: ReceivedShareRepository

    private lateinit var getReceivedSharesOrderedInteractor: GetReceivedSharesOrderedInteractor

    @BeforeEach
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        getReceivedSharesOrderedInteractor = GetReceivedSharesOrderedInteractor(receivedRepository)
    }

    @Test
    fun getReceivedListShouldSuccessWithReceivedList() {
        runBlockingTest {
            Mockito.`when`(receivedRepository.getReceivedShares())
                .thenAnswer { listOf(ShareFixtures.SHARE_1, ShareFixtures.SHARE_2) }

            Truth.assertThat(getReceivedSharesOrderedInteractor(OrderListConfigurationType.AscendingName)
                .map { it(TestFixtures.State.INIT_STATE) }
                .toList(ArrayList()))
                .containsExactly(TestFixtures.State.LOADING_STATE, ShareFixtures.ALL_RECEIVED_STATE)
        }
    }

    @Test
    fun getReceivedListShouldFailedWhenGetReceivedListFailed() {
        runBlockingTest {
            val exception = RuntimeException("get list received failed")

            Mockito.`when`(receivedRepository.getReceivedShares())
                .thenThrow(exception)

            Truth.assertThat(getReceivedSharesOrderedInteractor(OrderListConfigurationType.AscendingName)
                .map { it(TestFixtures.State.INIT_STATE) }
                .toList(ArrayList()))
                .containsExactly(TestFixtures.State.LOADING_STATE, Either.Left(ReceivedSharesFailure(exception)))
        }
    }

    @Test
    fun getReceivedListShouldSuccessWithEmptyList() {
        runBlockingTest {
            Mockito.`when`(receivedRepository.getReceivedShares())
                .thenAnswer { emptyList<Share>() }

            Truth.assertThat(getReceivedSharesOrderedInteractor(OrderListConfigurationType.AscendingName)
                .map { it(TestFixtures.State.INIT_STATE) }
                .toList(ArrayList()))
                .containsExactly(TestFixtures.State.LOADING_STATE, Either.left(EmptyReceivedSharesViewState))
        }
    }

    @Test
    fun getReceivedListShouldSuccessSortedAscendingNameWithInputAscendingName() {
        runBlockingTest {
            Mockito.`when`(receivedRepository.getReceivedShares())
                .thenAnswer { listOf(SHARE_1, SHARE_3, SHARE_4) }

            val expectedState = Either.right(ReceivedSharesViewState(listOf(SHARE_1, SHARE_3, SHARE_4)))

            Truth.assertThat(getReceivedSharesOrderedInteractor(OrderListConfigurationType.AscendingName)
                .map { it(TestFixtures.State.INIT_STATE) }
                .toList(ArrayList()))
                .containsExactly(TestFixtures.State.LOADING_STATE, expectedState)
        }
    }

    @Test
    fun getReceivedListShouldSuccessSortedDescendingNameWithInputDescendingName() {
        runBlockingTest {
            Mockito.`when`(receivedRepository.getReceivedShares())
                .thenAnswer { listOf(SHARE_1, SHARE_3, SHARE_4) }

            val expectedState = Either.right(ReceivedSharesViewState(listOf(SHARE_4, SHARE_3, SHARE_1)))

            Truth.assertThat(getReceivedSharesOrderedInteractor(OrderListConfigurationType.DescendingName)
                .map { it(TestFixtures.State.INIT_STATE) }
                .toList(ArrayList()))
                .containsExactly(TestFixtures.State.LOADING_STATE, expectedState)
        }
    }

    @Test
    fun getReceivedListShouldSuccessSortedAscendingCreationDateWithInputAscendingCreationDate() {
        runBlockingTest {
            Mockito.`when`(receivedRepository.getReceivedShares())
                .thenAnswer { listOf(SHARE_1, SHARE_3, SHARE_4) }

            val expectedState = Either.right(ReceivedSharesViewState(listOf(SHARE_3, SHARE_4, SHARE_1)))

            Truth.assertThat(getReceivedSharesOrderedInteractor(OrderListConfigurationType.AscendingCreationDate)
                .map { it(TestFixtures.State.INIT_STATE) }
                .toList(ArrayList()))
                .containsExactly(TestFixtures.State.LOADING_STATE, expectedState)
        }
    }

    @Test
    fun getReceivedListShouldSuccessSortedDescendingCreationDateWithInputDescendingCreationDate() {
        runBlockingTest {
            Mockito.`when`(receivedRepository.getReceivedShares())
                .thenAnswer { listOf(SHARE_1, SHARE_3, SHARE_4) }

            val expectedState = Either.right(ReceivedSharesViewState(listOf(SHARE_1, SHARE_4, SHARE_3)))

            Truth.assertThat(getReceivedSharesOrderedInteractor(OrderListConfigurationType.DescendingCreationDate)
                .map { it(TestFixtures.State.INIT_STATE) }
                .toList(ArrayList()))
                .containsExactly(TestFixtures.State.LOADING_STATE, expectedState)
        }
    }

    @Test
    fun getReceivedListShouldSuccessSortedAscendingModificationDateWithInputAscendingModificationDate() {
        runBlockingTest {
            Mockito.`when`(receivedRepository.getReceivedShares())
                .thenAnswer { listOf(SHARE_1, SHARE_3, SHARE_4) }

            val expectedState = Either.right(ReceivedSharesViewState(listOf(SHARE_3, SHARE_4, SHARE_1)))

            Truth.assertThat(getReceivedSharesOrderedInteractor(OrderListConfigurationType.AscendingModificationDate)
                .map { it(TestFixtures.State.INIT_STATE) }
                .toList(ArrayList()))
                .containsExactly(TestFixtures.State.LOADING_STATE, expectedState)
        }
    }

    @Test
    fun getReceivedListShouldSuccessSortedDescendingModificationDateWithInputDescendingModificationDate() {
        runBlockingTest {
            Mockito.`when`(receivedRepository.getReceivedShares())
                .thenAnswer { listOf(SHARE_1, SHARE_3, SHARE_4) }

            val expectedState = Either.right(ReceivedSharesViewState(listOf(SHARE_1, SHARE_4, SHARE_3)))

            Truth.assertThat(getReceivedSharesOrderedInteractor(OrderListConfigurationType.DescendingModificationDate)
                .map { it(TestFixtures.State.INIT_STATE) }
                .toList(ArrayList()))
                .containsExactly(TestFixtures.State.LOADING_STATE, expectedState)
        }
    }

    @Test
    fun getReceivedListShouldSuccessSortedAscendingFileSizeWithInputAscendingFileSize() {
        runBlockingTest {
            Mockito.`when`(receivedRepository.getReceivedShares())
                .thenAnswer { listOf(SHARE_1, SHARE_3, SHARE_4) }

            val expectedState = Either.right(ReceivedSharesViewState(listOf(SHARE_1, SHARE_3, SHARE_4)))

            Truth.assertThat(getReceivedSharesOrderedInteractor(OrderListConfigurationType.AscendingFileSize)
                .map { it(TestFixtures.State.INIT_STATE) }
                .toList(ArrayList()))
                .containsExactly(TestFixtures.State.LOADING_STATE, expectedState)
        }
    }

    @Test
    fun getReceivedListShouldSuccessSortedDescendingFileSizeWithInputDescendingFileSize() {
        runBlockingTest {
            Mockito.`when`(receivedRepository.getReceivedShares())
                .thenAnswer { listOf(SHARE_1, SHARE_3, SHARE_4) }

            val expectedState = Either.right(ReceivedSharesViewState(listOf(SHARE_4, SHARE_1, SHARE_3)))

            Truth.assertThat(getReceivedSharesOrderedInteractor(OrderListConfigurationType.DescendingFileSize)
                .map { it(TestFixtures.State.INIT_STATE) }
                .toList(ArrayList()))
                .containsExactly(TestFixtures.State.LOADING_STATE, expectedState)
        }
    }

    @Test
    fun getReceivedListShouldSuccessSortedAscendingSenderWithInputAscendingSender() {
        runBlockingTest {
            Mockito.`when`(receivedRepository.getReceivedShares())
                .thenAnswer { listOf(SHARE_1, SHARE_3, SHARE_4) }

            val expectedState = Either.right(ReceivedSharesViewState(listOf(SHARE_4, SHARE_1, SHARE_3)))

            Truth.assertThat(getReceivedSharesOrderedInteractor(OrderListConfigurationType.AscendingSender)
                .map { it(TestFixtures.State.INIT_STATE) }
                .toList(ArrayList()))
                .containsExactly(TestFixtures.State.LOADING_STATE, expectedState)
        }
    }

    @Test
    fun getReceivedListShouldSuccessSortedDescendingSenderWithInputDescendingSender() {
        runBlockingTest {
            Mockito.`when`(receivedRepository.getReceivedShares())
                .thenAnswer { listOf(SHARE_1, SHARE_3, SHARE_4) }

            val expectedState = Either.right(ReceivedSharesViewState(listOf(SHARE_1, SHARE_3, SHARE_4)))

            Truth.assertThat(getReceivedSharesOrderedInteractor(OrderListConfigurationType.DescendingSender)
                .map { it(TestFixtures.State.INIT_STATE) }
                .toList(ArrayList()))
                .containsExactly(TestFixtures.State.LOADING_STATE, expectedState)
        }
    }
}
