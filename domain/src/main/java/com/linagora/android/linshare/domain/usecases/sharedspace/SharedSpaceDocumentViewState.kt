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

package com.linagora.android.linshare.domain.usecases.sharedspace

import com.linagora.android.linshare.domain.model.OperatorType
import com.linagora.android.linshare.domain.model.sharedspace.SharedSpaceId
import com.linagora.android.linshare.domain.model.sharedspace.WorkGroupDocument
import com.linagora.android.linshare.domain.model.sharedspace.WorkGroupFolder
import com.linagora.android.linshare.domain.model.sharedspace.WorkGroupNode
import com.linagora.android.linshare.domain.model.sharedspace.WorkGroupNodeId
import com.linagora.android.linshare.domain.usecases.utils.Failure
import com.linagora.android.linshare.domain.usecases.utils.Success

data class SharedSpaceDocumentViewState(val documents: List<WorkGroupNode>) : Success.ViewState()
object SharedSpaceDocumentEmpty : Success.ViewState()
data class SharedSpaceDocumentFailure(val throwable: Throwable) : Failure.FeatureFailure()
data class GetSharedSpaceNodeFail(val throwable: Throwable) : Failure.FeatureFailure()
data class GetSharedSpaceNodeSuccess(val node: WorkGroupNode) : Success.ViewState()
data class SharedSpaceDocumentItemClick(val workGroupNode: WorkGroupNode) : Success.OnlineViewEvent(OperatorType.OnItemClick)
data class SharedSpaceDocumentContextMenuClick(val workGroupDocument: WorkGroupDocument) : Success.ViewEvent()
object SharedSpaceDocumentOnBackClick : Success.ViewEvent()
data class DownloadSharedSpaceNodeClick(val workGroupNode: WorkGroupNode) : Success.ViewEvent()
object SharedSpaceDocumentOnAddButtonClick : Success.ViewEvent()
object SearchSharedSpaceDocumentNoResult : Failure.FeatureFailure()
data class SearchSharedSpaceDocumentViewState(val documents: List<WorkGroupNode>) : Success.ViewState()
data class RemoveSharedSpaceNodeSuccessViewState(val workGroupNode: WorkGroupNode) : Success.ViewState()
data class RemoveSharedSpaceNodeFailure(val throwable: Throwable) : Failure.FeatureFailure()
data class RemoveSharedSpaceNodeClick(val workGroupNode: WorkGroupNode) : Success.ViewEvent()
object RemoveNodeNotFoundSharedSpaceState : Failure.FeatureFailure()
data class SharedSpaceFolderContextMenuClick(val workGroupFolder: WorkGroupFolder) : Success.ViewEvent()
data class CreateSharedSpaceFolderSuccessViewState(val workGroupFolder: WorkGroupFolder) : Success.ViewState()
data class CreateSharedSpaceFolderFailure(val throwable: Throwable) : Failure.FeatureFailure()
object SharedSpaceDocumentOnUploadFileClick : Success.OfflineViewEvent(OperatorType.UploadFile)
object SharedSpaceDocumentOnCreateFolderClick : Success.OfflineViewEvent(OperatorType.CreateFolder)

data class CopyToSharedSpaceSuccess(
    val destinationSharedSpaceId: SharedSpaceId,
    val destinationParentNodeId: WorkGroupNodeId? = null
) : Success.ViewState()
data class CopyToSharedSpaceFailure(val throwable: Throwable) : Failure.FeatureFailure()
