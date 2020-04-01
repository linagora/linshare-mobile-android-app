package com.linagora.android.linshare.domain.usecases.share

import arrow.core.Either
import com.linagora.android.linshare.domain.model.share.ShareRequest
import com.linagora.android.linshare.domain.repository.document.DocumentRepository
import com.linagora.android.linshare.domain.usecases.utils.Failure
import com.linagora.android.linshare.domain.usecases.utils.State
import com.linagora.android.linshare.domain.usecases.utils.Success
import com.linagora.android.linshare.domain.utils.emitState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ShareDocumentInteractor @Inject constructor(
    private val documentRepository: DocumentRepository
) {

    operator fun invoke(shareRequest: ShareRequest): Flow<State<Either<Failure, Success>>> {
        return flow<State<Either<Failure, Success>>> {
            emitState { Either.right(Success.Loading) }

            val shareState = Either
                .catch { documentRepository.share(shareRequest) }
                .bimap(::ShareFailureState, ::ShareViewState)

            emitState { shareState }
        }
    }
}
