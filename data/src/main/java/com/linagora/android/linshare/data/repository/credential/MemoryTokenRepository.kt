package com.linagora.android.linshare.data.repository.credential

import com.linagora.android.linshare.domain.model.Credential
import com.linagora.android.linshare.domain.model.Token
import com.linagora.android.linshare.domain.repository.TokenRepository
import java.util.concurrent.atomic.AtomicReference

class MemoryTokenRepository : TokenRepository {

    private val tokenStore = AtomicReference<Pair<Credential, Token>>()

    override suspend fun persistsToken(credential: Credential, token: Token) {
        tokenStore.set(Pair(credential, token))
    }

    override suspend fun getToken(credential: Credential): Token? {
        return credential.takeIf { tokenStore.get()?.first == it }
            ?.let { tokenStore.get().second }
    }

    override suspend fun removeToken(credential: Credential) {
        credential.takeIf { tokenStore.get()?.first == it }
            ?.run { tokenStore.set(null) }
    }
}
