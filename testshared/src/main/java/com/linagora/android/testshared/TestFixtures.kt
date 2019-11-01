package com.linagora.android.testshared

import arrow.core.Either
import com.linagora.android.linshare.domain.model.Credential
import com.linagora.android.linshare.domain.model.Password
import com.linagora.android.linshare.domain.model.Token
import com.linagora.android.linshare.domain.model.Username
import com.linagora.android.linshare.domain.usecases.auth.AuthenticationException
import com.linagora.android.linshare.domain.usecases.auth.AuthenticationFailure
import com.linagora.android.linshare.domain.usecases.auth.AuthenticationViewState
import com.linagora.android.linshare.domain.usecases.auth.BadCredentials
import com.linagora.android.linshare.domain.usecases.auth.ConnectError
import com.linagora.android.linshare.domain.usecases.auth.ServerNotFound
import com.linagora.android.linshare.domain.usecases.auth.UnknownError
import com.linagora.android.linshare.domain.usecases.utils.Success.Idle
import com.linagora.android.linshare.domain.usecases.utils.Success.Loading
import com.linagora.android.testshared.TestFixtures.Credentials.LINSHARE_CREDENTIAL
import java.net.URL

object TestFixtures {

    object Tokens {

        const val TOKEN_VALUE = "eyJhbGciOiJSUzUxMiJ9.eyJzdWIiOiJ1c2VyMUBsaW5zaGFyZS5vcmciLCJkb21haW" +
                "4iOiI0YTBjMmNmNC01ZjA5LTRmYjAtOWNhZS0yYzdlYTdiNjRmYWEiLCJpc3MiOiJMaW5TaGFyZSIsImV4cCI6" +
                "MTU3MDY4OTk1OSwiaWF0IjoxNTcwNjg5NjU5fQ.YQgIhRShjcq1_l3MLeQX7rTqPZGi67hqi3SF_WK2yuuVbIe" +
                "wD4r4X6A-HU0myeHh4YHhfeQ_1TsOBAP88Q86QzD0fYqL5Oe3MY5Ula7j29KR7R2_WFooHAq4y9UTOId8GY-ue" +
                "GB3m9DYZt-1-ViC05rGR92wBF86FGpfuunDboh-SApghG-S7LFJ-_J99leXMblvRkIPblxI8z-nbjy6ANHlN5_" +
                "PPOSudS3eOwKuDDzv0uiyztKuSakWkVz0IaByxlKAR_0-KEnGHf4tUcazy7v3NjxpliKrNyPurrTWMdvUlIA4Z" +
                "K_64WjHoDad3ho7lZsPDSq44UrTaUUYncVHvQ"
        const val TOKEN_VALUE_2 = "ZHBoYW1ob2GzX0BsaW5hZ29yYS5jb206aThqckJ3KTgzNk4="

        val TOKEN = Token(TOKEN_VALUE)
        val TOKEN_2 = Token(TOKEN_VALUE_2)
    }

    object Credentials {

        const val NAME = "alica@domain.com"
        const val NAME2 = "bob@domain.com"
        const val SERVER_NAME = "http://domain.com"
        const val LINSHARE_NAME = "user1@linagora.com"
        const val LINSHARE_URL = "http://linshare.org"

        val USER_NAME = Username(NAME)
        val USER_NAME2 = Username(NAME2)
        val LINSHARE_USER1 = Username(LINSHARE_NAME)
        val LINSHARE_BASE_URL = URL(LINSHARE_URL)
        val LINSHARE_CREDENTIAL = Credential(LINSHARE_BASE_URL, LINSHARE_USER1)
        val SERVER_URL = URL(SERVER_NAME)
        val CREDENTIAL = Credential(SERVER_URL, USER_NAME)
        val CREDENTIAL2 = Credential(SERVER_URL, USER_NAME2)
    }

    object Authentications {

        const val PASSWORD_VALUE = "qwertyui"
        const val PASSWORD_VALUE_2 = "asdasdasd"
        const val LINSHARE_PASSWORD = "password1"

        val LINSHARE_PASSWORD1 = Password(LINSHARE_PASSWORD)
        val PASSWORD = Password(PASSWORD_VALUE)
        val PASSWORD_2 = Password(PASSWORD_VALUE_2)
    }

    object State {

        val INIT_STATE = Either.Right(Idle)

        val LOADING_STATE = Either.Right(Loading)

        val AUTHENTICATE_SUCCESS_STATE = Either.Right(
            AuthenticationViewState(LINSHARE_CREDENTIAL, TestFixtures.Tokens.TOKEN))

        val WRONG_CREDENTIAL_STATE = Either.Left(
            AuthenticationFailure(
                BadCredentials(
                    AuthenticationException.WRONG_CREDENTIAL
                )
            )
        )

        val WRONG_PASSWORD_STATE = Either.Left(
            AuthenticationFailure(
                BadCredentials(
                    AuthenticationException.WRONG_PASSWORD
                )
            )
        )

        val CONNECT_ERROR_STATE = Either.Left(
            AuthenticationFailure(ConnectError)
        )

        val UNKNOW_ERROR_STATE = Either.Left(
            AuthenticationFailure(UnknownError)
        )

        val SERVER_NOT_FOUND_STATE = Either.Left(
            AuthenticationFailure(ServerNotFound)
        )
    }
}
