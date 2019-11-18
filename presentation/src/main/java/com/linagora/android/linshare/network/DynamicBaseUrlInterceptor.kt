package com.linagora.android.linshare.network

import com.linagora.android.linshare.domain.network.Endpoint
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.Interceptor
import okhttp3.Interceptor.Chain
import okhttp3.Request
import okhttp3.Response
import java.net.URL
import java.util.concurrent.atomic.AtomicReference
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DynamicBaseUrlInterceptor @Inject constructor() : Interceptor {

    private val newScheme = AtomicReference<String>()
    private val newHost = AtomicReference<String>()
    private var newPort = AtomicReference<Port>()

    fun changeBaseUrl(baseUrl: URL) {
        baseUrl.toHttpUrlOrNull()
            ?.apply {
                newScheme.set(scheme)
                newHost.set(host)
                newPort.set(Port(port))
            }
    }

    fun reset() {
        newScheme.set(null)
        newHost.set(null)
        newPort.set(null)
    }

    override fun intercept(chain: Chain): Response {
        val request = buildNewRequest(chain.request())
        return chain.proceed(request)
    }

    private fun buildNewRequest(original: Request): Request {
        return runCatching {
            val newUrl = original.url.newBuilder()
                .scheme(newScheme.get()!!)
                .host(newHost.get()!!)
                .port(newPort.get()!!.portNumber)
                .encodedPath("/" + Endpoint.ROOT_PATH + original.url.encodedPath)
                .build()
            return original.newBuilder()
                .url(newUrl)
                .build()
        }.getOrDefault(original)
    }
}
