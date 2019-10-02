package com.linagora.android.linshare.data.repository.credential

import android.content.SharedPreferences
import com.linagora.android.linshare.domain.model.Credential
import com.linagora.android.linshare.domain.repository.CredentialRepository
import javax.inject.Inject

class PreferenceCredentialRepository @Inject constructor(
    private val sharedPreferences: SharedPreferences
) : CredentialRepository {

    object Key {

        const val SERVER_NAME = "serverName"

        const val USER_NAME = "userName"
    }

    override suspend fun persistsCredential(credential: Credential) {
        with(sharedPreferences.edit()) {
            putString(Key.SERVER_NAME, credential.serverUrl.toString())
            putString(Key.USER_NAME, credential.userName.username)
            commit()
        }
    }

    override suspend fun getCredential(): Credential? {
        return with(sharedPreferences) {
            val serverName = getString(Key.SERVER_NAME, null)
            val userName = getString(Key.USER_NAME, null)

            serverName?.let {
                userName?.let {
                    Credential.fromString(serverName, userName)
                }
            }
        }
    }

    override suspend fun clearCredential() {
        with(sharedPreferences.edit()) {
            remove(Key.SERVER_NAME)
            remove(Key.USER_NAME)
        }
    }
}
