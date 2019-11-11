package com.linagora.android.linshare.domain.usecases.auth

import arrow.core.Either
import com.linagora.android.linshare.domain.model.Credential
import com.linagora.android.linshare.domain.repository.CredentialRepository
import com.linagora.android.linshare.domain.repository.TokenRepository
import com.linagora.android.linshare.domain.repository.authentication.AuthenticationRepository
import com.linagora.android.linshare.domain.usecases.utils.Failure
import com.linagora.android.linshare.domain.usecases.utils.State
import com.linagora.android.linshare.domain.usecases.utils.Success
import com.linagora.android.linshare.domain.usecases.utils.Success.Loading
import com.linagora.android.linshare.domain.utils.emitState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class RemoveAccountInteractor @Inject constructor(
    private val authenticationRepository: AuthenticationRepository,
    private val credentialRepository: CredentialRepository,
    private val tokenRepository: TokenRepository
) {

    operator fun invoke(credential: Credential): Flow<State<Either<Failure, Success>>> {
        return flow<State<Either<Failure, Success>>> {
            emitState { Either.right(Loading) }
            tokenRepository.getToken(credential)
                ?.apply {
                    authenticationRepository.deletePermanentToken(this)
                }
            credentialRepository.removeCredential(credential)
            emitState { Either.right(SuccessRemoveAccount) }
        }
    }
}
