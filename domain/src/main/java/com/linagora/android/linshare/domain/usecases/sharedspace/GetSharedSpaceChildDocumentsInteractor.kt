package com.linagora.android.linshare.domain.usecases.sharedspace

import arrow.core.Either
import com.linagora.android.linshare.domain.model.sharedspace.SharedSpaceId
import com.linagora.android.linshare.domain.model.sharedspace.WorkGroupNode
import com.linagora.android.linshare.domain.repository.sharedspacesdocument.SharedSpacesDocumentRepository
import com.linagora.android.linshare.domain.usecases.utils.Failure
import com.linagora.android.linshare.domain.usecases.utils.State
import com.linagora.android.linshare.domain.usecases.utils.Success
import com.linagora.android.linshare.domain.utils.emitState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetSharedSpaceChildDocumentsInteractor @Inject constructor(
    private val sharedSpacesDocumentRepository: SharedSpacesDocumentRepository
) {

    operator fun invoke(sharedSpaceId: SharedSpaceId): Flow<State<Either<Failure, Success>>> {
        return flow<State<Either<Failure, Success>>> {
            emitState { Either.right(Success.Loading) }

            val state = Either.catch { sharedSpacesDocumentRepository.getAllChildNodes(sharedSpaceId) }
                .bimap(::SharedSpaceDocumentFailure, ::generateState)

            emitState { state }
        }
    }

    private fun generateState(sharedSpaceDocuments: List<WorkGroupNode>): Success {
        return sharedSpaceDocuments.takeIf { it.isNotEmpty() }
            ?.let(::SharedSpaceDocumentViewState)
            ?: SharedSpaceDocumentEmpty
    }
}
