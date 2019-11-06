package com.linagora.android.linshare.data.model

import java.util.Date

data class AuthenticationAuditLogEntryUserResponse(
    val type: String,
    val action: String,
    val creationDate: Date,
    val message: String
)
