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

package com.linagora.android.testshared.repository.authentication

import com.google.common.truth.Truth.assertThat
import com.linagora.android.linshare.domain.repository.authentication.AuthenticationRepository
import com.linagora.android.linshare.domain.usecases.auth.AuthenticationException.Companion.WRONG_CREDENTIAL
import com.linagora.android.linshare.domain.usecases.auth.AuthenticationException.Companion.WRONG_PASSWORD
import com.linagora.android.linshare.domain.usecases.auth.BadCredentials
import com.linagora.android.testshared.TestFixtures.Authentications.LINSHARE_PASSWORD1
import com.linagora.android.testshared.TestFixtures.Authentications.PASSWORD_2
import com.linagora.android.testshared.TestFixtures.Credentials.LINSHARE_BASE_URL
import com.linagora.android.testshared.TestFixtures.Credentials.LINSHARE_USER1
import com.linagora.android.testshared.TestFixtures.Credentials.SERVER_URL
import com.linagora.android.testshared.TestFixtures.Credentials.USER_NAME2
import com.linagora.android.testshared.TestFixtures.Tokens.TOKEN
import com.linagora.android.testshared.TestFixtures.Tokens.TOKEN_2
import kotlinx.coroutines.test.runBlockingTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

abstract class AuthenticationRepositoryContract {

    abstract val authenticationRepository: AuthenticationRepository

    @Test
    open fun retrievePermanentTokenShouldSuccessWithRightUsernamePassword() {
        runBlockingTest {
            val token = authenticationRepository.retrievePermanentToken(LINSHARE_BASE_URL, LINSHARE_USER1, LINSHARE_PASSWORD1)

            assertThat(token).isEqualTo(TOKEN)
        }
    }

    @Test
    open fun retrievePermanentTokenShouldFailureWithWrongUrl() {
        val exception = assertThrows<BadCredentials> {
            runBlockingTest {
                authenticationRepository.retrievePermanentToken(SERVER_URL, USER_NAME2, LINSHARE_PASSWORD1)
            }
        }
        assertThat(exception.message).isEqualTo(WRONG_CREDENTIAL)
    }

    @Test
    open fun retrievePermanentTokenShouldFailureWithWrongUsername() {
        val exception = assertThrows<BadCredentials> {
            runBlockingTest {
                authenticationRepository.retrievePermanentToken(LINSHARE_BASE_URL, USER_NAME2, LINSHARE_PASSWORD1)
            }
        }
        assertThat(exception.message).isEqualTo(WRONG_CREDENTIAL)
    }

    @Test
    open fun retrievePermanentTokenShouldFailureWithWrongPassword() {
        val exception = assertThrows<BadCredentials> {
            runBlockingTest {
                authenticationRepository.retrievePermanentToken(LINSHARE_BASE_URL, LINSHARE_USER1, PASSWORD_2)
            }
        }
        assertThat(exception.message).isEqualTo(WRONG_PASSWORD)
    }

    @Test
    open fun deletePermanentTokenShouldSuccessWithExistToken() {
        runBlockingTest {
            assertThat(authenticationRepository.deletePermanentToken(TOKEN)).isTrue()
        }
    }

    @Test
    open fun deletePermanentTokenShouldSuccessWithNotExistToken() {
        runBlockingTest {
            assertThat(authenticationRepository.deletePermanentToken(TOKEN_2)).isTrue()
        }
    }
}
