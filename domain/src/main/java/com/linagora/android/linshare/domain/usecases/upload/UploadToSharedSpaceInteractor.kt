package com.linagora.android.linshare.domain.usecases.upload

import arrow.core.Either
import com.linagora.android.linshare.domain.model.document.DocumentRequest
import com.linagora.android.linshare.domain.model.quota.QuotaId
import com.linagora.android.linshare.domain.model.sharedspace.SharedSpaceId
import com.linagora.android.linshare.domain.model.sharedspace.WorkGroupNodeId
import com.linagora.android.linshare.domain.model.upload.TotalBytes
import com.linagora.android.linshare.domain.model.upload.TransferredBytes
import com.linagora.android.linshare.domain.repository.sharedspacesdocument.SharedSpacesDocumentRepository
import com.linagora.android.linshare.domain.usecases.InteractorHandler
import com.linagora.android.linshare.domain.usecases.auth.AuthenticationViewState
import com.linagora.android.linshare.domain.usecases.auth.GetAuthenticatedInfoInteractor
import com.linagora.android.linshare.domain.usecases.quota.EnoughQuotaToUploadInteractor
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
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import org.slf4j.LoggerFactory
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UploadToSharedSpaceInteractor @Inject constructor(
    private val getAuthenticatedInfo: GetAuthenticatedInfoInteractor,
    private val enoughQuotaToUpload: EnoughQuotaToUploadInteractor,
    private val sharedSpacesDocumentRepository: SharedSpacesDocumentRepository,
    private val interactorHandler: InteractorHandler,
    private val viewStateStore: ViewStateStore
) {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(UploadToSharedSpaceInteractor::class.java)
    }

    operator fun invoke(
        sharedSpaceId: SharedSpaceId,
        quotaId: QuotaId,
        parentNodeId: WorkGroupNodeId? = null,
        documentRequest: DocumentRequest
    ): Flow<State<Either<Failure, Success>>> {
        return getAuthenticatedInfo()
            .map { viewStateStore.storeAndGet(it) }
            .flatMapConcat { authenticateState -> reactToAuthenticatedState(authenticateState, quotaId, documentRequest) }
            .flatMapConcat { quotaState -> reactToQuotaState(quotaState, sharedSpaceId, parentNodeId, documentRequest) }
    }

    private fun reactToAuthenticatedState(
        authenticatedState: Either<Failure, Success>,
        quotaId: QuotaId,
        documentRequest: DocumentRequest
    ) = flow<Either<Failure, Success>> {
        emit(authenticatedState)
        validateQuota(this, authenticatedState, quotaId, documentRequest)
    }

    private suspend fun validateQuota(
        flowCollector: FlowCollector<Either<Failure, Success>>,
        authenticatedState: Either<Failure, Success>,
        quotaId: QuotaId,
        documentRequest: DocumentRequest
    ) {
        authenticatedState.fold(NoOpOnFailure) { success ->
            checkQuotaOnSuccessAuthenticated(flowCollector, success, quotaId, documentRequest)
        }
    }

    private suspend fun checkQuotaOnSuccessAuthenticated(
        flowCollector: FlowCollector<Either<Failure, Success>>,
        success: Success,
        quotaId: QuotaId,
        documentRequest: DocumentRequest
    ) {
        if (success is AuthenticationViewState) {
            enoughQuotaToUpload(quotaId, documentRequest)
                .onStart { delay(500) }
                .map { viewStateStore.storeAndGet(it) }
                .collect { quotaState -> flowCollector.emit(quotaState) }
        }
    }

    private suspend fun reactToQuotaState(
        quotaState: Either<Failure, Success>,
        sharedSpaceId: SharedSpaceId,
        parentNodeId: WorkGroupNodeId? = null,
        documentRequest: DocumentRequest
    ): Flow<State<Either<Failure, Success>>> {
        return channelFlow<State<Either<Failure, Success>>> {
            sendState { quotaState }
            quotaState.fold(NoOpOnFailure) { success ->
                if (success is ValidAccountQuota) {
                    uploadToSharedSpace(this, sharedSpaceId, parentNodeId, documentRequest)
                }
            }
        }
    }

    private suspend fun uploadToSharedSpace(
        producerScope: ProducerScope<State<Either<Failure, Success>>>,
        sharedSpaceId: SharedSpaceId,
        parentNodeId: WorkGroupNodeId? = null,
        documentRequest: DocumentRequest
    ) {
        interactorHandler.handle(
            execution = { performUpload(producerScope, sharedSpaceId, parentNodeId, documentRequest) },
            onCatch = { UploadErrorHandler(producerScope)(it) }
        )
    }

    private suspend fun performUpload(
        producerScope: ProducerScope<State<Either<Failure, Success>>>,
        sharedSpaceId: SharedSpaceId,
        parentNodeId: WorkGroupNodeId? = null,
        documentRequest: DocumentRequest
    ) {
        val workgroupDocument = sharedSpacesDocumentRepository.uploadSharedSpaceDocument(
            documentRequest = documentRequest,
            sharedSpaceId = sharedSpaceId,
            parentNodeId = parentNodeId,
            onTransfer = { transferredBytes, totalBytes ->
                sendUploadingState(producerScope, transferredBytes, totalBytes)
            }
        )

        producerScope.send(State { Either.right(UploadToSharedSpaceSuccess(workgroupDocument)) })
    }

    private fun sendUploadingState(
        producerScope: ProducerScope<State<Either<Failure, Success>>>,
        transferredBytes: TransferredBytes,
        totalBytes: TotalBytes
    ) {
        producerScope.sendState { Either.right(UploadingViewState(transferredBytes, totalBytes)) }
    }
}
