package com.linagora.android.linshare.domain.usecases.upload

import arrow.core.Either
import com.linagora.android.linshare.domain.model.BaseErrorCode
import com.linagora.android.linshare.domain.model.ClientErrorCode
import com.linagora.android.linshare.domain.model.LinShareErrorCode
import com.linagora.android.linshare.domain.model.document.DocumentRequest
import com.linagora.android.linshare.domain.model.upload.TotalBytes
import com.linagora.android.linshare.domain.model.upload.TransferredBytes
import com.linagora.android.linshare.domain.network.InternetNotAvailable
import com.linagora.android.linshare.domain.repository.document.DocumentRepository
import com.linagora.android.linshare.domain.usecases.auth.AuthenticationViewState
import com.linagora.android.linshare.domain.usecases.auth.GetAuthenticatedInfoInteractor
import com.linagora.android.linshare.domain.usecases.quota.EnoughAccountQuotaInteractor
import com.linagora.android.linshare.domain.usecases.quota.QuotaAccountNoMoreSpaceAvailable
import com.linagora.android.linshare.domain.usecases.quota.ValidAccountQuota
import com.linagora.android.linshare.domain.usecases.utils.Failure
import com.linagora.android.linshare.domain.usecases.utils.State
import com.linagora.android.linshare.domain.usecases.utils.Success
import com.linagora.android.linshare.domain.usecases.utils.Success.Idle
import com.linagora.android.linshare.domain.utils.BusinessErrorCode
import com.linagora.android.linshare.domain.utils.BusinessErrorCode.QuotaAccountNoMoreSpaceErrorCode
import kotlinx.coroutines.channels.ProducerScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filterNot
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import org.slf4j.LoggerFactory
import java.util.concurrent.atomic.AtomicReference
import javax.inject.Inject

class UploadInteractor @Inject constructor(
    private val getAuthenticatedInfo: GetAuthenticatedInfoInteractor,
    private val enoughAccountQuotaInteractor: EnoughAccountQuotaInteractor,
    private val documentRepository: DocumentRepository
) {

    companion object {
        private val LOGGER = LoggerFactory.getLogger(UploadInteractor::class.java)
    }

    private val currentState = AtomicReference<Either<Failure, Success>>(Either.right(Idle))

    operator fun invoke(documentRequest: DocumentRequest) = channelFlow {
        getAuthenticatedInfo()
            .map { dispatchState(it) }
            .collect { authenticationState ->
                send(State<Either<Failure, Success>> { authenticationState })
                consumeAuthenticationState(this, documentRequest, authenticationState)
            }
    }

    private fun consumeAuthenticationState(
        producerScope: ProducerScope<State<Either<Failure, Success>>>,
        documentRequest: DocumentRequest,
        authenticationState: Either<Failure, Success>
    ) {
        authenticationState.map { success ->
            if (success is AuthenticationViewState) {
                checkAccountQuota(producerScope, documentRequest)
            }
        }
    }

    private fun checkAccountQuota(
        producerScope: ProducerScope<State<Either<Failure, Success>>>,
        documentRequest: DocumentRequest
    ) = producerScope.launch {
        enoughAccountQuotaInteractor.invoke(documentRequest)
            .onStart { delay(500) }
            .map { dispatchState(it) }
            .filterNot { invalidState(it) }
            .collect { reactQuotaState(producerScope, documentRequest, it) }
    }

    private fun invalidState(eitherState: Either<Failure, Success>): Boolean {
        return eitherState.exists { success -> success is Success.Loading }
    }

    private fun dispatchState(state: State<Either<Failure, Success>>): Either<Failure, Success> {
        val newState = state(currentState.get())
        currentState.set(newState)
        return newState
    }

    private suspend fun reactQuotaState(
        producerScope: ProducerScope<State<Either<Failure, Success>>>,
        documentRequest: DocumentRequest,
        eitherState: Either<Failure, Success>
    ) {
        eitherState.fold(
            ifLeft = { producerScope.send(State { eitherState }) },
            ifRight = { success -> doWithSuccessState(producerScope, documentRequest, success) }
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

    private suspend fun upload(producerScope: ProducerScope<State<Either<Failure, Success>>>, documentRequest: DocumentRequest) {
        try {
            val document = documentRepository.upload(documentRequest) { transferredBytes, totalBytes ->
                sendUploadingState(producerScope, transferredBytes, totalBytes)
            }
            producerScope.send(State { Either.right(UploadSuccessViewState(document)) })
        } catch (uploadException: UploadException) {
            LOGGER.error("upload(): $uploadException")
            when (val uploadErrorCode = uploadException.errorResponse.errCode) {
                is ClientErrorCode -> { handleStateClientErrorCode(producerScope, uploadErrorCode) }
                is LinShareErrorCode -> { handleStateLinShareErrorCode(producerScope, uploadErrorCode) }
                else -> producerScope.send(State { Either.left(Failure.Error) })
            }
        }
    }

    private suspend fun handleStateClientErrorCode(producerScope: ProducerScope<State<Either<Failure, Success>>>, uploadErrorCode: BaseErrorCode) {
        when (uploadErrorCode) {
            BusinessErrorCode.InternetNotAvailableErrorCode -> producerScope.send(State { Either.left(InternetNotAvailable) })
        }
    }

    private suspend fun handleStateLinShareErrorCode(producerScope: ProducerScope<State<Either<Failure, Success>>>, uploadErrorCode: BaseErrorCode) {
        when (uploadErrorCode) {
            QuotaAccountNoMoreSpaceErrorCode -> producerScope.send(State { Either.left(QuotaAccountNoMoreSpaceAvailable) })
        }
    }

    private fun sendUploadingState(
        producerScope: ProducerScope<State<Either<Failure, Success>>>,
        transferredBytes: TransferredBytes,
        totalBytes: TotalBytes
    ) {
        producerScope.launch { producerScope.send(State {
            Either.right(UploadingViewState(transferredBytes, totalBytes))
        }) }
    }
}
