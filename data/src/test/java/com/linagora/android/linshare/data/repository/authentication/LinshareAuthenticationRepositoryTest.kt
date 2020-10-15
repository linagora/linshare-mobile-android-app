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

package com.linagora.android.linshare.data.repository.authentication

import com.google.common.truth.Truth.assertThat
import com.linagora.android.linshare.data.datasource.LinshareDataSource
import com.linagora.android.linshare.data.repository.credential.MemoryCredentialRepository
import com.linagora.android.linshare.data.repository.credential.MemoryTokenRepository
import com.linagora.android.linshare.domain.network.SupportVersion
import com.linagora.android.linshare.domain.repository.authentication.AuthenticationRepository
import com.linagora.android.linshare.domain.usecases.auth.AuthenticationException.Companion.CONNECT_ERROR
import com.linagora.android.linshare.domain.usecases.auth.AuthenticationException.Companion.EMPTY_TOKEN
import com.linagora.android.linshare.domain.usecases.auth.AuthenticationException.Companion.UNKNOWN
import com.linagora.android.linshare.domain.usecases.auth.AuthenticationException.Companion.WRONG_CREDENTIAL
import com.linagora.android.linshare.domain.usecases.auth.AuthenticationException.Companion.WRONG_PASSWORD
import com.linagora.android.linshare.domain.usecases.auth.BadCredentials
import com.linagora.android.linshare.domain.usecases.auth.ConnectError
import com.linagora.android.linshare.domain.usecases.auth.EmptyToken
import com.linagora.android.linshare.domain.usecases.auth.ServerNotFoundException
import com.linagora.android.linshare.domain.usecases.auth.UnknownError
import com.linagora.android.testshared.TestFixtures.Authentications.LINSHARE_PASSWORD1
import com.linagora.android.testshared.TestFixtures.Authentications.PASSWORD_2
import com.linagora.android.testshared.TestFixtures.Credentials.LINSHARE_BASE_URL
import com.linagora.android.testshared.TestFixtures.Credentials.LINSHARE_CREDENTIAL
import com.linagora.android.testshared.TestFixtures.Credentials.LINSHARE_CREDENTIAL_VERSION4
import com.linagora.android.testshared.TestFixtures.Credentials.LINSHARE_USER1
import com.linagora.android.testshared.TestFixtures.Credentials.SERVER_URL
import com.linagora.android.testshared.TestFixtures.Credentials.USER_NAME2
import com.linagora.android.testshared.TestFixtures.Tokens.TOKEN
import com.linagora.android.testshared.TestFixtures.Tokens.TOKEN_2
import com.linagora.android.testshared.repository.authentication.AuthenticationRepositoryContract
import kotlinx.coroutines.test.runBlockingTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations

class LinshareAuthenticationRepositoryTest : AuthenticationRepositoryContract() {

    private lateinit var linshareAuthenticationRepository: LinshareAuthenticationRepository
    private lateinit var credentialRepository: MemoryCredentialRepository
    private lateinit var tokenRepository: MemoryTokenRepository

    @Mock
    private lateinit var linshareDataSource: LinshareDataSource

    override val authenticationRepository: AuthenticationRepository
        get() = linshareAuthenticationRepository

    @BeforeEach
    fun setUp() {
        MockitoAnnotations.initMocks(this)

        credentialRepository = MemoryCredentialRepository()
        tokenRepository = MemoryTokenRepository()

        linshareAuthenticationRepository = LinshareAuthenticationRepository(
            linshareDataSource,
            credentialRepository,
            tokenRepository
        )
    }

    @Test
    override fun retrievePermanentTokenShouldSuccessWithRightUsernamePassword() {
        runBlockingTest {
            `when`(linshareDataSource.retrievePermanentToken(
                    baseUrl = LINSHARE_BASE_URL,
                    supportVersion = SupportVersion.Version2,
                    username = LINSHARE_USER1,
                    password = LINSHARE_PASSWORD1))
                .thenAnswer { TOKEN }

            super.retrievePermanentTokenShouldSuccessWithRightUsernamePassword()
            assertThat(credentialRepository.getAllCredential()).containsExactly(LINSHARE_CREDENTIAL)
            assertThat(tokenRepository.getToken(LINSHARE_CREDENTIAL))
        }
    }

