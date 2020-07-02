package com.linagora.android.linshare.domain.model.sharedspace

import com.google.gson.annotations.SerializedName
import okhttp3.MediaType
import java.util.Date

data class WorkGroupDocument(
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
    val size: Long,
    val mimeType: MediaType,
    val hasThumbnail: Boolean,
    val uploadDate: Date,
    val hasRevision: Boolean,
    val sha256sum: String,
    override val treePath: List<TreePath>
) : WorkGroupNode
