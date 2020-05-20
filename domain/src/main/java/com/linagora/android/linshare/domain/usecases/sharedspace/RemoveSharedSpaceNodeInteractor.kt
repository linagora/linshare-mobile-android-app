package com.linagora.android.linshare.domain.usecases.sharedspace

import arrow.core.Either
import com.linagora.android.linshare.domain.model.sharedspace.SharedSpaceId
import com.linagora.android.linshare.domain.model.sharedspace.WorkGroupNodeId
import com.linagora.android.linshare.domain.repository.sharedspacesdocument.SharedSpacesDocumentRepository
import com.linagora.android.linshare.domain.usecases.utils.Failure
import com.linagora.android.linshare.domain.usecases.utils.State
import com.linagora.android.linshare.domain.usecases.utils.Success
import com.linagora.android.linshare.domain.usecases.utils.Success.Loading
import com.linagora.android.linshare.domain.utils.emitState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RemoveSharedSpaceNodeInteractor @Inject constructor(
    private val sharedSpacesDocumentRepository: SharedSpacesDocumentRepository
) {

    operator fun invoke(sharedSpaceId: SharedSpaceId, sharedSpaceNodeUuid: WorkGroupNodeId): Flow<State<Either<Failure, Success>>> {
        return flow<State<Either<Failure, Success>>> {
            emitState { Either.right(Loading) }

            val state = Either.catch { sharedSpacesDocumentRepository.removeSharedSpaceNode(sharedSpaceId, sharedSpaceNodeUuid) }
                .fold(ifLeft = { catchRemoveDocumentError(it) }, ifRight = { Either.right(RemoveSharedSpaceNodeSuccessViewState(it)) })

            emitState { state }
        }
    }

    private fun catchRemoveDocumentError(throwable: Throwable): Either<Failure, Success> {
        return throwable.takeIf { it is RemoveNotFoundSharedSpaceDocumentException }
            ?.let { Either.left(RemoveNodeNotFoundSharedSpaceState) }
            ?: Either.left(RemoveSharedSpaceNodeFailure(throwable))
    }
}
