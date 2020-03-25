package com.linagora.android.linshare.data.datasource

import android.content.Context
import android.provider.Settings
import com.linagora.android.linshare.data.api.LinshareApi
import com.linagora.android.linshare.data.model.authentication.PermanentTokenBodyRequest
import com.linagora.android.linshare.domain.model.AccountQuota
import com.linagora.android.linshare.domain.model.LastLogin
import com.linagora.android.linshare.domain.model.Password
import com.linagora.android.linshare.domain.model.Token
import com.linagora.android.linshare.domain.model.User
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
    private val context: Context,
    private val linshareApi: LinshareApi
) {

    suspend fun retrievePermanentToken(baseUrl: URL, username: Username, password: Password): Token {
        try {
            val androidId = Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
            val response = linshareApi.createPermanentToken(
                authenticateUrl = baseUrl.toString(),
                basicToken = Credentials.basic(username.username, password.value),
                permanentToken = PermanentTokenBodyRequest(label = "LinShare-Android-App-$androidId")
            )
            return when (response.isSuccessful) {
                true -> response.body() ?: throw EmptyToken
                else -> throw produceError(response)
            }
        } catch (exp: Exception) {
            exp.printStackTrace()
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

    suspend fun deletePermanentToken(token: Token): Boolean {
        return runCatching {
            linshareApi.deletePermanentToken(token.uuid.toString())
            return true
        }.getOrDefault(false)
    }

    suspend fun getLastLogin(): LastLogin? {
        return runCatching {
            LastLogin(
                linshareApi.auditAuthenticationEntryUser()
                    .last()
                    .creationDate
            )
        }.getOrNull()
    }

    suspend fun getAuthorizedUser(): User? {
        return runCatching {
            linshareApi.getAuthorizedUser()
        }.getOrNull()
    }

    suspend fun findQuota(quotaUuid: String): AccountQuota? {
        return kotlin.runCatching {
            linshareApi.getQuota(quotaUuid)
        }.getOrNull()
    }
}
