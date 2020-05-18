package com.linagora.android.linshare.data.datasource.network

import com.linagora.android.linshare.data.api.LinshareApi
import com.linagora.android.linshare.data.datasource.sharedspacesdocument.SharedSpacesDocumentDataSource
import com.linagora.android.linshare.data.network.NetworkExecutor
import com.linagora.android.linshare.data.network.handler.UploadNetworkRequestHandler
import com.linagora.android.linshare.domain.model.document.DocumentRequest
import com.linagora.android.linshare.domain.model.search.QueryString
import com.linagora.android.linshare.domain.model.sharedspace.PartParameter.FILE_PARAMETER_FIELD
import com.linagora.android.linshare.domain.model.sharedspace.SharedSpaceId
import com.linagora.android.linshare.domain.model.sharedspace.WorkGroupNode
import com.linagora.android.linshare.domain.model.sharedspace.WorkGroupNodeId
import com.linagora.android.linshare.domain.model.sharedspace.nameContains
import com.linagora.android.linshare.domain.model.upload.OnTransfer
import okhttp3.MultipartBody
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LinShareSharedSpacesDocumentDataSource @Inject constructor(
    private val linShareApi: LinshareApi,
    private val networkExecutor: NetworkExecutor,
    private val uploadNetworkRequestHandler: UploadNetworkRequestHandler
) : SharedSpacesDocumentDataSource {

    override suspend fun getAllChildNodes(
        sharedSpaceId: SharedSpaceId,
        parentNodeId: WorkGroupNodeId?
    ): List<WorkGroupNode> {
        val workGroupNodes = parentNodeId
            ?.let { parentId -> linShareApi.getAllSharedSpaceNode(sharedSpaceId.uuid.toString(), parentId.uuid.toString()) }
            ?: linShareApi.getAllSharedSpaceNode(sharedSpaceId.uuid.toString())
        return workGroupNodes.sortedByDescending { it.modificationDate }
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

    override suspend fun uploadSharedSpaceDocument(
        documentRequest: DocumentRequest,
        sharedSpaceId: SharedSpaceId,
        parentNodeId: WorkGroupNodeId?,
        onTransfer: OnTransfer
    ): WorkGroupNode {
        return networkExecutor.execute(
            networkRequest = { uploadToSharedSpace(documentRequest, sharedSpaceId, parentNodeId, onTransfer) },
            onFailure = { uploadNetworkRequestHandler(it) }
        )
    }

    override suspend fun searchSharedSpaceDocument(
        sharedSpaceId: SharedSpaceId,
        parentNodeId: WorkGroupNodeId?,
        queryString: QueryString
    ): List<WorkGroupNode> {
        return getAllChildNodes(sharedSpaceId, parentNodeId)
            ?.filter { node -> node.nameContains(queryString.value) }
    }

    private suspend fun uploadToSharedSpace(
        documentRequest: DocumentRequest,
        sharedSpaceId: SharedSpaceId,
        parentNodeId: WorkGroupNodeId?,
        onTransfer: OnTransfer
    ): WorkGroupNode {
        val fileRequestBody = documentRequest.toMeasureRequestBody(onTransfer)
        return parentNodeId
            ?.let {
                linShareApi.uploadToSharedSpace(
                    sharedSpaceUuid = sharedSpaceId.uuid.toString(),
                    parentUuid = it.uuid.toString(),
                    file = MultipartBody.Part.createFormData(
                        FILE_PARAMETER_FIELD,
                        documentRequest.uploadFileName,
                        fileRequestBody),
                    fileSize = documentRequest.file.length())
            } ?: linShareApi.uploadToSharedSpace(
                    sharedSpaceUuid = sharedSpaceId.uuid.toString(),
                    file = MultipartBody.Part.createFormData(
                        FILE_PARAMETER_FIELD,
                        documentRequest.uploadFileName,
                        fileRequestBody),
                    fileSize = documentRequest.file.length())
    }
}
