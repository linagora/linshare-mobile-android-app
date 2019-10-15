package com.linagora.android.linshare.data.repository.authentication

import com.linagora.android.linshare.domain.model.Credential
import com.linagora.android.linshare.domain.model.Password
import com.linagora.android.linshare.domain.model.Token
import com.linagora.android.linshare.domain.model.Username
import com.linagora.android.linshare.domain.repository.authentication.AuthenticationRepository
import com.linagora.android.linshare.domain.usecases.auth.AuthenticationException
import com.linagora.android.linshare.domain.usecases.auth.AuthenticationException.Companion.WRONG_CREDENTIAL
import com.linagora.android.linshare.domain.usecases.auth.AuthenticationException.Companion.WRONG_PASSWORD
import com.linagora.android.linshare.domain.usecases.auth.BadCredentials
import java.net.URL
import java.util.concurrent.atomic.AtomicReference

class MemoryAuthenticationRepository(
    predefinedCredential: Credential,
    predefinedPassword: Password,
    token: Token
) : AuthenticationRepository {

    private val authenticationStore: AtomicReference<Triple<Credential, Password, Token>> =
        AtomicReference(Triple(predefinedCredential, predefinedPassword, token))

    override suspend fun retrievePermanentToken(baseUrl: URL, username: Username, password: Password): Token {
        return validateCredential(Credential(baseUrl, username)) { authenticationStore.get().first == it }
            .run { validatePassword(password) { authenticationStore.get().second == it } }
            .let { authenticationStore.get().third }
    }

    @Throws(AuthenticationException::class)
    private fun validateCredential(credential: Credential, predicate: (Credential) -> Boolean): Credential {
        return credential.takeIf(predicate) ?: throw BadCredentials(WRONG_CREDENTIAL)
    }

    @Throws(AuthenticationException::class)
    private fun validatePassword(password: Password, predicate: (Password) -> Boolean): Password {
        return password.takeIf(predicate) ?: throw BadCredentials(WRONG_PASSWORD)
    }
}
