package com.linagora.android.linshare.data.repository.sharedspace

import com.linagora.android.linshare.data.datasource.sharedspacesdocument.SharedSpacesDocumentDataSource
import com.linagora.android.linshare.domain.model.sharedspace.SharedSpaceId
import com.linagora.android.linshare.domain.model.sharedspace.WorkGroupNode
import com.linagora.android.linshare.domain.model.sharedspace.WorkGroupNodeId
import com.linagora.android.linshare.domain.repository.sharedspacesdocument.SharedSpacesDocumentRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SharedSpacesDocumentRepositoryImp @Inject constructor(
    private val sharedSpacesDocumentDataSource: SharedSpacesDocumentDataSource
) : SharedSpacesDocumentRepository {

    override suspend fun getAllChildNodes(
        sharedSpaceId: SharedSpaceId,
        parentNodeId: WorkGroupNodeId?
    ): List<WorkGroupNode> {
        return sharedSpacesDocumentDataSource.getAllChildNodes(sharedSpaceId, parentNodeId)
    }

    override suspend fun getSharedSpaceNode(
        sharedSpaceId: SharedSpaceId,
        nodeId: WorkGroupNodeId
    ): WorkGroupNode {
        return sharedSpacesDocumentDataSource.getSharedSpaceNode(sharedSpaceId, nodeId)
    }
}
