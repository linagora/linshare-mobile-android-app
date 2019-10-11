package com.linagora.android.linshare.data.datasource

import com.linagora.android.linshare.data.api.LinshareApi
import com.linagora.android.linshare.domain.model.Password
import com.linagora.android.linshare.domain.model.Token
import com.linagora.android.linshare.domain.model.Username
import com.linagora.android.linshare.domain.usecases.auth.BadCredentials
import com.linagora.android.linshare.domain.usecases.auth.EmptyToken
import okhttp3.Credentials
import java.net.URL
import javax.inject.Inject

class LinshareDataSource @Inject constructor(
    private val linshareApi: LinshareApi
) {

    suspend fun retrievePermanentToken(baseUrl: URL, username: Username, password: Password): Token {
        val response = linshareApi.getPermanentToken(
            authenticateUrl = baseUrl.toString(),
            basicToken = Credentials.basic(username.username, password.value))
        return when (response.isSuccessful) {
            true -> response.body() ?: throw EmptyToken
            else -> throw BadCredentials(response.message())
        }
    }
}
