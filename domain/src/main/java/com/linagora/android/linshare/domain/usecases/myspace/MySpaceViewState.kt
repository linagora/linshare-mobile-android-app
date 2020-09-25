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

package com.linagora.android.linshare.domain.usecases.myspace

import com.linagora.android.linshare.domain.model.OperatorType
import com.linagora.android.linshare.domain.model.document.Document
import com.linagora.android.linshare.domain.usecases.utils.Failure
import com.linagora.android.linshare.domain.usecases.utils.Failure.FeatureFailure
import com.linagora.android.linshare.domain.usecases.utils.Success

data class MySpaceViewState(val documents: List<Document>) : Success.ViewState()
object EmptyMySpaceState : Failure.FeatureFailure()
data class MySpaceFailure(val throwable: Throwable) : FeatureFailure()
data class GetDocumentSuccess(val document: Document) : Success.ViewState()
data class GetDocumentFailure(val throwable: Throwable) : FeatureFailure()
data class ContextMenuClick(val document: Document) : Success.ViewEvent()
data class DocumentItemClick(val document: Document) : Success.OnlineViewEvent(OperatorType.OnItemClick)
data class DownloadClick(val document: Document) : Success.ViewEvent()
object UploadButtonBottomBarClick : Success.ViewEvent()
data class RemoveDocumentSuccessViewState(val document: Document) : Success.ViewState()
data class RemoveDocumentFailure(val throwable: Throwable) : FeatureFailure()
data class RemoveClick(val document: Document) : Success.OnlineViewEvent(OperatorType.DeleteDocument)
object SearchButtonClick : Success.ViewEvent()
data class ShareItemClick(val document: Document) : Success.ViewEvent()
data class CopyInMySpaceFailure(val throwable: Throwable) : FeatureFailure()
data class CopyInMySpaceSuccess(val documents: List<Document>) : Success.ViewState()
object CopyFailedWithFileSizeExceed : FeatureFailure()
object CopyFailedWithQuotaReach : FeatureFailure()
data class DocumentDetailsClick(val document: Document) : Success.OnlineViewEvent(OperatorType.ViewDetails)
data class RenameDocumentSuccess(val document: Document) : Success.ViewState()
data class RenameDocumentFailure(val throwable: Throwable) : FeatureFailure()
data class DuplicateDocumentMySpaceClick(val document: Document) : Success.OnlineViewEvent(OperatorType.DuplicateFile)
