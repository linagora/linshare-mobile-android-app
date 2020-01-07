package com.linagora.android.linshare.domain.model

import java.net.URL

data class Credential(val serverUrl: URL, val userName: Username) {

    companion object {
        fun fromString(serverStr: String, userNameStr: String): Credential {
            return Credential(URL(serverStr), Username(userNameStr))
        }

        val InvalidCredential = Credential(URL("http://invalid"), Username("invalid"))
    }
}
