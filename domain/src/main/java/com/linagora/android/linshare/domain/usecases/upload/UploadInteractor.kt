package com.linagora.android.linshare.domain.usecases.upload

import arrow.core.Either
import arrow.core.Either.Companion
import com.linagora.android.linshare.domain.model.document.DocumentRequest
import com.linagora.android.linshare.domain.repository.document.DocumentRepository
import com.linagora.android.linshare.domain.repository.user.QuotaRepository
import com.linagora.android.linshare.domain.repository.user.UserRepository
import com.linagora.android.linshare.domain.usecases.utils.Failure
import com.linagora.android.linshare.domain.usecases.utils.Failure.QuotaAccountNoMoreSpaceAvailable
import com.linagora.android.linshare.domain.usecases.utils.State
import com.linagora.android.linshare.domain.usecases.utils.Success
import com.linagora.android.linshare.domain.usecases.utils.Success.Loading
import com.linagora.android.linshare.domain.utils.BusinessErrorCode.QuotaAccountNoMoreSpaceErrorCode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

class UploadInteractor @Inject constructor(
    private val userRepository: UserRepository,
    private val quotaRepository: QuotaRepository,
    private val documentRepository: DocumentRepository
) {

    operator fun invoke(documentRequest: DocumentRequest): Flow<State<Either<Failure, Success>>> {
        return channelFlow <State<Either<Failure, Success>>> {
            send(State { Either.right(Loading) })
            userRepository.getAuthorizedUser()
                ?.let { user ->
                    try {
                        check(enoughQuota(user.quotaUuid, documentRequest))
                        val document = documentRepository.upload(documentRequest) { transferredBytes, totalBytes ->
                            this.launch {
                                send(State { Either.right(UploadingViewState(transferredBytes, totalBytes)) })
                            }
                        }
                        send(State { Companion.right(UploadSuccessViewState(document)) })
                    } catch (illegalStateException: java.lang.IllegalStateException) {
                        send(State { Either.left(QuotaAccountNoMoreSpaceAvailable) })
                    } catch (uploadException: UploadException) {
                        when (uploadException.errorResponse.linShareErrorCode) {
                            QuotaAccountNoMoreSpaceErrorCode -> send(State { Either.left(QuotaAccountNoMoreSpaceAvailable) })
                            else -> send(State { Either.left(Failure.Error) })
                        }
                    }
                }
        }
    }

    private suspend fun enoughQuota(quotaUUID: UUID, document: DocumentRequest): Boolean {
        return quotaRepository.findQuota(quotaUUID.toString())
            ?.let { quota ->
                return document.fileSize < quota.maxFileSize.size &&
                    document.fileSize < (quota.quota - quota.usedSpace)
            }
            ?: false
    }
}
