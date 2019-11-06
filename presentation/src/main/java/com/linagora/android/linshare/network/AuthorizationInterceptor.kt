package com.linagora.android.linshare.network

import com.linagora.android.linshare.domain.model.Token
import okhttp3.Interceptor
import okhttp3.Interceptor.Chain
import okhttp3.Request
import okhttp3.Response
import java.util.concurrent.atomic.AtomicReference
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthorizationInterceptor @Inject constructor() : Interceptor {

    private val currentTokenStored = AtomicReference<Token>()

    fun updateToken(token: Token) {
        currentTokenStored.set(token)
    }

    override fun intercept(chain: Chain): Response {
        return chain.proceed(buildNewRequest(chain.request()))
    }

    private fun buildNewRequest(original: Request): Request {
        return runCatching {
            original.newBuilder()
                .addHeader("Authorization", currentTokenStored.get().asBearerHeader())
                .build()
        }.getOrDefault(original)
    }
}
