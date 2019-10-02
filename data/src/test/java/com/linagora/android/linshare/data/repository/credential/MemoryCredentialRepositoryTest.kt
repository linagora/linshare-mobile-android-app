package com.linagora.android.linshare.data.repository.credential

import com.linagora.android.linshare.domain.repository.CredentialRepository
import com.linagora.android.testshared.repository.credential.CredentialRepositoryContract

class MemoryCredentialRepositoryTest : CredentialRepositoryContract() {

    private val credentialRepo =
        MemoryCredentialRepository()

    override val credentialRepository: CredentialRepository
        get() = credentialRepo
}
