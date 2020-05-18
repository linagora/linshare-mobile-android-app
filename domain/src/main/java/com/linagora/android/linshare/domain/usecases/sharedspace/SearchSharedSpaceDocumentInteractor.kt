package com.linagora.android.linshare.domain.usecases.sharedspace

import arrow.core.Either
import com.linagora.android.linshare.domain.model.search.QueryString
import com.linagora.android.linshare.domain.model.sharedspace.SharedSpaceId
import com.linagora.android.linshare.domain.model.sharedspace.WorkGroupNode
import com.linagora.android.linshare.domain.model.sharedspace.WorkGroupNodeId
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
class SearchSharedSpaceDocumentInteractor @Inject constructor(
    private val sharedSpacesDocumentRepository: SharedSpacesDocumentRepository
) {

    operator fun invoke(
        sharedSpaceId: SharedSpaceId,
        parentNodeId: WorkGroupNodeId?,
        queryString: QueryString
    ): Flow<State<Either<Failure, Success>>> {
        return flow<State<Either<Failure, Success>>> {
            emitState { Either.right(Success.Loading) }

            val state = Either.catch { sharedSpacesDocumentRepository
                    .searchSharedSpaceDocuments(sharedSpaceId, parentNodeId, queryString) }
                .fold({ Either.left(SharedSpaceDocumentFailure(it)) }, ::generateSearchState)

            emitState { state }
        }
    }

    private fun generateSearchState(sharedSpaceDocumentResults: List<WorkGroupNode>): Either<Failure, Success> {
        return sharedSpaceDocumentResults.takeIf { it.isNotEmpty() }
            ?.let { Either.right(SearchSharedSpaceDocumentViewState(it)) }
            ?: let { Either.left(SearchSharedSpaceDocumentNoResult) }
    }
}
