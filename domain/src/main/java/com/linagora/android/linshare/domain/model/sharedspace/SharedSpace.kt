package com.linagora.android.linshare.domain.model.sharedspace

import com.google.gson.annotations.SerializedName
import com.linagora.android.linshare.domain.model.quota.QuotaId
import java.util.Date

data class SharedSpace(
    @SerializedName("uuid")
    val sharedSpaceId: SharedSpaceId,
    val name: String,
    @SerializedName("parentUuid")
    val parentSharedSpaceId: SharedSpaceId? = null,
    val nodeType: LinShareNodeType,
    val creationDate: Date,
    val modificationDate: Date,
    val role: SharedSpaceRole,
    @SerializedName("quotaUuid")
    val quotaId: QuotaId
)
