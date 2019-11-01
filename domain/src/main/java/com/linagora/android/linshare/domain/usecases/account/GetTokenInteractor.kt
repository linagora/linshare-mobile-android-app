package com.linagora.android.linshare.domain.usecases.account

import arrow.core.Either
import com.linagora.android.linshare.domain.model.Credential
import com.linagora.android.linshare.domain.repository.TokenRepository
import com.linagora.android.linshare.domain.usecases.auth.AuthenticationFailure
import com.linagora.android.linshare.domain.usecases.auth.AuthenticationViewState
import com.linagora.android.linshare.domain.usecases.auth.EmptyToken
import com.linagora.android.linshare.domain.usecases.utils.Failure
import com.linagora.android.linshare.domain.usecases.utils.State
import com.linagora.android.linshare.domain.usecases.utils.Success
import com.linagora.android.linshare.domain.usecases.utils.Success.Loading
import com.linagora.android.linshare.domain.utils.emitState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetTokenInteractor @Inject constructor(
    private val tokenRepository: TokenRepository
) {

    operator fun invoke(credential: Credential): Flow<State<Either<Failure, Success>>> {
        return flow<State<Either<Failure, Success>>> {
            emitState { Either.Right(Loading) }
            tokenRepository.getToken(credential)
                ?.let { emitState {
                    Either.Right(AuthenticationViewState(credential = credential, token = it)) }
                }
                ?: emitState { Either.Left(AuthenticationFailure(EmptyToken)) }
        }
    }
}
