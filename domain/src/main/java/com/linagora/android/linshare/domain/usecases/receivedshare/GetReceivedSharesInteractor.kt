package com.linagora.android.linshare.domain.usecases.receivedshare

import arrow.core.Either
import com.linagora.android.linshare.domain.repository.share.ReceivedShareRepository
import com.linagora.android.linshare.domain.usecases.utils.Failure
import com.linagora.android.linshare.domain.usecases.utils.State
import com.linagora.android.linshare.domain.usecases.utils.Success
import com.linagora.android.linshare.domain.usecases.utils.Success.Loading
import com.linagora.android.linshare.domain.utils.emitState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetReceivedSharesInteractor @Inject constructor(
    private val receivedShareRepository: ReceivedShareRepository
) {

    operator fun invoke(): Flow<State<Either<Failure, Success>>> {
        return flow<State<Either<Failure, Success>>> {
            emitState { Either.right(Loading) }

            val state = Either.catch { receivedShareRepository.getReceivedShares() }
                .bimap(::ReceivedSharesFailure, ::ReceivedSharesViewState)

            emitState { state }
        }
    }
}
