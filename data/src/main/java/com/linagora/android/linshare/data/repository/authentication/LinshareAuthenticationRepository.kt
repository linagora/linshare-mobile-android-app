package com.linagora.android.linshare.data.repository.authentication

import com.linagora.android.linshare.data.datasource.LinshareDataSource
import com.linagora.android.linshare.domain.model.Credential
import com.linagora.android.linshare.domain.model.Password
import com.linagora.android.linshare.domain.model.Token
import com.linagora.android.linshare.domain.model.Username
import com.linagora.android.linshare.domain.network.Endpoint
import com.linagora.android.linshare.domain.network.withServicePath
import com.linagora.android.linshare.domain.repository.CredentialRepository
import com.linagora.android.linshare.domain.repository.TokenRepository
import com.linagora.android.linshare.domain.repository.authentication.AuthenticationRepository
import java.net.URL
import javax.inject.Inject

class LinshareAuthenticationRepository @Inject constructor(
    private val linshareDataSource: LinshareDataSource,
    private val credentialRepository: CredentialRepository,
    private val tokenRepository: TokenRepository
) : AuthenticationRepository {

    override suspend fun retrievePermanentToken(baseUrl: URL, username: Username, password: Password): Token {
        return linshareDataSource.retrievePermanentToken(
                baseUrl = baseUrl.withServicePath(Endpoint.AUTHENTICAION),
                username = username,
                password = password)
            .also { storeAuthenticationInfo(baseUrl, username, it) }
    }

    private suspend fun storeAuthenticationInfo(baseUrl: URL, username: Username, token: Token) {
        Credential(baseUrl, username)
            .also { credentialRepository.persistsCredential(it) }
            .also { tokenRepository.persistsToken(it, token) }
    }
}
