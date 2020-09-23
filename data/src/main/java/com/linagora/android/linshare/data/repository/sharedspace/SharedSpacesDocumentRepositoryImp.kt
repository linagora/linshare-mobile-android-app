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

package com.linagora.android.linshare.data.repository.sharedspace

import com.linagora.android.linshare.data.datasource.sharedspacesdocument.SharedSpacesDocumentDataSource
import com.linagora.android.linshare.domain.model.copy.CopyRequest
import com.linagora.android.linshare.domain.model.document.DocumentRequest
import com.linagora.android.linshare.domain.model.search.QueryString
import com.linagora.android.linshare.domain.model.sharedspace.CreateSharedSpaceNodeRequest
import com.linagora.android.linshare.domain.model.sharedspace.RenameWorkGroupNodeRequest
import com.linagora.android.linshare.domain.model.sharedspace.SharedSpaceId
import com.linagora.android.linshare.domain.model.sharedspace.WorkGroupFolder
import com.linagora.android.linshare.domain.model.sharedspace.WorkGroupNode
import com.linagora.android.linshare.domain.model.sharedspace.WorkGroupNodeId
import com.linagora.android.linshare.domain.model.upload.OnTransfer
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

    override suspend fun uploadSharedSpaceDocument(
        documentRequest: DocumentRequest,
        sharedSpaceId: SharedSpaceId,
        parentNodeId: WorkGroupNodeId?,
        onTransfer: OnTransfer
    ): WorkGroupNode {
        return sharedSpacesDocumentDataSource
            .uploadSharedSpaceDocument(documentRequest, sharedSpaceId, parentNodeId, onTransfer)
    }

    override suspend fun searchSharedSpaceDocuments(
        sharedSpaceId: SharedSpaceId,
        parentNodeId: WorkGroupNodeId?,
        query: QueryString
    ): List<WorkGroupNode> {
        return sharedSpacesDocumentDataSource.searchSharedSpaceDocument(sharedSpaceId, parentNodeId, query)
    }

    override suspend fun removeSharedSpaceNode(
        sharedSpaceId: SharedSpaceId,
        sharedSpaceNodeUuid: WorkGroupNodeId
    ): WorkGroupNode {
        return sharedSpacesDocumentDataSource.removeSharedSpaceNode(sharedSpaceId, sharedSpaceNodeUuid)
    }

    override suspend fun copyToSharedSpace(
        copyRequest: CopyRequest,
        destinationSharedSpaceId: SharedSpaceId,
        destinationParentNodeId: WorkGroupNodeId?
    ): List<WorkGroupNode> {
        return sharedSpacesDocumentDataSource.copyToSharedSpace(
            copyRequest,
            destinationSharedSpaceId,
            destinationParentNodeId
        )
    }

    override suspend fun duplicateWorkGroupNode(
        copyRequest: CopyRequest,
        sharedSpaceUuid: SharedSpaceId
    ): List<WorkGroupNode> {
        return sharedSpacesDocumentDataSource.duplicateInWorkGroupNode(
            copyRequest,
            sharedSpaceUuid
        )
    }

    override suspend fun createSharedSpaceFolder(
        sharedSpaceId: SharedSpaceId,
        createSharedSpaceNodeRequest: CreateSharedSpaceNodeRequest
    ): WorkGroupFolder {
        return sharedSpacesDocumentDataSource.createSharedSpaceFolder(sharedSpaceId, createSharedSpaceNodeRequest)
    }

    override suspend fun renameSharedSpaceNode(
        sharedSpaceId: SharedSpaceId,
        sharedSpaceNodeId: WorkGroupNodeId,
        renameRequest: RenameWorkGroupNodeRequest
    ): WorkGroupNode {
        return sharedSpacesDocumentDataSource.renameSharedSpaceNode(sharedSpaceId, sharedSpaceNodeId, renameRequest)
    }
}
