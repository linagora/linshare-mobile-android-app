package com.linagora.android.linshare.data.datasource

import com.linagora.android.linshare.data.api.LinshareApi
import com.linagora.android.linshare.domain.model.Password
import com.linagora.android.linshare.domain.model.Token
import com.linagora.android.linshare.domain.model.Username
import com.linagora.android.linshare.domain.usecases.auth.AuthenticationException
import com.linagora.android.linshare.domain.usecases.auth.AuthenticationException.Companion.WRONG_CREDENTIAL
import com.linagora.android.linshare.domain.usecases.auth.BadCredentials
import com.linagora.android.linshare.domain.usecases.auth.ConnectError
import com.linagora.android.linshare.domain.usecases.auth.EmptyToken
import com.linagora.android.linshare.domain.usecases.auth.ServerNotFound
import com.linagora.android.linshare.domain.usecases.auth.UnknownError
import kotlinx.coroutines.TimeoutCancellationException
import okhttp3.Credentials
import retrofit2.Response
import java.net.SocketException
import java.net.SocketTimeoutException
import java.net.URL
import java.net.UnknownHostException
import javax.inject.Inject

class LinshareDataSource @Inject constructor(
    private val linshareApi: LinshareApi
) {

    suspend fun retrievePermanentToken(baseUrl: URL, username: Username, password: Password): Token {
        try {
            val response = linshareApi.getPermanentToken(
                authenticateUrl = baseUrl.toString(),
                basicToken = Credentials.basic(username.username, password.value)
            )
            return when (response.isSuccessful) {
                true -> response.body() ?: throw EmptyToken
                else -> throw produceError(response)
            }
        } catch (exp: Exception) {
            when (exp) {
                is EmptyToken, ServerNotFound -> throw exp
                is BadCredentials -> throw exp
                is SocketTimeoutException -> throw ConnectError
                is SocketException -> throw ConnectError
                is TimeoutCancellationException -> throw ConnectError
                is UnknownHostException -> throw ConnectError
                else -> throw UnknownError
            }
        }
    }

    private fun produceError(response: Response<Token>): AuthenticationException {
        return when (response.code()) {
            404 -> ServerNotFound
            401 -> BadCredentials(WRONG_CREDENTIAL)
            else -> UnknownError
        }
    }
}
