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

package com.linagora.android.linshare.domain.usecases.account

import arrow.core.Either
import com.google.common.truth.Truth.assertThat
import com.linagora.android.linshare.domain.network.manager.AuthorizationManager
import com.linagora.android.linshare.domain.repository.user.AuditUserRepository
import com.linagora.android.linshare.domain.repository.user.QuotaRepository
import com.linagora.android.linshare.domain.repository.user.UserRepository
import com.linagora.android.linshare.domain.usecases.utils.Failure
import com.linagora.android.linshare.domain.usecases.utils.State
import com.linagora.android.linshare.domain.usecases.utils.Success
import com.linagora.android.linshare.domain.utils.emitState
import com.linagora.android.testshared.TestFixtures.Accounts.LAST_LOGIN
import com.linagora.android.testshared.TestFixtures.Accounts.LINSHARE_USER
import com.linagora.android.testshared.TestFixtures.Accounts.QUOTA
import com.linagora.android.testshared.TestFixtures.Accounts.QUOTA_UUID
import com.linagora.android.testshared.TestFixtures.Credentials.CREDENTIAL
import com.linagora.android.testshared.TestFixtures.Credentials.LINSHARE_CREDENTIAL
import com.linagora.android.testshared.TestFixtures.State.ACCOUNT_DETAILS_SUCCESS_STATE
import com.linagora.android.testshared.TestFixtures.State.ACCOUNT_DETAILS_WITH_CREDENTIAL
import com.linagora.android.testshared.TestFixtures.State.AUTHENTICATE_SUCCESS_STATE
import com.linagora.android.testshared.TestFixtures.State.EMPTY_TOKEN_STATE
import com.linagora.android.testshared.TestFixtures.State.ERROR_STATE
import com.linagora.android.testshared.TestFixtures.State.INIT_STATE
import com.linagora.android.testshared.TestFixtures.State.LOADING_STATE
import com.linagora.android.testshared.TestFixtures.Tokens.TOKEN
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runBlockingTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify
import org.mockito.Mockito.verifyNoInteractions
import org.mockito.MockitoAnnotations

class GetAccountDetailsInteractorTest {

    @Mock
    lateinit var getToken: GetTokenInteractor

    @Mock
    lateinit var authorizationManager: AuthorizationManager

    @Mock
    lateinit var auditUserRepository: AuditUserRepository

    @Mock
    lateinit var userRepository: UserRepository

    @Mock
    lateinit var quotaRepository: QuotaRepository

    private lateinit var getAccountDetails: GetAccountDetailsInteractor

