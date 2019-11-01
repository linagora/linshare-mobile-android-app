package com.linagora.android.linshare.domain.network

import com.google.common.truth.Truth.assertThat
import com.linagora.android.linshare.domain.network.Endpoint.AUTHENTICATION_PATH
import org.junit.jupiter.api.Test
import java.net.URL

class URLExtensionTest {

    private val BASE_PATH = "http://localhost.com"
    private val BASE_URL = URL(BASE_PATH)

    private val BASE_PATH_2 = "http://localhost.com/"
    private val BASE_URL_2 = URL(BASE_PATH_2)

    @Test
    fun withServicePathShouldReturnASuccessEndpoint() {
        val endpoint = BASE_URL.withServicePath(Endpoint.AUTHENTICAION)

        assertThat(endpoint).isEqualTo(URL(BASE_PATH.plus("/$AUTHENTICATION_PATH")))
    }

    @Test
    fun withServicePathShouldReturnASuccessEndpointWithSplashEnd() {
        val endpoint = BASE_URL_2.withServicePath(Endpoint.AUTHENTICAION)

        assertThat(endpoint).isEqualTo(URL(BASE_PATH_2.plus("$AUTHENTICATION_PATH")))
    }
}
