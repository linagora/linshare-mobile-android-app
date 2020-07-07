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

package com.linagora.android.linshare.domain.usecases.quota

import com.google.common.truth.Truth.assertThat
import com.linagora.android.linshare.domain.DomainFixtures.DOCUMENT_REQUEST
import com.linagora.android.linshare.domain.DomainFixtures.DOCUMENT_REQUEST_BIG_SIZE
import com.linagora.android.linshare.domain.repository.user.QuotaRepository
import com.linagora.android.testshared.TestFixtures.Accounts.LOW_QUOTA
import com.linagora.android.testshared.TestFixtures.Accounts.QUOTA
import com.linagora.android.testshared.TestFixtures.Accounts.QUOTA_UUID
import com.linagora.android.testshared.TestFixtures.State.EXCEED_MAX_FILE_SIZE
import com.linagora.android.testshared.TestFixtures.State.INIT_STATE
import com.linagora.android.testshared.TestFixtures.State.LOADING_STATE
import com.linagora.android.testshared.TestFixtures.State.QUOTA_ACCOUNT_NO_MORE_AVAILABLE_SPACE
import com.linagora.android.testshared.TestFixtures.State.VALID_QUOTA_ACCOUNT_STATE
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runBlockingTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations

class EnoughQuotaToUploadInteractorTest {

    @Mock
    lateinit var quotaRepository: QuotaRepository

    private lateinit var enoughQuotaToUploadInteractor: EnoughQuotaToUploadInteractor

    @BeforeEach
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        enoughQuotaToUploadInteractor = EnoughQuotaToUploadInteractor(quotaRepository)
    }

    @Test
    fun enoughQuotaShouldReturnValidAccountQuotaWhenEnoughQuotaForDocument() {
        runBlockingTest {
            Mockito.`when`(quotaRepository.findQuota(QUOTA_UUID))
                .thenAnswer { QUOTA }

            val states = enoughQuotaToUploadInteractor(QUOTA_UUID, DOCUMENT_REQUEST)
                .toList(ArrayList())

            assertThat(states).hasSize(2)
            assertThat(states[0](INIT_STATE))
                .isEqualTo(LOADING_STATE)

            assertThat(states[1](LOADING_STATE))
                .isEqualTo(VALID_QUOTA_ACCOUNT_STATE)
        }
    }

    @Test
    fun enoughQuotaShouldReturnExceedMaxFileSizeWhenDocumentSizeExceed() {
        runBlockingTest {
            Mockito.`when`(quotaRepository.findQuota(QUOTA_UUID))
                .thenAnswer { LOW_QUOTA }

            val states = enoughQuotaToUploadInteractor(QUOTA_UUID, DOCUMENT_REQUEST_BIG_SIZE)
                .toList(ArrayList())

            assertThat(states).hasSize(2)
            assertThat(states[0](INIT_STATE))
                .isEqualTo(LOADING_STATE)

            assertThat(states[1](LOADING_STATE))
                .isEqualTo(EXCEED_MAX_FILE_SIZE)
        }
    }

    @Test
    fun enoughQuotaShouldReturnNotEnoughAccountQuotaWhenNoMoreSpaceAvailable() {
        runBlockingTest {
            Mockito.`when`(quotaRepository.findQuota(QUOTA_UUID))
                .thenAnswer { QUOTA }

            val states = enoughQuotaToUploadInteractor(QUOTA_UUID, DOCUMENT_REQUEST_BIG_SIZE)
                .toList(ArrayList())

            assertThat(states).hasSize(2)
            assertThat(states[0](INIT_STATE))
                .isEqualTo(LOADING_STATE)

            assertThat(states[1](LOADING_STATE))
                .isEqualTo(QUOTA_ACCOUNT_NO_MORE_AVAILABLE_SPACE)
        }
    }
}
