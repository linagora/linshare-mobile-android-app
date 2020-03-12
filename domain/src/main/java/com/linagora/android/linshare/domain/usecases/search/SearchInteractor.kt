package com.linagora.android.linshare.domain.usecases.search

import arrow.core.Either
import com.linagora.android.linshare.domain.model.document.Document
import com.linagora.android.linshare.domain.repository.document.DocumentRepository
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
class SearchInteractor @Inject constructor(private val documentRepository: DocumentRepository) {

    operator fun invoke(query: String): Flow<State<Either<Failure, Success>>> {
        return flow<State<Either<Failure, Success>>> {
            query.takeIf { it.length > 2 }
                ?.let { performSearch(this, query) }
                ?: emitState { Either.right(SearchInitial) }
        }
    }

    private suspend fun performSearch(
        flowCollector: FlowCollector<State<Either<Failure, Success>>>,
        query: String
    ) {
        flowCollector.apply {
            emitState { Either.right(Success.Loading) }

            val state = Either.catch { documentRepository.search(query) }
                .fold(
                    ifLeft = { Either.left(SearchFailure(it)) },
                    ifRight = this@SearchInteractor::generateSearchState)

            emitState { state }
        }
    }

    private fun generateSearchState(documents: List<Document>): Either<Failure, Success> {
        return documents.takeIf { it.isNotEmpty() }
            ?.let { Either.right(SearchViewState(it)) }
            ?:let { Either.left(NoResults) }
    }
}
