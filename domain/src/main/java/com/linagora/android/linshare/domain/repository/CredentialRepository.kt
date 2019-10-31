package com.linagora.android.linshare.domain.repository

import com.linagora.android.linshare.domain.model.Credential

interface CredentialRepository {
    suspend fun persistsCredential(credential: Credential)

    suspend fun removeCredential(credential: Credential)

    suspend fun setCurrentCredential(credential: Credential): Boolean

    suspend fun getCurrentCredential(): Credential?

    suspend fun getAllCredential(): Set<Credential>

    suspend fun clearCredential()
}
