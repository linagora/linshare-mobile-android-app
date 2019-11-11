package com.linagora.android.linshare.domain.repository.authentication

import com.linagora.android.linshare.domain.model.Password
import com.linagora.android.linshare.domain.model.Token
import com.linagora.android.linshare.domain.model.Username
import com.linagora.android.linshare.domain.usecases.auth.AuthenticationException
import java.net.URL

interface AuthenticationRepository {

    @Throws(AuthenticationException::class)
    suspend fun retrievePermanentToken(baseUrl: URL, username: Username, password: Password): Token

    suspend fun deletePermanentToken(token: Token): Boolean
}
