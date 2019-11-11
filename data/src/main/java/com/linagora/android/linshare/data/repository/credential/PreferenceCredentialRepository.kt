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

    override suspend fun removeCredential(credential: Credential) {
        if (containsCredential(credential)) {
            clearCredential()
        }
    }

    override suspend fun setCurrentCredential(credential: Credential): Boolean {
        return containsCredential(credential)
    }

    override suspend fun getCurrentCredential(): Credential? {
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

    override suspend fun getAllCredential(): Set<Credential> {
        return getCurrentCredential()
            ?.let { setOf(it) }
            ?: emptySet()
    }

    override suspend fun clearCredential() {
        with(sharedPreferences.edit()) {
            remove(Key.SERVER_NAME)
            remove(Key.USER_NAME)
            commit()
        }
    }

    private fun containsCredential(credential: Credential): Boolean {
        with(sharedPreferences) {
            val serverName = getString(Key.SERVER_NAME, null)
            val userName = getString(Key.USER_NAME, null)

            serverName?.let {
                userName?.let {
                    if (credential.serverUrl.toString() == serverName &&
                        credential.userName.username == userName) {
                        return true
                    }
                }
            }
        }
        return false
    }
}
