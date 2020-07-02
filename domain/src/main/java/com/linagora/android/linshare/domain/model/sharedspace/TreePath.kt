package com.linagora.android.linshare.domain.model.sharedspace

import com.google.gson.annotations.SerializedName
data class TreePath(
    @SerializedName("uuid")
    val workGroupNodeId: WorkGroupNodeId,
    val name: String
)
