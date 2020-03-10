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
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RemoveDocumentInteractor @Inject constructor(
    private val documentRepository: DocumentRepository
) {

    operator fun invoke(uuid: UUID): Flow<State<Either<Failure, Success>>> {
        return flow {
            emitState { Either.right(Loading) }

            val state = Either.catch { documentRepository.remove(uuid) }
                .bimap(
                    leftOperation = { throwable -> RemoveDocumentFailure(throwable) },
                    rightOperation = { RemoveDocumentSuccessViewState(it) }
                )

            emitState { state }
        }
    }
}
