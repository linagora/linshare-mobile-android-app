package com.linagora.android.linshare.domain.usecases.copy

import arrow.core.Either
import com.linagora.android.linshare.domain.model.copy.CopyRequest
import com.linagora.android.linshare.domain.repository.document.DocumentRepository
import com.linagora.android.linshare.domain.usecases.InteractorHandler
import com.linagora.android.linshare.domain.usecases.myspace.CopyInMySpaceSuccess
import com.linagora.android.linshare.domain.usecases.utils.Failure
import com.linagora.android.linshare.domain.usecases.utils.State
import com.linagora.android.linshare.domain.usecases.utils.Success
import com.linagora.android.linshare.domain.utils.sendState
import kotlinx.coroutines.channels.ProducerScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CopyInMySpaceInteractor @Inject constructor(
    private val interactorHandler: InteractorHandler,
    private val documentRepository: DocumentRepository
) {
    operator fun invoke(copyRequest: CopyRequest): Flow<State<Either<Failure, Success>>> {
        return channelFlow<State<Either<Failure, Success>>> {
            interactorHandler.handle(
                execution = { executeCopy(this, copyRequest) },
                onCatch = { CopyErrorHandler(this)(it) }
            )
        }
    }

    private suspend fun executeCopy(
        producerScope: ProducerScope<State<Either<Failure, Success>>>,
        copyRequest: CopyRequest
    ) {
        val documents = documentRepository.copy(copyRequest)
        producerScope.sendState { Either.right(CopyInMySpaceSuccess(documents)) }
    }
}
