package com.linagora.android.linshare.view.upload.controller

import android.content.Context
import arrow.core.Either
import com.linagora.android.linshare.R
import com.linagora.android.linshare.domain.model.document.DocumentRequest
import com.linagora.android.linshare.domain.network.InternetNotAvailable
import com.linagora.android.linshare.domain.usecases.quota.QuotaAccountNoMoreSpaceAvailable
import com.linagora.android.linshare.domain.usecases.upload.UploadFailed
import com.linagora.android.linshare.domain.usecases.upload.UploadInteractor
import com.linagora.android.linshare.domain.usecases.utils.Failure
import com.linagora.android.linshare.domain.usecases.utils.State
import com.linagora.android.linshare.domain.usecases.utils.Success
import com.linagora.android.linshare.domain.usecases.utils.ViewStateStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class UploadController @Inject constructor(
    private val context: Context,
    private val uploadInteractor: UploadInteractor,
    private val viewStateStore: ViewStateStore
) {

    suspend fun upload(documentRequest: DocumentRequest): Flow<State<Either<Failure, Success>>> {
        val uploadCommand = createUploadCommand()
        return uploadCommand.execute(documentRequest)
            .map { viewStateStore.storeAndGet(it) }
            .map { State<Either<Failure, Success>> { mapGenericState(it) } }
    }

    private fun createUploadCommand(): UploadCommand {
        return UploadMySpaceCommand(uploadInteractor)
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
