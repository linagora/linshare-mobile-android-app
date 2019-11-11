package com.linagora.android.linshare.data.repository.credential

import android.content.SharedPreferences
import com.linagora.android.linshare.domain.model.Credential
import com.linagora.android.linshare.domain.model.Token
import com.linagora.android.linshare.domain.repository.TokenRepository
import java.util.UUID
import javax.inject.Inject

class PreferenceTokenRepository @Inject constructor(
    private val sharedPreferences: SharedPreferences
) : TokenRepository {

    companion object {
        const val TOKEN_SUFFIX_KEY = "_token_key"
        const val TOKEN_UUID_SUFFIX_KEY = "_token_uuid_key"
    }

    override suspend fun persistsToken(credential: Credential, token: Token) {
        sharedPreferences.edit()
            .putString(credential.toString().plus(TOKEN_SUFFIX_KEY), token.token)
            .putString(credential.toString().plus(TOKEN_UUID_SUFFIX_KEY), token.uuid.toString())
            .commit()
    }

    override suspend fun getToken(credential: Credential): Token? {
        return with(sharedPreferences) {
            val uuid = getString(credential.toString().plus(TOKEN_UUID_SUFFIX_KEY), null)
            val token = getString(credential.toString().plus(TOKEN_SUFFIX_KEY), null)

            runCatching {
                Token(
                    uuid = UUID.fromString(uuid!!),
                    token = token!!
                )
            }.getOrNull()
        }
    }

    override suspend fun removeToken(credential: Credential) {
        with(sharedPreferences.edit()) {
            remove(credential.toString().plus(TOKEN_UUID_SUFFIX_KEY))
            remove(credential.toString().plus(TOKEN_SUFFIX_KEY))
            commit()
        }
    }
}
