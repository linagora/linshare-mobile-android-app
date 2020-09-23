/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 *
 * Copyright (C) 2020 LINAGORA
 *
 * This program is free software: you can redistribute it and/or modify it under the
 * terms of the GNU Affero General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later version,
 * provided you comply with the Additional Terms applicable for LinShare software by
 * Linagora pursuant to Section 7 of the GNU Affero General Public License,
 * subsections (b), (c), and (e), pursuant to which you must notably (i) retain the
 * display in the interface of the “LinShare™” trademark/logo, the "Libre & Free" mention,
 * the words “You are using the Free and Open Source version of LinShare™, powered by
 * Linagora © 2009–2020. Contribute to Linshare R&D by subscribing to an Enterprise
 * offer!”. You must also retain the latter notice in all asynchronous messages such as
 * e-mails sent with the Program, (ii) retain all hypertext links between LinShare and
 * http://www.linshare.org, between linagora.com and Linagora, and (iii) refrain from
 * infringing Linagora intellectual property rights over its trademarks and commercial
 * brands. Other Additional Terms apply, see
 * <http://www.linshare.org/licenses/LinShare-License_AfferoGPL-v3.pdf>
 * for more details.
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for
 * more details.
 * You should have received a copy of the GNU Affero General Public License and its
 * applicable Additional Terms for LinShare along with this program. If not, see
 * <http://www.gnu.org/licenses/> for the GNU Affero General Public License version
 *  3 and <http://www.linshare.org/licenses/LinShare-License_AfferoGPL-v3.pdf> for
 *  the Additional Terms applicable to LinShare software.
 */

package com.linagora.android.linshare.data.datasource.network

import com.linagora.android.linshare.data.api.LinshareApi
import com.linagora.android.linshare.data.datasource.sharedspacesdocument.SharedSpacesDocumentDataSource
import com.linagora.android.linshare.data.network.NetworkExecutor
import com.linagora.android.linshare.data.network.handler.RemoveSharedSpaceDocumentNetworkRequestHandler
import com.linagora.android.linshare.data.network.handler.UploadNetworkRequestHandler
import com.linagora.android.linshare.domain.model.copy.CopyRequest
import com.linagora.android.linshare.domain.model.document.DocumentRequest
import com.linagora.android.linshare.domain.model.search.QueryString
import com.linagora.android.linshare.domain.model.sharedspace.CreateSharedSpaceNodeRequest
import com.linagora.android.linshare.domain.model.sharedspace.PartParameter.FILE_PARAMETER_FIELD
import com.linagora.android.linshare.domain.model.sharedspace.RenameWorkGroupNodeRequest
import com.linagora.android.linshare.domain.model.sharedspace.SharedSpaceId
import com.linagora.android.linshare.domain.model.sharedspace.WorkGroupFolder
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
    private val uploadNetworkRequestHandler: UploadNetworkRequestHandler,
    private val removeSharedSpaceDocumentNetworkRequestHandler: RemoveSharedSpaceDocumentNetworkRequestHandler
) : SharedSpacesDocumentDataSource {

    override suspend fun getAllChildNodes(
        sharedSpaceId: SharedSpaceId,
        parentNodeId: WorkGroupNodeId?
    ): List<WorkGroupNode> {
        return parentNodeId
            ?.let { parentId -> linShareApi.getAllSharedSpaceNode(sharedSpaceId.uuid.toString(), parentId.uuid.toString()) }
            ?: linShareApi.getAllSharedSpaceNode(sharedSpaceId.uuid.toString())
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

    override suspend fun removeSharedSpaceNode(
        sharedSpaceId: SharedSpaceId,
        sharedSpaceNodeId: WorkGroupNodeId
    ): WorkGroupNode {
        return networkExecutor.execute(
            networkRequest = {
                linShareApi.removeSharedSpaceNode(
                    sharedSpaceId.uuid.toString(),
                    sharedSpaceNodeId.uuid.toString()
                )
            },
            onFailure = { removeSharedSpaceDocumentNetworkRequestHandler(it) }
        )
    }

    override suspend fun copyToSharedSpace(
        copyRequest: CopyRequest,
        destinationSharedSpaceId: SharedSpaceId,
        destinationParentNodeId: WorkGroupNodeId?
    ): List<WorkGroupNode> {
        return linShareApi.copyWorkGroupNodeToSharedSpaceDestination(
            destinationSharedSpaceId.uuid.toString(),
            destinationParentNodeId?.uuid.toString(),
            copyRequest
        )
    }

    override suspend fun duplicateInWorkGroupNode(
        copyRequest: CopyRequest,
        sharedSpaceUuid: SharedSpaceId
    ): List<WorkGroupNode> {
        return linShareApi.duplicateWorkGroupNode(
            sharedSpaceUuid.uuid.toString(),
            copyRequest
        )
    }

    override suspend fun createSharedSpaceFolder(
        sharedSpaceId: SharedSpaceId,
        createSharedSpaceNodeRequest: CreateSharedSpaceNodeRequest
    ): WorkGroupFolder {
        return linShareApi.createSharedSpaceFolder(sharedSpaceId.uuid.toString(), createSharedSpaceNodeRequest)
    }

    override suspend fun renameSharedSpaceNode(
        sharedSpaceId: SharedSpaceId,
        sharedSpaceNodeId: WorkGroupNodeId,
        renameRequest: RenameWorkGroupNodeRequest
    ): WorkGroupNode {
        return linShareApi.renameSharedSpaceNode(
            sharedSpaceId.uuid.toString(),
            sharedSpaceNodeId.uuid.toString(),
            renameRequest
        )
    }
}
