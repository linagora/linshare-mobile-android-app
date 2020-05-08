package com.linagora.android.linshare.data.datasource.network

import com.linagora.android.linshare.data.api.LinshareApi
import com.linagora.android.linshare.data.datasource.sharedspacesdocument.SharedSpacesDocumentDataSource
import com.linagora.android.linshare.domain.model.sharedspace.SharedSpaceId
import com.linagora.android.linshare.domain.model.sharedspace.WorkGroupNode
import com.linagora.android.linshare.domain.model.sharedspace.WorkGroupNodeId
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LinShareSharedSpacesDocumentDataSource @Inject constructor(
    private val linShareApi: LinshareApi
) : SharedSpacesDocumentDataSource {

    override suspend fun getAllChildNodes(
        sharedSpaceId: SharedSpaceId,
        parentNodeId: WorkGroupNodeId?
    ): List<WorkGroupNode> {
        val workGroupNodes = parentNodeId
            ?.let { parentId -> linShareApi.getAllSharedSpaceNode(sharedSpaceId.uuid.toString(), parentId.uuid.toString()) }
            ?: linShareApi.getAllSharedSpaceNode(sharedSpaceId.uuid.toString())
        return workGroupNodes.sortedBy { it.modificationDate }
    }

    override suspend fun getSharedSpaceNode(
        sharedSpaceId: SharedSpaceId,
        nodeId: WorkGroupNodeId
    ): WorkGroupNode {
        return linShareApi.getSharedSpaceNode(
            sharedSpaceId.uuid.toString(),
            nodeId.uuid.toString()
        )
    }
}
