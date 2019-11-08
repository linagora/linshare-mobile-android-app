package com.linagora.android.linshare.domain.usecases.account

import arrow.core.Either
import com.linagora.android.linshare.domain.model.AccountQuota
import com.linagora.android.linshare.domain.model.Credential
import com.linagora.android.linshare.domain.model.LastLogin
import com.linagora.android.linshare.domain.model.User
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
                findQuota(producerScope, authenticationViewState, user.quotaUuid.toString())
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
        quotaId: String
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
