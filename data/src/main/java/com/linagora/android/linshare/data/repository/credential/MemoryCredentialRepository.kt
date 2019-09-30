package com.linagora.android.linshare.data.repository.credential

import com.linagora.android.linshare.domain.model.Credential
import com.linagora.android.linshare.domain.repository.CredentialRepository
import java.util.concurrent.atomic.AtomicReference

class MemoryCredentialRepository : CredentialRepository {

    private val storedCredential = AtomicReference<Credential>()

    override suspend fun persistsCredential(credential: Credential) {
        storedCredential.set(credential)
    }

    override suspend fun getCredential(): Credential? {
        return storedCredential.get()
    }

    override suspend fun clearCredential() {
        storedCredential.set(null)
    }
}
