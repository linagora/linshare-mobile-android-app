package com.linagora.android.linshare.domain.usecases.quota

import arrow.core.Either
import com.linagora.android.linshare.domain.model.AccountQuota
import com.linagora.android.linshare.domain.model.User
import com.linagora.android.linshare.domain.model.document.DocumentRequest
import com.linagora.android.linshare.domain.model.enoughQuotaToUpload
import com.linagora.android.linshare.domain.model.validMaxFileSizeToUpload
import com.linagora.android.linshare.domain.repository.user.QuotaRepository
import com.linagora.android.linshare.domain.repository.user.UserRepository
import com.linagora.android.linshare.domain.usecases.utils.Failure
import com.linagora.android.linshare.domain.usecases.utils.State
import com.linagora.android.linshare.domain.usecases.utils.Success
import com.linagora.android.linshare.domain.usecases.utils.Success.Loading
import kotlinx.coroutines.channels.ProducerScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import javax.inject.Inject

class EnoughAccountQuotaInteractor @Inject constructor(
    private val userRepository: UserRepository,
    private val quotaRepository: QuotaRepository
) {

    operator fun invoke(document: DocumentRequest): Flow<State<Either<Failure, Success>>> {
        return channelFlow<State<Either<Failure, Success>>> {
            send(State { Either.right(Loading) })
            userRepository.getAuthorizedUser()
                ?.let { user -> enoughQuota(this, user, document) }
                ?: send(State { Either.left(QuotaAccountNoMoreSpaceAvailable) })
        }
    }

    private suspend fun enoughQuota(
        producerScope: ProducerScope<State<Either<Failure, Success>>>,
        user: User,
        document: DocumentRequest
    ) {
        quotaRepository.findQuota(user.quotaUuid)
            ?.let { quota -> validateQuota(producerScope, document, quota) }
            ?: producerScope.send(State { Either.left(QuotaAccountNoMoreSpaceAvailable) })
    }

    private suspend fun validateQuota(
        producerScope: ProducerScope<State<Either<Failure, Success>>>,
        document: DocumentRequest,
        quota: AccountQuota
    ) {
        if (!quota.validMaxFileSizeToUpload(document)) {
            producerScope.send(State { Either.left(ExceedMaxFileSize) })
            return
        }

        if (!quota.enoughQuotaToUpload(document)) {
            producerScope.send(State { Either.left(QuotaAccountNoMoreSpaceAvailable) })
            return
        }

        producerScope.send(State { Either.right(ValidAccountQuota) })
    }
}
