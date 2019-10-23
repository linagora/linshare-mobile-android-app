package com.linagora.android.linshare.domain.usecases.auth

import arrow.core.Either
import com.linagora.android.linshare.domain.model.Password
import com.linagora.android.linshare.domain.model.Username
import com.linagora.android.linshare.domain.repository.authentication.AuthenticationRepository
import com.linagora.android.linshare.domain.usecases.utils.Failure
import com.linagora.android.linshare.domain.usecases.utils.State
import com.linagora.android.linshare.domain.usecases.utils.Success
import com.linagora.android.linshare.domain.usecases.utils.Success.Loading
import com.linagora.android.linshare.domain.utils.emitState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withTimeout
import java.net.URL
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthenticateInteractor @Inject constructor(
    private val authenticationRepository: AuthenticationRepository
) {

    companion object {
        const val AUTHENTICATION_REQUEST_TIMEOUT_MS = 1000 * 30L
    }

    operator fun invoke(baseUrl: URL, username: Username, password: Password): Flow<State<Either<Failure, Success>>> {
        return flow<State<Either<Failure, Success>>> {
            emitState { Either.Right(Loading) }
            try {
                withTimeout(AUTHENTICATION_REQUEST_TIMEOUT_MS) {
                    authenticationRepository.retrievePermanentToken(baseUrl, username, password)
                }.run {
                    emitState { Either.Right(AuthenticationViewState(this@run)) }
                }
            } catch (authException: AuthenticationException) {
                emitState { Either.Left(AuthenticationFailure(authException)) }
            }
        }
    }
}
