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

package com.linagora.android.linshare.domain.usecases.auth

import com.google.common.truth.Truth.assertThat
import com.linagora.android.linshare.domain.network.SupportVersion
import com.linagora.android.linshare.domain.repository.authentication.AuthenticationRepository
import com.linagora.android.linshare.domain.usecases.auth.AuthenticationException.Companion.WRONG_CREDENTIAL
import com.linagora.android.linshare.domain.usecases.auth.AuthenticationException.Companion.WRONG_PASSWORD
import com.linagora.android.testshared.TestFixtures.Authentications.PASSWORD
import com.linagora.android.testshared.TestFixtures.Authentications.PASSWORD_2
import com.linagora.android.testshared.TestFixtures.Credentials.LINSHARE_BASE_URL
import com.linagora.android.testshared.TestFixtures.Credentials.LINSHARE_USER1
import com.linagora.android.testshared.TestFixtures.Credentials.SERVER_URL
import com.linagora.android.testshared.TestFixtures.Credentials.USER_NAME
import com.linagora.android.testshared.TestFixtures.Credentials.USER_NAME2
import com.linagora.android.testshared.TestFixtures.State.AUTHENTICATE_SUCCESS_STATE
import com.linagora.android.testshared.TestFixtures.State.CONNECT_ERROR_STATE
import com.linagora.android.testshared.TestFixtures.State.INIT_STATE
import com.linagora.android.testshared.TestFixtures.State.LOADING_STATE
import com.linagora.android.testshared.TestFixtures.State.SERVER_NOT_FOUND_STATE
import com.linagora.android.testshared.TestFixtures.State.SERVER_VERSION_4_NOT_FOUND_STATE
import com.linagora.android.testshared.TestFixtures.State.UNKNOW_ERROR_STATE
import com.linagora.android.testshared.TestFixtures.State.WRONG_CREDENTIAL_STATE
import com.linagora.android.testshared.TestFixtures.State.WRONG_PASSWORD_STATE
import com.linagora.android.testshared.TestFixtures.Tokens.TOKEN
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runBlockingTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations

class AuthenticateInteractorTest {

    @Mock
    private lateinit var authenticationRepository: AuthenticationRepository

    private lateinit var authenticateInteractor: AuthenticateInteractor

    @BeforeEach
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        authenticateInteractor = AuthenticateInteractor(authenticationRepository)
    }

    @Test
    fun authenticateShouldSuccessWithRightUsernamePassword() {
        runBlockingTest {
            `when`(authenticationRepository.retrievePermanentToken(LINSHARE_BASE_URL, SupportVersion.Version2, LINSHARE_USER1, PASSWORD))
                .thenAnswer { TOKEN }

            assertThat(authenticateInteractor(LINSHARE_BASE_URL, SupportVersion.Version2, LINSHARE_USER1, PASSWORD)
                    .map { it(INIT_STATE) }
                    .toList(ArrayList()))
                .containsExactly(LOADING_STATE, AUTHENTICATE_SUCCESS_STATE)
        }
    }

    @Test
    fun authenticateShouldFailureWithWrongUrl() {
        runBlockingTest {
            `when`(authenticationRepository.retrievePermanentToken(SERVER_URL, SupportVersion.Version2, USER_NAME, PASSWORD))
                .thenThrow(BadCredentials(WRONG_CREDENTIAL))

            assertThat(authenticateInteractor(SERVER_URL, SupportVersion.Version2, USER_NAME, PASSWORD)
                    .map { it(INIT_STATE) }
                    .toList(ArrayList()))
                .containsExactly(LOADING_STATE, WRONG_CREDENTIAL_STATE)
        }
    }

    @Test
    fun authenticateShouldFailureWithWrongUsername() {
        runBlockingTest {
            `when`(authenticationRepository.retrievePermanentToken(SERVER_URL, SupportVersion.Version2, USER_NAME2, PASSWORD))
                .thenThrow(BadCredentials(WRONG_CREDENTIAL))

            assertThat(authenticateInteractor(SERVER_URL, SupportVersion.Version2, USER_NAME2, PASSWORD)
                    .map { it(INIT_STATE) }
                    .toList(ArrayList()))
                .containsExactly(LOADING_STATE, WRONG_CREDENTIAL_STATE)
        }
    }

    @Test
    fun authenticateShouldFailureWithWrongPassword() {
        runBlockingTest {
            `when`(authenticationRepository.retrievePermanentToken(SERVER_URL, SupportVersion.Version2, USER_NAME, PASSWORD_2))
                .thenThrow(BadCredentials(WRONG_PASSWORD))

            assertThat(authenticateInteractor(SERVER_URL, SupportVersion.Version2, USER_NAME, PASSWORD_2)
                    .map { it(INIT_STATE) }
                    .toList(ArrayList()))
                .containsExactly(LOADING_STATE, WRONG_PASSWORD_STATE)
        }
    }

    @Test
    fun authenticateShouldFailureWithConnectError() {
        runBlockingTest {
            `when`(authenticationRepository.retrievePermanentToken(SERVER_URL, SupportVersion.Version2, USER_NAME, PASSWORD))
                .thenThrow(ConnectError)

            assertThat(authenticateInteractor(SERVER_URL, SupportVersion.Version2, USER_NAME, PASSWORD)
                    .map { it(INIT_STATE) }
                    .toList(ArrayList()))
                .containsExactly(LOADING_STATE, CONNECT_ERROR_STATE)
        }
    }

    @Test
    fun authenticateShouldFailureWithUnknownError() {
        runBlockingTest {
            `when`(authenticationRepository.retrievePermanentToken(SERVER_URL, SupportVersion.Version2, USER_NAME, PASSWORD))
                .thenThrow(UnknownError)

            assertThat(authenticateInteractor(SERVER_URL, SupportVersion.Version2, USER_NAME, PASSWORD)
                .map { it(INIT_STATE) }
                .toList(ArrayList()))
                .containsExactly(LOADING_STATE, UNKNOW_ERROR_STATE)
        }
    }

    @Test
    fun authenticateShouldFailureWithServerNotFound() {
        runBlockingTest {
            `when`(authenticationRepository.retrievePermanentToken(SERVER_URL, SupportVersion.Version2, USER_NAME, PASSWORD))
                .thenThrow(ServerNotFoundException(SupportVersion.Version2))

            assertThat(authenticateInteractor(SERVER_URL, SupportVersion.Version2, USER_NAME, PASSWORD)
                .map { it(INIT_STATE) }
                .toList(ArrayList()))
                .containsExactly(LOADING_STATE, SERVER_NOT_FOUND_STATE)
        }
    }

    @Test
    fun authenticateWithVersion4ShouldFailureWithServerNotFound() {
        runBlockingTest {
            `when`(authenticationRepository.retrievePermanentToken(SERVER_URL, SupportVersion.Version4, USER_NAME, PASSWORD))
                .thenThrow(ServerNotFoundException(SupportVersion.Version4))

            val states = authenticateInteractor(SERVER_URL, SupportVersion.Version4, USER_NAME, PASSWORD)
                .map { it(INIT_STATE) }
                .toList(ArrayList())

            assertThat(states).containsExactly(LOADING_STATE, SERVER_VERSION_4_NOT_FOUND_STATE)
        }
    }
}