    override fun retrievePermanentTokenVersion4ShouldSuccessWithRightUsernamePassword() {
        runBlockingTest {
            `when`(linshareDataSource.retrievePermanentToken(
                    baseUrl = LINSHARE_BASE_URL,
                    supportVersion = SupportVersion.Version4,
                    username = LINSHARE_USER1,
                    password = LINSHARE_PASSWORD1))
                .thenAnswer { TOKEN }

            super.retrievePermanentTokenShouldSuccessWithRightUsernamePassword()

            assertThat(credentialRepository.getAllCredential()).containsExactly(LINSHARE_CREDENTIAL_VERSION4)
            assertThat(tokenRepository.getToken(LINSHARE_CREDENTIAL_VERSION4))
        }
    }

    @Test
    override fun retrievePermanentTokenShouldFailureWithWrongUrl() {
        runBlockingTest {
            `when`(linshareDataSource.retrievePermanentToken(
                    baseUrl = SERVER_URL,
                    supportVersion = SupportVersion.Version2,
                    username = USER_NAME2,
                    password = LINSHARE_PASSWORD1))
                .thenThrow(BadCredentials(WRONG_CREDENTIAL))
            super.retrievePermanentTokenShouldFailureWithWrongUrl()
        }
    }

    override fun retrievePermanentTokenVersion4ShouldFailureWithWrongUrl() {
        runBlockingTest {
            `when`(linshareDataSource.retrievePermanentToken(
                    baseUrl = SERVER_URL,
                    supportVersion = SupportVersion.Version4,
                    username = USER_NAME2,
                    password = LINSHARE_PASSWORD1))
                .thenThrow(BadCredentials(WRONG_CREDENTIAL))
            super.retrievePermanentTokenShouldFailureWithWrongUrl()
        }
    }

    @Test
    override fun retrievePermanentTokenShouldFailureWithWrongUsername() {
        runBlockingTest {
            `when`(linshareDataSource.retrievePermanentToken(
                    baseUrl = LINSHARE_BASE_URL,
                    supportVersion = SupportVersion.Version2,
                    username = USER_NAME2,
                    password = LINSHARE_PASSWORD1))
                .thenThrow(BadCredentials(WRONG_CREDENTIAL))
            super.retrievePermanentTokenShouldFailureWithWrongUsername()
        }
    }

    override fun retrievePermanentTokenVersion4ShouldFailureWithWrongUsername() {
        runBlockingTest {
            `when`(linshareDataSource.retrievePermanentToken(
                    baseUrl = LINSHARE_BASE_URL,
                    supportVersion = SupportVersion.Version2,
                    username = USER_NAME2,
                    password = LINSHARE_PASSWORD1))
                .thenThrow(BadCredentials(WRONG_CREDENTIAL))
            super.retrievePermanentTokenShouldFailureWithWrongUsername()
        }
    }

    @Test
    override fun retrievePermanentTokenShouldFailureWithWrongPassword() {
        runBlockingTest {
            `when`(linshareDataSource.retrievePermanentToken(
                    baseUrl = LINSHARE_BASE_URL,
                    supportVersion = SupportVersion.Version2,
                    username = LINSHARE_USER1,
                    password = PASSWORD_2))
                .thenThrow(BadCredentials(WRONG_PASSWORD))

            super.retrievePermanentTokenShouldFailureWithWrongPassword()
        }
    }

    override fun retrievePermanentTokenVersion4ShouldFailureWithWrongPassword() {
        runBlockingTest {
            `when`(linshareDataSource.retrievePermanentToken(
                    baseUrl = LINSHARE_BASE_URL,
                    supportVersion = SupportVersion.Version4,
                    username = LINSHARE_USER1,
                    password = PASSWORD_2))
                .thenThrow(BadCredentials(WRONG_PASSWORD))

            super.retrievePermanentTokenShouldFailureWithWrongPassword()
        }
    }

