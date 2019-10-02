package com.linagora.android.linshare.domain.repository

import com.linagora.android.linshare.domain.model.Credential

interface CredentialRepository {
    suspend fun persistsCredential(credential: Credential)

    suspend fun getCredential(): Credential?

    suspend fun clearCredential()
}
