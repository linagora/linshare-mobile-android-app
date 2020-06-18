package com.linagora.android.linshare.domain.usecases.sharedspace

import arrow.core.Either
import com.linagora.android.linshare.domain.model.sharedspace.CreateWorkGroupRequest
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
class CreateWorkGroupInteractor @Inject constructor(
    private val sharedSpaceRepository: SharedSpaceRepository
) {

    operator fun invoke(createWorkGroupRequest: CreateWorkGroupRequest): Flow<State<Either<Failure, Success>>> {
        return flow<State<Either<Failure, Success>>> {
            emitState { Either.right(Loading) }

            val state = Either.catch { sharedSpaceRepository.createWorkGroup(createWorkGroupRequest) }
                    .bimap(::CreateWorkGroupFailed, ::CreateWorkGroupSuccess)
            emitState { state }
        }
    }
}
