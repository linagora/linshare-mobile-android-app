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
