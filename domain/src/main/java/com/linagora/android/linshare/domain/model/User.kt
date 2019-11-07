package com.linagora.android.linshare.domain.model

import java.util.Date
import java.util.UUID

data class User(
    val uuid: UUID,
    val firstName: String,
    val lastName: String,
    val mail: String,
    val creationDate: Date,
    val modificationDate: Date,
    val quotaUuid: UUID,
    val accountType: String,
    val role: String,
    val canUpload: Boolean
)
