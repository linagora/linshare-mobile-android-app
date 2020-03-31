package com.linagora.android.linshare.domain.model.share

import com.linagora.android.linshare.domain.model.GenericUser
import com.linagora.android.linshare.domain.model.document.Document
import java.util.Date
import java.util.UUID

data class Share(
    val uuid: UUID,
    val name: String,
    val creationDate: Date,
    val modificationDate: Date,
    val expirationDate: Date,
    val downloaded: Long,
    val document: Document,
    val recipient: GenericUser,
    val description: String
)
