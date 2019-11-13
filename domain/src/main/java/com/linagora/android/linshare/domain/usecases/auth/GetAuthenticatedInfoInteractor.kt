package com.linagora.android.linshare.domain.usecases.auth

import arrow.core.Either
import com.linagora.android.linshare.domain.repository.CredentialRepository
import com.linagora.android.linshare.domain.usecases.account.GetTokenInteractor
import com.linagora.android.linshare.domain.usecases.auth.AuthenticationException.Companion.WRONG_CREDENTIAL
import com.linagora.android.linshare.domain.usecases.utils.Failure
import com.linagora.android.linshare.domain.usecases.utils.State
import com.linagora.android.linshare.domain.usecases.utils.Success
import com.linagora.android.linshare.domain.utils.emitState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetAuthenticatedInfoInteractor @Inject constructor(
    private val credentialRepository: CredentialRepository,
    private val getToken: GetTokenInteractor
) {
    operator fun invoke(): Flow<State<Either<Failure, Success>>> {
        return runBlocking {
            credentialRepository.getCurrentCredential()
                ?.let { getToken(it) }
                ?: flow<State<Either<Failure, Success>>> {
                    emitState { Either.left(AuthenticationFailure(BadCredentials(WRONG_CREDENTIAL))) }
                }
        }
    }
}
