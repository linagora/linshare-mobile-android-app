package com.linagora.android.linshare.domain.repository

import com.linagora.android.linshare.domain.model.Token

interface TokenRepository {

    suspend fun persistsToken(token: Token)

    suspend fun getToken(): Token?

    suspend fun clearToken()
}
