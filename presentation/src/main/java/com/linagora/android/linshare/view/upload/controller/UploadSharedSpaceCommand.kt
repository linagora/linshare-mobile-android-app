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

package com.linagora.android.linshare.view.upload.controller

import arrow.core.Either
import com.linagora.android.linshare.domain.model.document.DocumentRequest
import com.linagora.android.linshare.domain.model.quota.QuotaId
import com.linagora.android.linshare.domain.model.sharedspace.SharedSpaceId
import com.linagora.android.linshare.domain.model.sharedspace.WorkGroupNodeId
import com.linagora.android.linshare.domain.usecases.upload.UploadSuccess
import com.linagora.android.linshare.domain.usecases.upload.UploadToSharedSpaceInteractor
import com.linagora.android.linshare.domain.usecases.upload.UploadToSharedSpaceSuccess
import com.linagora.android.linshare.domain.usecases.utils.Failure
import com.linagora.android.linshare.domain.usecases.utils.State
import com.linagora.android.linshare.domain.usecases.utils.Success
import com.linagora.android.linshare.domain.usecases.utils.ViewStateStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.slf4j.LoggerFactory

class UploadSharedSpaceCommand(
    private val uploadToSharedSpaceInteractor: UploadToSharedSpaceInteractor,
    private val viewStateStore: ViewStateStore = ViewStateStore(),
    override val documentRequest: DocumentRequest,
    private val sharedSpaceId: SharedSpaceId,
    private val sharedSpaceQuotaId: QuotaId,
    private val parentNodeId: WorkGroupNodeId
) : UploadCommand {

    companion object {
        private val LOGGER = LoggerFactory.getLogger(UploadSharedSpaceCommand::class.java)
    }

    override suspend fun execute(): Flow<State<Either<Failure, Success>>> {
        LOGGER.info("execute()")
        return uploadToSharedSpaceInteractor(sharedSpaceId, sharedSpaceQuotaId, parentNodeId, documentRequest)
            .map { viewStateStore.storeAndGet(it) }
            .map { State<Either<Failure, Success>> { processUploadState(documentRequest, it) } }
    }

    private fun processUploadState(
        documentRequest: DocumentRequest,
        downloadState: Either<Failure, Success>
    ): Either<Failure, Success> {
        return downloadState.map { success ->
            when (success) {
                is UploadToSharedSpaceSuccess -> UploadSuccess(
                    uploadedDocumentUUID = success.workGroupNode.workGroupNodeId.uuid,
                    message = documentRequest.uploadFileName
                )
                else -> success
            }
        }
    }
}