    @Test
    fun retrievePermanentTokenShouldFailureWithEmptyToken() {
        runBlockingTest {
            `when`(linshareDataSource.retrievePermanentToken(
                    baseUrl = LINSHARE_BASE_URL,
                    supportVersion = SupportVersion.Version2,
                    username = LINSHARE_USER1,
                    password = LINSHARE_PASSWORD1))
                .thenThrow(EmptyToken)

            val exception = assertThrows<EmptyToken> {
                runBlockingTest {
                    authenticationRepository.retrievePermanentToken(LINSHARE_BASE_URL, SupportVersion.Version2, LINSHARE_USER1, LINSHARE_PASSWORD1)
                }
            }
            assertThat(exception.message).isEqualTo(EMPTY_TOKEN)
        }
    }

    @Test
    fun retrievePermanentTokenShouldFailureWithServerNotFound() {
        runBlockingTest {
            `when`(linshareDataSource.retrievePermanentToken(
                    baseUrl = LINSHARE_BASE_URL,
                    supportVersion = SupportVersion.Version2,
                    username = LINSHARE_USER1,
                    password = LINSHARE_PASSWORD1))
                .thenThrow(ServerNotFoundException(SupportVersion.Version2))

            val exception = assertThrows<ServerNotFoundException> {
                runBlockingTest {
                    authenticationRepository.retrievePermanentToken(LINSHARE_BASE_URL, SupportVersion.Version2, LINSHARE_USER1, LINSHARE_PASSWORD1)
                }
            }
            assertThat(exception.supportVersion).isEqualTo(SupportVersion.Version2)
        }
    }

    @Test
    fun retrievePermanentTokenVersion4ShouldFailureWithServerNotFound() {
        runBlockingTest {
            `when`(linshareDataSource.retrievePermanentToken(
                    baseUrl = LINSHARE_BASE_URL,
                    supportVersion = SupportVersion.Version4,
                    username = LINSHARE_USER1,
                    password = LINSHARE_PASSWORD1))
                .thenThrow(ServerNotFoundException(SupportVersion.Version4))

            val exception = assertThrows<ServerNotFoundException> {
                runBlockingTest {
                    authenticationRepository.retrievePermanentToken(LINSHARE_BASE_URL, SupportVersion.Version4, LINSHARE_USER1, LINSHARE_PASSWORD1)
                }
            }
            assertThat(exception.supportVersion).isEqualTo(SupportVersion.Version4)
        }
    }

    @Test
    fun retrievePermanentTokenShouldFailureWithUnknownError() {
        runBlockingTest {
            `when`(linshareDataSource.retrievePermanentToken(
                    baseUrl = LINSHARE_BASE_URL,
                    supportVersion = SupportVersion.Version2,
                    username = LINSHARE_USER1,
                    password = LINSHARE_PASSWORD1))
                .thenThrow(UnknownError)

            val exception = assertThrows<UnknownError> {
                runBlockingTest {
                    authenticationRepository.retrievePermanentToken(LINSHARE_BASE_URL, SupportVersion.Version2, LINSHARE_USER1, LINSHARE_PASSWORD1)
                }
            }
            assertThat(exception.message).isEqualTo(UNKNOWN)
        }
    }

    @Test
    fun retrievePermanentTokenShouldFailureWithConnectError() {
        runBlockingTest {
            `when`(linshareDataSource.retrievePermanentToken(
                    baseUrl = LINSHARE_BASE_URL,
                    supportVersion = SupportVersion.Version2,
                    username = LINSHARE_USER1,
                    password = LINSHARE_PASSWORD1))
                .thenThrow(ConnectError)

            val exception = assertThrows<ConnectError> {
                runBlockingTest {
                    authenticationRepository.retrievePermanentToken(LINSHARE_BASE_URL, SupportVersion.Version2, LINSHARE_USER1, LINSHARE_PASSWORD1)
                }
            }
            assertThat(exception.message).isEqualTo(CONNECT_ERROR)
        }
    }

    @Test
    override fun deletePermanentTokenShouldSuccessWithExistToken() {
        runBlockingTest {
            `when`(linshareDataSource.deletePermanentToken(TOKEN))
                .thenAnswer { true }

            super.deletePermanentTokenShouldSuccessWithExistToken()
        }
    }

    @Test
    override fun deletePermanentTokenShouldSuccessWithNotExistToken() {
        runBlockingTest {
            `when`(linshareDataSource.deletePermanentToken(TOKEN_2))
                .thenAnswer { true }
            super.deletePermanentTokenShouldSuccessWithNotExistToken()
        }
    }
}
