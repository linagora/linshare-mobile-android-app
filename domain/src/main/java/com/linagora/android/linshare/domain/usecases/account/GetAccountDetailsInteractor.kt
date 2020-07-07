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
import com.linagora.android.linshare.domain.model.AccountQuota
import com.linagora.android.linshare.domain.model.Credential
import com.linagora.android.linshare.domain.model.LastLogin
import com.linagora.android.linshare.domain.model.User
import com.linagora.android.linshare.domain.model.quota.QuotaId
import com.linagora.android.linshare.domain.network.manager.AuthorizationManager
import com.linagora.android.linshare.domain.repository.user.AuditUserRepository
import com.linagora.android.linshare.domain.repository.user.QuotaRepository
import com.linagora.android.linshare.domain.repository.user.UserRepository
import com.linagora.android.linshare.domain.usecases.auth.AuthenticationViewState
import com.linagora.android.linshare.domain.usecases.utils.Failure
import com.linagora.android.linshare.domain.usecases.utils.Failure.Error
import com.linagora.android.linshare.domain.usecases.utils.State
import com.linagora.android.linshare.domain.usecases.utils.Success
import com.linagora.android.linshare.domain.usecases.utils.Success.Idle
import kotlinx.coroutines.channels.ProducerScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import javax.inject.Inject

class GetAccountDetailsInteractor @Inject constructor(
    private val getToken: GetTokenInteractor,
    private val authorizationManager: AuthorizationManager,
    private val auditUserRepository: AuditUserRepository,
    private val userRepository: UserRepository,
    private val quotaRepository: QuotaRepository
) {

    companion object {
        private val LOGGER: Logger = LoggerFactory.getLogger(GetAccountDetailsInteractor::class.java)
    }

    operator fun invoke(credential: Credential): Flow<State<Either<Failure, Success>>> {
        return getToken(credential)
            .flatMapConcat {
                when (val state = it(Either.right(Idle))) {
                    is Either.Right -> doWithSuccessState(it, state.b)
                    else -> flowOf(it)
                }
            }
    }

    private suspend fun doWithSuccessState(
        state: State<Either<Failure, Success>>,
        success: Success
    ): Flow<State<Either<Failure, Success>>> = runCatching {
        success.takeIf { it is AuthenticationViewState }
            .let { it as AuthenticationViewState }
            .let {
                authorizationManager.updateToken(it.token)
                getMoreDetails(state, it)
            }
    }.getOrDefault(flowOf(state))

    private suspend fun getMoreDetails(
        state: State<Either<Failure, Success>>,
        authenticationViewState: AuthenticationViewState
    ): Flow<State<Either<Failure, Success>>> = channelFlow {
        send(state)
        launch {
            LOGGER.info("getMoreDetails() getLastLogin")
            getLastLogin(this@channelFlow, authenticationViewState)
        }
        launch {
            LOGGER.info("getMoreDetails() getAvailableSpace")
            getAvailableSpace(this@channelFlow, authenticationViewState)
        }
    }

    private suspend fun getLastLogin(
        producerScope: ProducerScope<State<Either<Failure, Success>>>,
        authenticationViewState: AuthenticationViewState
    ) {
        auditUserRepository.getLastLogin()
            ?.let { sendLastLogin(producerScope, authenticationViewState, it) }
            ?: producerScope.send(State { Either.left(Error) })
    }

    private suspend fun getAvailableSpace(
        producerScope: ProducerScope<State<Either<Failure, Success>>>,
        authenticationViewState: AuthenticationViewState
    ) {
        userRepository.getAuthorizedUser()
            ?.let { user ->
                sendAuthorizedUser(producerScope, authenticationViewState, user)
                findQuota(producerScope, authenticationViewState, user.quotaUuid)
            } ?: producerScope.send(State { Either.left(Error) })
    }

    private suspend fun sendLastLogin(
        producerScope: ProducerScope<State<Either<Failure, Success>>>,
        lastAuthenticationViewState: AuthenticationViewState,
        lastLogin: LastLogin
    ) {
        producerScope.send(State {
            this.fold(
                ifLeft = { Either.Right(defaultLastLogin(lastAuthenticationViewState, lastLogin)) },
                ifRight = { success -> appendLastLogin(success, lastAuthenticationViewState, lastLogin) }
            )
        })
    }

    private suspend fun sendAuthorizedUser(
        producerScope: ProducerScope<State<Either<Failure, Success>>>,
        lastAuthenticationViewState: AuthenticationViewState,
        user: User
    ) {
        producerScope.send(State {
            this.fold(
                ifLeft = { Either.Right(defaultUser(lastAuthenticationViewState, user)) },
                ifRight = { success -> appendUser(success, lastAuthenticationViewState, user) }
            )
        })
    }

    private suspend fun findQuota(
        producerScope: ProducerScope<State<Either<Failure, Success>>>,
        authenticationViewState: AuthenticationViewState,
        quotaId: QuotaId
    ) {
        quotaRepository.findQuota(quotaId)
            ?.let { sendAccountQuota(producerScope, authenticationViewState, it) }
    }

    private fun appendLastLogin(
        success: Success,
        authenticationViewState: AuthenticationViewState,
        lastLogin: LastLogin
    ): Either<Failure, Success> = Either.right(
        when (success) {
            is AccountDetailsViewState -> success.copy(isLoading = false, lastLogin = lastLogin)
            else -> defaultLastLogin(authenticationViewState, lastLogin)
        }
    )

    private fun defaultLastLogin(
        authenticationViewState: AuthenticationViewState,
        lastLogin: LastLogin
    ) = AccountDetailsViewState(
        credential = authenticationViewState.credential,
        token = authenticationViewState.token,
        lastLogin = lastLogin
    )

    private fun appendUser(
        success: Success,
        authenticationViewState: AuthenticationViewState,
        user: User
    ): Either<Failure, Success> = Either.right(
        when (success) {
            is AccountDetailsViewState -> success.copy(isLoading = false, user = user)
            else -> defaultUser(authenticationViewState, user)
        }
    )

    private fun defaultUser(
        authenticationViewState: AuthenticationViewState,
        user: User
    ) = AccountDetailsViewState(
        credential = authenticationViewState.credential,
        token = authenticationViewState.token,
        user = user
    )

    private suspend fun sendAccountQuota(
        producerScope: ProducerScope<State<Either<Failure, Success>>>,
        lastAuthenticationViewState: AuthenticationViewState,
        accountQuota: AccountQuota
    ) {
        producerScope.send(State {
            this.fold(
                ifLeft = { Either.Right(defaultQuota(lastAuthenticationViewState, accountQuota)) },
                ifRight = { success -> appendQuota(success, lastAuthenticationViewState, accountQuota) }
            )
        })
    }

    private fun appendQuota(
        success: Success,
        authenticationViewState: AuthenticationViewState,
        quota: AccountQuota
    ): Either<Failure, Success> = Either.right(
        when (success) {
            is AccountDetailsViewState -> success.copy(isLoading = false, quota = quota)
            else -> defaultQuota(authenticationViewState, quota)
        }
    )

    private fun defaultQuota(
        authenticationViewState: AuthenticationViewState,
        quota: AccountQuota
    ) = AccountDetailsViewState(
        credential = authenticationViewState.credential,
        token = authenticationViewState.token,
        quota = quota
    )
}
