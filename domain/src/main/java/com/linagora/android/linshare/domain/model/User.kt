package com.linagora.android.linshare.domain.model

import com.linagora.android.linshare.domain.model.quota.QuotaId
import java.util.Date
import java.util.UUID

data class User(
    val uuid: UUID,
    val firstName: String,
    val lastName: String,
    val mail: String,
    val creationDate: Date,
    val modificationDate: Date,
    val quotaUuid: QuotaId,
    val accountType: String,
    val role: String,
    val canUpload: Boolean
)
