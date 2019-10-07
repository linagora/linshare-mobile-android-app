package com.linagora.android.linshare.data.repository.credential

import android.content.SharedPreferences
import com.linagora.android.linshare.domain.model.Token
import com.linagora.android.linshare.domain.repository.TokenRepository
import javax.inject.Inject

class PreferenceTokenRepository @Inject constructor(
    private val sharedPreferences: SharedPreferences
) : TokenRepository {

    object Key {
        const val TOKEN = "token"
    }

    override suspend fun persistsToken(token: Token) {
        sharedPreferences.edit()
            .putString(Key.TOKEN, token.tokenString)
            .commit()
    }

    override suspend fun getToken(): Token? {
        return sharedPreferences.getString(Key.TOKEN, null)
            ?.let { Token(it) }
    }

    override suspend fun clearToken() {
        sharedPreferences.edit().remove(Key.TOKEN)
    }
}
