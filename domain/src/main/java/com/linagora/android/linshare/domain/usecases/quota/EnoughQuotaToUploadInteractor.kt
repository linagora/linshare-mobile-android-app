package com.linagora.android.linshare.domain.usecases.quota

import arrow.core.Either
import com.linagora.android.linshare.domain.model.AccountQuota
import com.linagora.android.linshare.domain.model.document.DocumentRequest
import com.linagora.android.linshare.domain.model.enoughQuotaToUpload
import com.linagora.android.linshare.domain.model.quota.QuotaId
import com.linagora.android.linshare.domain.model.validMaxFileSizeToUpload
import com.linagora.android.linshare.domain.repository.user.QuotaRepository
import com.linagora.android.linshare.domain.usecases.utils.Failure
import com.linagora.android.linshare.domain.usecases.utils.State
import com.linagora.android.linshare.domain.usecases.utils.Success
import kotlinx.coroutines.channels.ProducerScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EnoughQuotaToUploadInteractor @Inject constructor(
    private val quotaRepository: QuotaRepository
) {
    operator fun invoke(
        quotaId: QuotaId,
        documentRequest: DocumentRequest
    ): Flow<State<Either<Failure, Success>>> {
        return channelFlow<State<Either<Failure, Success>>> {
            send(State { Either.right(Success.Loading) })

            quotaRepository.findQuota(quotaId)
                ?.let { quota -> enough(this, quota, documentRequest) }
                ?: send(State { Either.left(QuotaAccountNoMoreSpaceAvailable) })
        }
    }

    private suspend fun enough(
        producerScope: ProducerScope<State<Either<Failure, Success>>>,
        quota: AccountQuota,
        documentRequest: DocumentRequest
    ) {
        if (!quota.validMaxFileSizeToUpload(documentRequest)) {
            producerScope.send(State { Either.left(ExceedMaxFileSize) })
            return
        }

        if (!quota.enoughQuotaToUpload(documentRequest)) {
            producerScope.send(State { Either.left(QuotaAccountNoMoreSpaceAvailable) })
            return
        }

        producerScope.send(State { Either.right(ValidAccountQuota) })
    }
}
