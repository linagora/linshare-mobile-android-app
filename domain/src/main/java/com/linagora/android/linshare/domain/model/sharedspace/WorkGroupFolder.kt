package com.linagora.android.linshare.domain.model.sharedspace

import com.google.gson.annotations.SerializedName
import java.util.Date

data class WorkGroupFolder(
    @SerializedName("uuid")
    override val workGroupNodeId: WorkGroupNodeId,
    @SerializedName("parent")
    override val parentWorkGroupNodeId: WorkGroupNodeId,
    override val creationDate: Date,
    @SerializedName("workGroup")
    override val sharedSpaceId: SharedSpaceId,
    override val modificationDate: Date,
    override val description: String?,
    override val name: String,
    override val treePath: List<TreePath>
) : WorkGroupNode
