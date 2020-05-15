package com.linagora.android.linshare.domain.usecases.upload

import arrow.core.Either
import com.linagora.android.linshare.domain.model.document.DocumentRequest
import com.linagora.android.linshare.domain.model.upload.TotalBytes
import com.linagora.android.linshare.domain.model.upload.TransferredBytes
import com.linagora.android.linshare.domain.repository.document.DocumentRepository
import com.linagora.android.linshare.domain.usecases.InteractorHandler
import com.linagora.android.linshare.domain.usecases.auth.AuthenticationViewState
import com.linagora.android.linshare.domain.usecases.auth.GetAuthenticatedInfoInteractor
import com.linagora.android.linshare.domain.usecases.quota.EnoughAccountQuotaInteractor
import com.linagora.android.linshare.domain.usecases.quota.ValidAccountQuota
import com.linagora.android.linshare.domain.usecases.utils.Failure
import com.linagora.android.linshare.domain.usecases.utils.NoOpOnFailure
import com.linagora.android.linshare.domain.usecases.utils.State
import com.linagora.android.linshare.domain.usecases.utils.Success
import com.linagora.android.linshare.domain.usecases.utils.ViewStateStore
import com.linagora.android.linshare.domain.utils.sendState
import kotlinx.coroutines.channels.ProducerScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filterNot
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject

class UploadInteractor @Inject constructor(
    private val getAuthenticatedInfo: GetAuthenticatedInfoInteractor,
    private val enoughAccountQuotaInteractor: EnoughAccountQuotaInteractor,
    private val documentRepository: DocumentRepository,
    private val interactorHandler: InteractorHandler,
    private val viewStateStore: ViewStateStore
) {

    operator fun invoke(documentRequest: DocumentRequest): Flow<State<Either<Failure, Success>>> {
        return getAuthenticatedInfo()
            .map { viewStateStore.storeAndGet(it) }
            .flatMapConcat { authenticateState -> consumeAuthenticationState(documentRequest, authenticateState) }
            .flatMapConcat { quotaState -> reactToQuotaState(quotaState, documentRequest) }
    }

    private fun consumeAuthenticationState(
        documentRequest: DocumentRequest,
        authenticationState: Either<Failure, Success>
    ) = flow<Either<Failure, Success>> {
        emit(authenticationState)
        authenticationState.fold(NoOpOnFailure) { success ->
            if (success is AuthenticationViewState) {
                checkAccountQuota(this, documentRequest)
            }
        }
    }

    private suspend fun checkAccountQuota(
        flowCollector: FlowCollector<Either<Failure, Success>>,
        documentRequest: DocumentRequest
    ) {
        enoughAccountQuotaInteractor.invoke(documentRequest)
            .onStart { delay(500) }
            .map { viewStateStore.storeAndGet(it) }
            .filterNot { invalidState(it) }
            .collect { quotaState -> flowCollector.emit(quotaState) }
    }

    private fun invalidState(eitherState: Either<Failure, Success>): Boolean {
        return eitherState.exists { success -> success is Success.Loading }
    }

    private suspend fun reactToQuotaState(
        quotaState: Either<Failure, Success>,
        documentRequest: DocumentRequest
    ): Flow<State<Either<Failure, Success>>> {
        return channelFlow<State<Either<Failure, Success>>> {
            sendState { quotaState }
            quotaState.fold(NoOpOnFailure) { success ->
                if (success is ValidAccountQuota) {
                    uploadToMySpace(this, documentRequest)
                }
            }
        }
    }

    private suspend fun uploadToMySpace(
        producerScope: ProducerScope<State<Either<Failure, Success>>>,
        documentRequest: DocumentRequest
    ) {
        interactorHandler.handle(
            execution = { performUpload(producerScope, documentRequest) },
            onCatch = { UploadErrorHandler(producerScope)(it) }
        )
    }

    private suspend fun performUpload(
        producerScope: ProducerScope<State<Either<Failure, Success>>>,
        documentRequest: DocumentRequest
    ) {
        val document = documentRepository.upload(
            documentRequest = documentRequest,
            onTransfer = { transferredBytes, totalBytes ->
                sendUploadingState(producerScope, transferredBytes, totalBytes)
            }
        )
        producerScope.sendState { Either.right(UploadSuccessViewState(document)) }
    }

    private fun sendUploadingState(
        producerScope: ProducerScope<State<Either<Failure, Success>>>,
        transferredBytes: TransferredBytes,
        totalBytes: TotalBytes
    ) {
        producerScope.sendState { Either.right(UploadingViewState(transferredBytes, totalBytes)) }
    }
}
