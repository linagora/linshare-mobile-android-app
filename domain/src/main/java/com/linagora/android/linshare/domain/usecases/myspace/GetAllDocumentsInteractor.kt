package com.linagora.android.linshare.domain.usecases.myspace

import arrow.core.Either
import com.linagora.android.linshare.domain.repository.document.DocumentRepository
import com.linagora.android.linshare.domain.usecases.utils.Failure
import com.linagora.android.linshare.domain.usecases.utils.State
import com.linagora.android.linshare.domain.usecases.utils.Success
import com.linagora.android.linshare.domain.usecases.utils.Success.Loading
import com.linagora.android.linshare.domain.utils.emitState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.slf4j.LoggerFactory
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetAllDocumentsInteractor @Inject constructor(
    private val documentRepository: DocumentRepository
) {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(GetAllDocumentsInteractor::class.java)
    }

    operator fun invoke(): Flow<State<Either<Failure, Success>>> {
        return flow<State<Either<Failure, Success>>> {
            emitState { Either.right(Loading) }
            documentRepository.getAll()
                .let { documents ->
                    documents.forEach {
                        LOGGER.info("document detail: $it")
                    }
                }
        }
    }
}
