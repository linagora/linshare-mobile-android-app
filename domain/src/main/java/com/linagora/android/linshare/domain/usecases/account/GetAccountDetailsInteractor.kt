package com.linagora.android.linshare.domain.usecases.account

import arrow.core.Either
import com.linagora.android.linshare.domain.model.Credential
import com.linagora.android.linshare.domain.model.LastLogin
import com.linagora.android.linshare.domain.network.manager.AuthorizationManager
import com.linagora.android.linshare.domain.repository.user.AuditUserRepository
import com.linagora.android.linshare.domain.usecases.auth.AuthenticationViewState
import com.linagora.android.linshare.domain.usecases.utils.Failure
import com.linagora.android.linshare.domain.usecases.utils.Failure.Error
import com.linagora.android.linshare.domain.usecases.utils.State
import com.linagora.android.linshare.domain.usecases.utils.Success
import com.linagora.android.linshare.domain.usecases.utils.Success.Idle
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import javax.inject.Inject

class GetAccountDetailsInteractor @Inject constructor(
    private val getToken: GetTokenInteractor,
    private val authorizationManager: AuthorizationManager,
    private val auditUserRepository: AuditUserRepository
) {

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
            auditUserRepository.getLastLogin()
                ?.let {
                    send(State<Either<Failure, Success>> {
                        when (this) {
                            is Either.Right -> appendAccountDetails(
                                success = this.b,
                                authenticationViewState = authenticationViewState,
                                lastLogin = it
                            )
                            else -> Either.Right(AccountDetailsViewState(lastLogin = it))
                        }
                    }
                    )
                } ?: send(State<Either<Failure, Success>> { Either.left(Error) })
        }
    }

    private fun appendAccountDetails(
        success: Success,
        authenticationViewState: AuthenticationViewState,
        lastLogin: LastLogin
    ): Either<Failure, Success> = Either.right(
        when (success) {
            is AccountDetailsViewState -> success.copy(isLoading = false, lastLogin = lastLogin)
            else -> AccountDetailsViewState(
                credential = authenticationViewState.credential,
                token = authenticationViewState.token,
                lastLogin = lastLogin
            )
        }
    )
}
