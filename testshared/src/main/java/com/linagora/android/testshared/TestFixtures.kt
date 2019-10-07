package com.linagora.android.testshared

import com.linagora.android.linshare.domain.model.Credential
import com.linagora.android.linshare.domain.model.Token
import com.linagora.android.linshare.domain.model.Username
import java.net.URL

object TestFixtures {

    object Tokens {

        const val TOKEN_VALUE = "ZHBoYW1ob2FuZ0BsaW5hZ29yYS5jb206aThqckJ3KTgzNk4="
        const val TOKEN_VALUE_2 = "ZHBoYW1ob2GzX0BsaW5hZ29yYS5jb206aThqckJ3KTgzNk4="

        val TOKEN = Token(TOKEN_VALUE)
        val TOKEN_2 = Token(TOKEN_VALUE_2)
    }

    object Credentials {

        const val NAME = "alica@domain.com"
        const val NAME2 = "bob@domain.com"
        const val SERVER_NAME = "http://domain.com"

        val USER_NAME = Username(NAME)
        val USER_NAME2 = Username(NAME2)
        private val SERVER_URL = URL(SERVER_NAME)
        val CREDENTIAL = Credential(SERVER_URL, USER_NAME)
        val CREDENTIAL2 = Credential(SERVER_URL, USER_NAME2)
    }
}
