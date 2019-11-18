package com.linagora.android.linshare.domain.network.manager

import com.linagora.android.linshare.domain.model.Token

interface AuthorizationManager {

    fun updateToken(token: Token)

    fun reset()
}
