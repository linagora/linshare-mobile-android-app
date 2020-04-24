package com.linagora.android.linshare.view.upload.controller

import arrow.core.Either
import com.linagora.android.linshare.domain.model.document.DocumentRequest
import com.linagora.android.linshare.domain.usecases.upload.UploadInteractor
import com.linagora.android.linshare.domain.usecases.upload.UploadSuccess
import com.linagora.android.linshare.domain.usecases.upload.UploadSuccessViewState
import com.linagora.android.linshare.domain.usecases.utils.Failure
import com.linagora.android.linshare.domain.usecases.utils.State
import com.linagora.android.linshare.domain.usecases.utils.Success
import com.linagora.android.linshare.domain.usecases.utils.ViewStateStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UploadMySpaceCommand(
    private val uploadInteractor: UploadInteractor,
    private val viewStateStore: ViewStateStore = ViewStateStore(),
    override val documentRequest: DocumentRequest
) : UploadCommand {

    override suspend fun execute(): Flow<State<Either<Failure, Success>>> {
        return uploadInteractor(documentRequest)
            .map { viewStateStore.storeAndGet(it) }
            .map { State<Either<Failure, Success>> { toUploadMySpaceState(documentRequest, it) } }
    }

    private fun toUploadMySpaceState(documentRequest: DocumentRequest, state: Either<Failure, Success>): Either<Failure, Success> {
        return state.map { success ->
            when (success) {
                is UploadSuccessViewState -> UploadSuccess(documentRequest.uploadFileName)
                else -> success
            }
        }
    }
}
