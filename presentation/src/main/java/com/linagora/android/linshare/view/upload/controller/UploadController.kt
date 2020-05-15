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
