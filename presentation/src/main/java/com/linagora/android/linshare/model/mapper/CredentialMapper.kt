package com.linagora.android.linshare.model.mapper

import com.linagora.android.linshare.domain.model.Credential
import com.linagora.android.linshare.model.CredentialParcelable

fun CredentialParcelable.toCredential(): Credential {
    return Credential(baseUrl, username)
}

fun Credential.toParcelable(): CredentialParcelable {
    return CredentialParcelable(serverUrl, userName)
}
