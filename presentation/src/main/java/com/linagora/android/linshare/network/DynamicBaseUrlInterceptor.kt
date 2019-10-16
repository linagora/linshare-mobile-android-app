package com.linagora.android.linshare.network

import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.Interceptor
import okhttp3.Interceptor.Chain
import okhttp3.Request
import okhttp3.Response
import java.net.URL
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DynamicBaseUrlInterceptor @Inject constructor() : Interceptor {

    private lateinit var newScheme: String
    private lateinit var newHost: String
    private lateinit var newPort: Port

    fun changeBaseUrl(baseUrl: URL) {
        baseUrl.toHttpUrlOrNull()
            ?.apply {
                newScheme = scheme
                newHost = host
                newPort = Port(port)
            }
    }

    override fun intercept(chain: Chain): Response {
        val request = buildNewRequest(chain.request())
        return chain.proceed(request)
    }

    private fun buildNewRequest(original: Request): Request {
        return runCatching {
            val newUrl = original.url.newBuilder()
                .scheme(newScheme)
                .host(newHost)
                .port(newPort.portNumber)
                .build()
            return original.newBuilder()
                .url(newUrl)
                .build()
        }.getOrDefault(original)
    }
}
