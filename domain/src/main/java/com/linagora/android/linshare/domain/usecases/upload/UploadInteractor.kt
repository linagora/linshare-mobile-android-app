package com.linagora.android.linshare.domain.usecases.upload

import arrow.core.Either
import com.linagora.android.linshare.domain.model.LinShareErrorCode
import com.linagora.android.linshare.domain.model.SystemErrorCode
import com.linagora.android.linshare.domain.model.document.DocumentRequest
import com.linagora.android.linshare.domain.repository.document.DocumentRepository
import com.linagora.android.linshare.domain.usecases.quota.EnoughAccountQuotaInteractor
import com.linagora.android.linshare.domain.usecases.quota.QuotaAccountNoMoreSpaceAvailable
import com.linagora.android.linshare.domain.usecases.quota.ValidAccountQuota
import com.linagora.android.linshare.domain.usecases.system.SystemState
import com.linagora.android.linshare.domain.usecases.utils.Failure
import com.linagora.android.linshare.domain.usecases.utils.State
import com.linagora.android.linshare.domain.usecases.utils.Success
import com.linagora.android.linshare.domain.usecases.utils.Success.Idle
import com.linagora.android.linshare.domain.utils.BusinessErrorCode
import com.linagora.android.linshare.domain.utils.BusinessErrorCode.QuotaAccountNoMoreSpaceErrorCode
import kotlinx.coroutines.channels.ProducerScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicReference
import javax.inject.Inject

class UploadInteractor @Inject constructor(
    private val enoughAccountQuotaInteractor: EnoughAccountQuotaInteractor,
    private val documentRepository: DocumentRepository
) {

    private val currentState = AtomicReference<Either<Failure, Success>>(Either.right(Idle))

    operator fun invoke(documentRequest: DocumentRequest): Flow<State<Either<Failure, Success>>> {
        return enoughAccountQuotaInteractor.invoke(documentRequest)
            .map { dispatchState(it) }
            .flatMapConcat { reactState(documentRequest, it) }
    }

    private fun dispatchState(state: State<Either<Failure, Success>>): Either<Failure, Success> {
        val newState = state(currentState.get())
        currentState.set(newState)
        return newState
    }

    private fun reactState(
        documentRequest: DocumentRequest,
        eitherState: Either<Failure, Success>
    ) = channelFlow {
        eitherState.fold(
            ifLeft = { send(State<Either<Failure, Success>> { eitherState }) },
            ifRight = { success -> doWithSuccessState(this, documentRequest, success) }
        )
    }

    private suspend fun doWithSuccessState(
        producerScope: ProducerScope<State<Either<Failure, Success>>>,
        documentRequest: DocumentRequest,
        success: Success
    ) {
        when (success) {
            is ValidAccountQuota -> { upload(producerScope, documentRequest) }
            else -> producerScope.send(State { Either.right(success) })
        }
    }

    private suspend fun upload(
        producerScope: ProducerScope<State<Either<Failure, Success>>>,
        documentRequest: DocumentRequest
    ) {
        try {
            val document =
                documentRepository.upload(documentRequest) { transferredBytes, totalBytes ->
                    producerScope.launch { producerScope.send(State { Either.right(UploadingViewState(transferredBytes, totalBytes)) }) }
                }
            producerScope.send(State { Either.right(UploadSuccessViewState(document)) })
        } catch (uploadException: UploadException) {
            when (val uploadErrorCode = uploadException.errorResponse.errCode) {
                is SystemErrorCode -> {
                    when (uploadErrorCode) {
                        BusinessErrorCode.InternetNotAvailableErrorCode -> producerScope.send(State { Either.left(SystemState.InternetNotAvailable) })
                    }
                }
                is LinShareErrorCode -> {
                    when (uploadErrorCode) {
                        QuotaAccountNoMoreSpaceErrorCode -> producerScope.send(State { Either.left(QuotaAccountNoMoreSpaceAvailable) })
                    }
                }
                else -> producerScope.send(State { Either.left(Failure.Error) })
            }
        }
    }
}
