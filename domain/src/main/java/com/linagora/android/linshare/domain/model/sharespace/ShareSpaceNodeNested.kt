package com.linagora.android.linshare.domain.model.sharespace

import com.google.gson.annotations.SerializedName
import java.util.Date

data class ShareSpaceNodeNested(
    @SerializedName("uuid")
    val shareSpaceId: ShareSpaceId,
    val role: SharedSpaceRole,
    val creationDate: Date,
    val modificationDate: Date,
    val name: String,
    val nodeType: LinShareNodeType
)
