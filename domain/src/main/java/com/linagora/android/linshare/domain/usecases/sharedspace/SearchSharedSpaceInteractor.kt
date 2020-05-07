package com.linagora.android.linshare.domain.usecases.sharedspace

import arrow.core.Either
import com.linagora.android.linshare.domain.model.search.QueryString
import com.linagora.android.linshare.domain.model.sharedspace.SharedSpaceNodeNested
import com.linagora.android.linshare.domain.repository.sharedspace.SharedSpaceRepository
import com.linagora.android.linshare.domain.usecases.utils.Failure
import com.linagora.android.linshare.domain.usecases.utils.State
import com.linagora.android.linshare.domain.usecases.utils.Success
import com.linagora.android.linshare.domain.utils.emitState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SearchSharedSpaceInteractor @Inject constructor(private val sharedSpaceRepository: SharedSpaceRepository) {

    operator fun invoke(query: QueryString): Flow<State<Either<Failure, Success>>> {
        return flow<State<Either<Failure, Success>>> {
            query.takeIf { it.value.length > 2 }
                ?.let { performSearch(this, query) }
                ?: emitState { Either.right(SearchSharedSpaceInitial) }
        }
    }

    private suspend fun performSearch(
        flowCollector: FlowCollector<State<Either<Failure, Success>>>,
        query: QueryString
    ) {
        flowCollector.apply {
            emitState { Either.right(Success.Loading) }

            val state = Either.catch { sharedSpaceRepository.search(query) }
                .fold(
                    ifLeft = { Either.left(SharedSpaceFailure(it)) },
                    ifRight = this@SearchSharedSpaceInteractor::generateSearchState)

            emitState { state }
        }
    }

    private fun generateSearchState(shareSpaceNodeNesteds: List<SharedSpaceNodeNested>): Either<Failure, Success> {
        return shareSpaceNodeNesteds.takeIf { it.isNotEmpty() }
            ?.let { Either.right(SearchSharedSpaceViewState(it)) }
            ?: let { Either.left(NoResultsSearchSharedSpace) }
    }
}
