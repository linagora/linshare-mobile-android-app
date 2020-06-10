package com.linagora.android.linshare.domain.usecases.sharedspace.role

import arrow.core.Either
import com.linagora.android.linshare.domain.repository.sharedspace.sharedspaceroles.SharedSpaceRoleRepository
import com.linagora.android.linshare.domain.usecases.utils.Failure
import com.linagora.android.linshare.domain.usecases.utils.State
import com.linagora.android.linshare.domain.usecases.utils.Success
import com.linagora.android.linshare.domain.utils.emitState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetAllRoles @Inject constructor(
    private val sharedSpaceRoleRepository: SharedSpaceRoleRepository
) {
    operator fun invoke(): Flow<State<Either<Failure, Success>>> {
        return flow<State<Either<Failure, Success>>> {
            emitState { Either.right(Success.Loading) }

            val getRolesState = Either.catch { sharedSpaceRoleRepository.findAll() }
                .bimap(::GetAllSharedSpaceRolesFailed, ::GetAllSharedSpaceRolesSuccess)

            emitState { getRolesState }
        }
    }
}
