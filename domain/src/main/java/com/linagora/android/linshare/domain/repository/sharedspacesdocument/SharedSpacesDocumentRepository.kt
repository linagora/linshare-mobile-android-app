package com.linagora.android.linshare.domain.repository.sharedspacesdocument

import com.linagora.android.linshare.domain.model.document.DocumentRequest
import com.linagora.android.linshare.domain.model.search.QueryString
import com.linagora.android.linshare.domain.model.sharedspace.SharedSpaceId
import com.linagora.android.linshare.domain.model.sharedspace.WorkGroupNode
import com.linagora.android.linshare.domain.model.sharedspace.WorkGroupNodeId
import com.linagora.android.linshare.domain.model.upload.OnTransfer

interface SharedSpacesDocumentRepository {

    suspend fun getAllChildNodes(
        sharedSpaceId: SharedSpaceId,
        parentNodeId: WorkGroupNodeId? = null
    ): List<WorkGroupNode>

    suspend fun getSharedSpaceNode(
        sharedSpaceId: SharedSpaceId,
        nodeId: WorkGroupNodeId
    ): WorkGroupNode

    suspend fun uploadSharedSpaceDocument(
        documentRequest: DocumentRequest,
        sharedSpaceId: SharedSpaceId,
        parentNodeId: WorkGroupNodeId? = null,
        onTransfer: OnTransfer
    ): WorkGroupNode

    suspend fun searchSharedSpaceDocuments(
        sharedSpaceId: SharedSpaceId,
        parentNodeId: WorkGroupNodeId? = null,
        query: QueryString
    ): List<WorkGroupNode>

    suspend fun removeSharedSpaceNode(
        sharedSpaceId: SharedSpaceId,
        sharedSpaceNodeId: WorkGroupNodeId
    ): WorkGroupNode
}
