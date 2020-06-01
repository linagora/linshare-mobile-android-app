package com.linagora.android.linshare.domain.model.contact

data class SimpleContact(
    override val displayName: String,
    override val email: String
) : Contact
