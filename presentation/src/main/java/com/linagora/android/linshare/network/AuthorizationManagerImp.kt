package com.linagora.android.linshare.network

import com.linagora.android.linshare.domain.model.Token
import com.linagora.android.linshare.domain.network.manager.AuthorizationManager
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthorizationManagerImp @Inject constructor(
    private val authorizationInterceptor: AuthorizationInterceptor
) : AuthorizationManager {

    override fun updateToken(token: Token) {
        authorizationInterceptor.updateToken(token)
    }
}
