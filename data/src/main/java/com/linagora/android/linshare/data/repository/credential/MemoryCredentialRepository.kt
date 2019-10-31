package com.linagora.android.linshare.data.repository.credential

import com.linagora.android.linshare.domain.model.Credential
import com.linagora.android.linshare.domain.repository.CredentialRepository
import java.util.Collections
import java.util.concurrent.atomic.AtomicReference

class MemoryCredentialRepository : CredentialRepository {

    private val currentCredential = AtomicReference<Credential>()
    private val storedCredential = Collections.synchronizedSet<Credential>(HashSet())

    override suspend fun persistsCredential(credential: Credential) {
        storedCredential.add(credential)
        setCurrentCredential(credential)
    }

    override suspend fun removeCredential(credential: Credential) {
        storedCredential.remove(credential)
    }

    override suspend fun setCurrentCredential(credential: Credential): Boolean {
        return when (storedCredential.contains(credential)) {
            true -> {
                currentCredential.set(credential)
                true
            }
            else -> false
        }
    }

    override suspend fun getCurrentCredential(): Credential? {
        return currentCredential.get()
    }

    override suspend fun getAllCredential(): Set<Credential> {
        return storedCredential
    }

    override suspend fun clearCredential() {
        currentCredential.set(null)
        storedCredential.clear()
    }
}
