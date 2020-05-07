package com.linagora.android.linshare.domain.model.sharedspace

import com.google.gson.annotations.SerializedName
import java.util.Date

data class SharedSpaceNodeNested(
    @SerializedName("uuid")
    val sharedSpaceId: SharedSpaceId,
    val role: SharedSpaceRole,
    val creationDate: Date,
    val modificationDate: Date,
    val name: String,
    val nodeType: LinShareNodeType
)
