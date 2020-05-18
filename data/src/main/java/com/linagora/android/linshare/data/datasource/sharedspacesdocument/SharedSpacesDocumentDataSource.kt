package com.linagora.android.linshare.data.datasource.sharedspacesdocument

import com.linagora.android.linshare.domain.model.document.DocumentRequest
import com.linagora.android.linshare.domain.model.search.QueryString
import com.linagora.android.linshare.domain.model.sharedspace.SharedSpaceId
import com.linagora.android.linshare.domain.model.sharedspace.WorkGroupNode
import com.linagora.android.linshare.domain.model.sharedspace.WorkGroupNodeId
import com.linagora.android.linshare.domain.model.upload.OnTransfer
import com.linagora.android.linshare.domain.usecases.upload.UploadException

interface SharedSpacesDocumentDataSource {

    suspend fun getAllChildNodes(
        sharedSpaceId: SharedSpaceId,
        parentNodeId: WorkGroupNodeId?
    ): List<WorkGroupNode>

    suspend fun getSharedSpaceNode(
        sharedSpaceId: SharedSpaceId,
        nodeId: WorkGroupNodeId
    ): WorkGroupNode

    @Throws(UploadException::class)
    suspend fun uploadSharedSpaceDocument(
        documentRequest: DocumentRequest,
        sharedSpaceId: SharedSpaceId,
        parentNodeId: WorkGroupNodeId? = null,
        onTransfer: OnTransfer
    ): WorkGroupNode

    suspend fun searchSharedSpaceDocument(
        sharedSpaceId: SharedSpaceId,
        parentNodeId: WorkGroupNodeId? = null,
        queryString: QueryString
    ): List<WorkGroupNode>
}
