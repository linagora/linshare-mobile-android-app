package com.linagora.android.linshare.domain.usecases.sharedspace

import arrow.core.Either
import com.linagora.android.linshare.domain.model.sharedspace.SharedSpaceNodeNested
import com.linagora.android.linshare.domain.repository.sharedspace.SharedSpaceRepository
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
                .fold({ Either.left(SharedSpaceFailure(it)) }, ::generateSharedSpaceState)

            emitState { state }
        }
    }

    private fun generateSharedSpaceState(sharedSpace: List<SharedSpaceNodeNested>): Either<Failure, Success> {
        return sharedSpace.takeIf { it.isNotEmpty() }
            ?.let { Either.right(SharedSpaceViewState(it)) }
            ?: let { Either.left(EmptySharedSpaceState) }
    }
}
