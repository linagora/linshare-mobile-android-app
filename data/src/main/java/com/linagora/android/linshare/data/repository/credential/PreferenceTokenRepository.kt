package com.linagora.android.linshare.data.repository.credential

import android.content.SharedPreferences
import com.linagora.android.linshare.domain.model.Credential
import com.linagora.android.linshare.domain.model.Token
import com.linagora.android.linshare.domain.repository.TokenRepository
import javax.inject.Inject

class PreferenceTokenRepository @Inject constructor(
    private val sharedPreferences: SharedPreferences
) : TokenRepository {

    override suspend fun persistsToken(credential: Credential, token: Token) {
        sharedPreferences.edit()
            .putString(credential.toString(), token.token)
            .commit()
    }

    override suspend fun getToken(credential: Credential): Token? {
        return sharedPreferences.getString(credential.toString(), null)
            ?.let { Token(it) }
    }

    override suspend fun removeToken(credential: Credential) {
        sharedPreferences.edit().remove(credential.toString())
    }
}
