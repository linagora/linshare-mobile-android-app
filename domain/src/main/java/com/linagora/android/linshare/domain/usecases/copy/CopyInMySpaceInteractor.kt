package com.linagora.android.linshare.domain.usecases.copy

import arrow.core.Either
import com.linagora.android.linshare.domain.model.copy.CopyRequest
import com.linagora.android.linshare.domain.repository.document.DocumentRepository
import com.linagora.android.linshare.domain.usecases.myspace.CopyInMySpaceFailure
import com.linagora.android.linshare.domain.usecases.myspace.CopyInMySpaceSuccess
import com.linagora.android.linshare.domain.usecases.utils.Failure
import com.linagora.android.linshare.domain.usecases.utils.State
import com.linagora.android.linshare.domain.usecases.utils.Success
import com.linagora.android.linshare.domain.utils.emitState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CopyInMySpaceInteractor @Inject constructor(
    private val documentRepository: DocumentRepository
) {
    operator fun invoke(copyRequest: CopyRequest): Flow<State<Either<Failure, Success>>> {
        return flow<State<Either<Failure, Success>>> {
            val copyState = Either.catch { documentRepository.copy(copyRequest) }
                .bimap(::CopyInMySpaceFailure, ::CopyInMySpaceSuccess)

            emitState { copyState }
        }
    }
}
