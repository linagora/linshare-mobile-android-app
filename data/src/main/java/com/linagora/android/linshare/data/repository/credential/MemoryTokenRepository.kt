package com.linagora.android.linshare.data.repository.credential

import com.linagora.android.linshare.domain.model.Token
import com.linagora.android.linshare.domain.repository.TokenRepository
import java.util.concurrent.atomic.AtomicReference

class MemoryTokenRepository : TokenRepository {

    private val tokenStore = AtomicReference<Token>()

    override suspend fun persistsToken(token: Token) {
        tokenStore.set(token)
    }

    override suspend fun getToken(): Token? = tokenStore.get()

    override suspend fun clearToken() {
        tokenStore.set(null)
    }
}
