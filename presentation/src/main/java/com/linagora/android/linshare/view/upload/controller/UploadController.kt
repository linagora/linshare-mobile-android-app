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

import android.content.Context
import androidx.work.Data
import arrow.core.Either
import com.linagora.android.linshare.R
import com.linagora.android.linshare.domain.model.document.DocumentRequest
import com.linagora.android.linshare.domain.model.quota.QuotaId
import com.linagora.android.linshare.domain.model.sharedspace.SharedSpaceId
import com.linagora.android.linshare.domain.model.sharedspace.WorkGroupNodeId
import com.linagora.android.linshare.domain.network.InternetNotAvailable
import com.linagora.android.linshare.domain.usecases.quota.QuotaAccountNoMoreSpaceAvailable
import com.linagora.android.linshare.domain.usecases.upload.UploadFailed
import com.linagora.android.linshare.domain.usecases.upload.UploadInteractor
import com.linagora.android.linshare.domain.usecases.upload.UploadToSharedSpaceInteractor
import com.linagora.android.linshare.domain.usecases.utils.Failure
import com.linagora.android.linshare.domain.usecases.utils.State
import com.linagora.android.linshare.domain.usecases.utils.Success
import com.linagora.android.linshare.domain.usecases.utils.ViewStateStore
import com.linagora.android.linshare.view.upload.request.UploadRequestType
import com.linagora.android.linshare.view.upload.worker.UploadWorker
import com.linagora.android.linshare.view.upload.worker.UploadWorker.Companion.UPLOAD_TO_PARENT_NODE_ID_KEY
import com.linagora.android.linshare.view.upload.worker.UploadWorker.Companion.UPLOAD_TO_SHARED_SPACE_ID_KEY
import com.linagora.android.linshare.view.upload.worker.UploadWorker.Companion.UPLOAD_TO_SHARED_SPACE_QUOTA_ID_KEY
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.slf4j.LoggerFactory
import java.util.UUID
import javax.inject.Inject

class UploadController @Inject constructor(
    private val context: Context,
    private val uploadInteractor: UploadInteractor,
    private val uploadToSharedSpaceInteractor: UploadToSharedSpaceInteractor,
    private val viewStateStore: ViewStateStore
) {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(UploadController::class.java)
    }

    suspend fun upload(uploadCommand: UploadCommand): Flow<State<Either<Failure, Success>>> {
        return uploadCommand.execute()
            .map { viewStateStore.storeAndGet(it) }
            .map { State<Either<Failure, Success>> { mapGenericState(it) } }
    }

    fun createUploadCommand(uploadInput: Data, documentRequest: DocumentRequest): UploadCommand {
        val uploadRequestType = UploadRequestType.valueOf(
            uploadInput.getString(UploadWorker.UPLOAD_REQUEST_TYPE)
                ?: UploadRequestType.UploadToMySpace.name
        )
        return when (uploadRequestType) {
            UploadRequestType.UploadToSharedSpace -> createUploadSharedSpaceCommand(uploadInput, documentRequest)
            else -> UploadMySpaceCommand(uploadInteractor, viewStateStore, documentRequest)
        }
    }

    private fun createUploadSharedSpaceCommand(
        uploadInput: Data,
        documentRequest: DocumentRequest
    ): UploadCommand {
        LOGGER.info("createUploadSharedSpaceCommand()")

        val uploadSharedSpaceId = uploadInput.getString(UPLOAD_TO_SHARED_SPACE_ID_KEY)
        val sharedSpaceQuotaId = uploadInput.getString(UPLOAD_TO_SHARED_SPACE_QUOTA_ID_KEY)
        val uploadParentNodeId = uploadInput.getString(UPLOAD_TO_PARENT_NODE_ID_KEY)

        require(uploadSharedSpaceId != null)
        require(sharedSpaceQuotaId != null)
        require(uploadParentNodeId != null)

        return UploadSharedSpaceCommand(
            uploadToSharedSpaceInteractor = uploadToSharedSpaceInteractor,
            viewStateStore = viewStateStore,
            documentRequest = documentRequest,
            sharedSpaceId = SharedSpaceId(UUID.fromString(uploadSharedSpaceId)),
            sharedSpaceQuotaId = QuotaId(UUID.fromString(sharedSpaceQuotaId)),
            parentNodeId = WorkGroupNodeId(UUID.fromString(uploadParentNodeId))
        )
    }

    private fun mapGenericState(state: Either<Failure, Success>): Either<Failure, Success> {
        return state.mapLeft(this::mapFailureState)
    }

    private fun mapFailureState(failure: Failure): Failure {
        return when (failure) {
            QuotaAccountNoMoreSpaceAvailable -> UploadFailed(context.getString(R.string.no_more_space_avalable))
            InternetNotAvailable -> UploadFailed(context.getString(R.string.internet_not_available))
            else -> failure
        }
    }
}
