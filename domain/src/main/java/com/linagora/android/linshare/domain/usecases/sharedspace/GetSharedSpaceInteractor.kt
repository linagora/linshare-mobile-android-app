package com.linagora.android.linshare.domain.usecases.sharedspace

import arrow.core.Either
import com.linagora.android.linshare.domain.model.sharespace.ShareSpaceNodeNested
import com.linagora.android.linshare.domain.repository.sharespace.SharedSpaceRepository
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
class GetSharedSpaceInteractor @Inject constructor(
    private val sharedSpaceRepository: SharedSpaceRepository
) {

    operator fun invoke(): Flow<State<Either<Failure, Success>>> {
        return flow<State<Either<Failure, Success>>> {
            emitState { Either.right(Loading) }

            val state = Either.catch { sharedSpaceRepository.getSharedSpaces() }
                .bimap(::SharedSpaceFailure, ::generateSharedSpaceState)

            emitState { state }
        }
    }

    private fun generateSharedSpaceState(sharedSpace: List<ShareSpaceNodeNested>): Success {
        return sharedSpace.takeIf { it.isNotEmpty() }
            ?.let { SharedSpaceViewState(it) }
            ?: let { EmptySharedSpaceState }
    }
}
