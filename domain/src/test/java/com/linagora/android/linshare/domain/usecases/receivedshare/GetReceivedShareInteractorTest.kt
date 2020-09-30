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
import com.google.common.truth.Truth.assertThat
import com.linagora.android.linshare.domain.repository.share.ReceivedShareRepository
import com.linagora.android.testshared.ShareFixtures.SHARE_1
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

class GetReceivedShareInteractorTest {

    @Mock
    private lateinit var receivedRepository: ReceivedShareRepository

    private lateinit var getReceivedShareInteractor: GetReceivedShareInteractor

    @BeforeEach
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        getReceivedShareInteractor = GetReceivedShareInteractor(receivedRepository)
    }

    @Test
    fun getReceivedShareShouldSuccessWithSingleReceivedShare() {
        runBlockingTest {
            `when`(receivedRepository.getReceivedShare(SHARE_1.shareId))
                .thenAnswer { SHARE_1 }

            assertThat(getReceivedShareInteractor(SHARE_1.shareId)
                .map { it(INIT_STATE) }
                .toList(ArrayList()))
                .containsExactly(LOADING_STATE, Either.Right(GetReceivedShareSuccess(SHARE_1)))
        }
    }

    @Test
    fun getReceivedShareShouldFailedWhenGetReceivedShareFailed() {
        runBlockingTest {
            val exception = RuntimeException("Get single received share failed")

            `when`(receivedRepository.getReceivedShare(SHARE_1.shareId))
                .thenThrow(exception)

            assertThat(getReceivedShareInteractor(SHARE_1.shareId)
                .map { it(INIT_STATE) }
                .toList(ArrayList()))
                .containsExactly(LOADING_STATE, Either.Left(GetReceivedShareFailure(exception)))
        }
    }
}
