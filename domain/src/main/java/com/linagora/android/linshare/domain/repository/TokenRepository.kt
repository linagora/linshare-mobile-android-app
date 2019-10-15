package com.linagora.android.linshare.domain.repository

import com.linagora.android.linshare.domain.model.Credential
import com.linagora.android.linshare.domain.model.Token

interface TokenRepository {

    suspend fun persistsToken(credential: Credential, token: Token)

    suspend fun getToken(credential: Credential): Token?

    suspend fun removeToken(credential: Credential)
}