    @BeforeEach
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        getAccountDetails = GetAccountDetailsInteractor(
            getToken = getToken,
            authorizationManager = authorizationManager,
            auditUserRepository = auditUserRepository,
            userRepository = userRepository,
            quotaRepository = quotaRepository
        )
    }

    @Test
    fun getAccountDetailsShouldSuccessWithRightCredential() {
        runBlockingTest {
            `when`(getToken(LINSHARE_CREDENTIAL))
                .then {
                    flow<State<Either<Failure, Success>>> {
                        emitState { LOADING_STATE }
                        emitState { AUTHENTICATE_SUCCESS_STATE }
                    }
                }

            `when`(auditUserRepository.getLastLogin())
                .thenAnswer { LAST_LOGIN }

            `when`(userRepository.getAuthorizedUser())
                .thenAnswer { LINSHARE_USER }

            `when`(quotaRepository.findQuota(QUOTA_UUID))
                .thenAnswer { QUOTA }

            val states = getAccountDetails(LINSHARE_CREDENTIAL)
                .toList(ArrayList())

            assertThat(states).hasSize(5)

            assertThat(states[0](INIT_STATE))
                .isEqualTo(LOADING_STATE)

            assertThat(states[1](LOADING_STATE))
                .isEqualTo(AUTHENTICATE_SUCCESS_STATE)

            assertThat(states[2](AUTHENTICATE_SUCCESS_STATE))
                .isEqualTo(Either.right(
                    ACCOUNT_DETAILS_WITH_CREDENTIAL.copy(lastLogin = LAST_LOGIN)
                ))

            assertThat(states[3](Either.right(
                    ACCOUNT_DETAILS_WITH_CREDENTIAL.copy(lastLogin = LAST_LOGIN))))
                .isEqualTo(Either.right(
                    ACCOUNT_DETAILS_WITH_CREDENTIAL.copy(
                        lastLogin = LAST_LOGIN,
                        user = LINSHARE_USER
                    )
                ))

            assertThat(states[4](Either.right(
                    ACCOUNT_DETAILS_WITH_CREDENTIAL.copy(
                        lastLogin = LAST_LOGIN,
                        user = LINSHARE_USER
                    ))))
                .isEqualTo(ACCOUNT_DETAILS_SUCCESS_STATE)

            verify(authorizationManager).updateToken(TOKEN)
        }
    }

    @Test
    fun getAccountDetailsShouldFailedWithWrongCredential() {
        runBlockingTest {
            `when`(getToken(CREDENTIAL))
                .then {
                    flow<State<Either<Failure, Success>>> {
                        emitState { LOADING_STATE }
                        emitState { EMPTY_TOKEN_STATE }
                    }
                }

            val states = getAccountDetails(CREDENTIAL)
                .toList(ArrayList())

            assertThat(states).hasSize(2)
            assertThat(states[0](INIT_STATE))
                .isEqualTo(LOADING_STATE)
            assertThat(states[1](LOADING_STATE))
                .isEqualTo(EMPTY_TOKEN_STATE)

            verifyNoInteractions(authorizationManager)
        }
    }

    @Test
    fun getAccountDetailsShouldErrorInLoadingLastLoginWhenLastLoginFailed() {
        runBlockingTest {
            `when`(getToken(LINSHARE_CREDENTIAL))
                .then {
                    flow<State<Either<Failure, Success>>> {
                        emitState { LOADING_STATE }
                        emitState { AUTHENTICATE_SUCCESS_STATE }
                    }
                }

            `when`(auditUserRepository.getLastLogin())
                .thenAnswer { null }

            `when`(userRepository.getAuthorizedUser())
                .thenAnswer { LINSHARE_USER }

            `when`(quotaRepository.findQuota(QUOTA_UUID))
                .thenAnswer { QUOTA }

            val states = getAccountDetails(LINSHARE_CREDENTIAL)
                .toList(ArrayList())

            assertThat(states).hasSize(5)

            assertThat(states[0](INIT_STATE))
                .isEqualTo(LOADING_STATE)

            assertThat(states[1](LOADING_STATE))
                .isEqualTo(AUTHENTICATE_SUCCESS_STATE)

            assertThat(states[2](AUTHENTICATE_SUCCESS_STATE))
                .isEqualTo(ERROR_STATE)

            assertThat(states[3](ERROR_STATE))
                .isEqualTo(Either.right(
                    ACCOUNT_DETAILS_WITH_CREDENTIAL.copy(
                        user = LINSHARE_USER
                    ))
                )

            assertThat(states[4](Either.right(
                    ACCOUNT_DETAILS_WITH_CREDENTIAL.copy(
                        user = LINSHARE_USER
                    ))))
                .isEqualTo(Either.right(
                    ACCOUNT_DETAILS_WITH_CREDENTIAL.copy(
                        user = LINSHARE_USER,
                        quota = QUOTA
                    ))
                )

            verify(authorizationManager).updateToken(TOKEN)
        }
    }

    @Test
    fun getAccountDetailsShouldErrorInLoadingLastLoginWhenGetAuthorizedUserFailed() {
        runBlockingTest {
            `when`(getToken(LINSHARE_CREDENTIAL))
                .then {
                    flow<State<Either<Failure, Success>>> {
                        emitState { LOADING_STATE }
                        emitState { AUTHENTICATE_SUCCESS_STATE }
                    }
                }

            `when`(auditUserRepository.getLastLogin())
                .thenAnswer { LAST_LOGIN }

            `when`(userRepository.getAuthorizedUser())
                .thenAnswer { null }

            val states = getAccountDetails(LINSHARE_CREDENTIAL)
                .toList(ArrayList())

            assertThat(states).hasSize(4)

            assertThat(states[0](INIT_STATE))
                .isEqualTo(LOADING_STATE)

            assertThat(states[1](LOADING_STATE))
                .isEqualTo(AUTHENTICATE_SUCCESS_STATE)

            assertThat(states[2](AUTHENTICATE_SUCCESS_STATE))
                .isEqualTo(Either.right(
                    ACCOUNT_DETAILS_WITH_CREDENTIAL.copy(lastLogin = LAST_LOGIN)
                ))

            assertThat(states[3](Either.right(
                ACCOUNT_DETAILS_WITH_CREDENTIAL.copy(lastLogin = LAST_LOGIN))))
                .isEqualTo(ERROR_STATE)

            verify(authorizationManager).updateToken(TOKEN)
        }
    }

    @Test
    fun getAccountDetailsShouldErrorInLoadingLastLoginWhenGetQuotaFailed() {
        runBlockingTest {
            `when`(getToken(LINSHARE_CREDENTIAL))
                .then {
                    flow<State<Either<Failure, Success>>> {
                        emitState { LOADING_STATE }
                        emitState { AUTHENTICATE_SUCCESS_STATE }
                    }
                }

            `when`(auditUserRepository.getLastLogin())
                .thenAnswer { LAST_LOGIN }

            `when`(userRepository.getAuthorizedUser())
                .thenAnswer { LINSHARE_USER }

            `when`(quotaRepository.findQuota(QUOTA_UUID))
                .thenAnswer { null }

            val states = getAccountDetails(LINSHARE_CREDENTIAL)
                .toList(ArrayList())

            assertThat(states).hasSize(4)

            assertThat(states[0](INIT_STATE))
                .isEqualTo(LOADING_STATE)

            assertThat(states[1](LOADING_STATE))
                .isEqualTo(AUTHENTICATE_SUCCESS_STATE)

            assertThat(states[2](AUTHENTICATE_SUCCESS_STATE))
                .isEqualTo(Either.right(
                    ACCOUNT_DETAILS_WITH_CREDENTIAL.copy(lastLogin = LAST_LOGIN)
                ))

            assertThat(states[3](Either.right(
                ACCOUNT_DETAILS_WITH_CREDENTIAL.copy(lastLogin = LAST_LOGIN))))
                .isEqualTo(Either.right(
                    ACCOUNT_DETAILS_WITH_CREDENTIAL.copy(
                        lastLogin = LAST_LOGIN,
                        user = LINSHARE_USER
                    )
                ))

            verify(authorizationManager).updateToken(TOKEN)
        }
    }
}
