package com.linagora.android.linshare.domain.usecases.sharedspace

import arrow.core.Either
import com.linagora.android.linshare.domain.model.sharedspace.MembersParameter
import com.linagora.android.linshare.domain.model.sharedspace.RolesParameter
import com.linagora.android.linshare.domain.model.sharedspace.SharedSpaceId
import com.linagora.android.linshare.domain.repository.sharedspace.SharedSpaceRepository
import com.linagora.android.linshare.domain.usecases.utils.Failure
import com.linagora.android.linshare.domain.usecases.utils.State
import com.linagora.android.linshare.domain.usecases.utils.Success
import com.linagora.android.linshare.domain.utils.emitState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetSingleSharedSpaceInteractor @Inject constructor(
    private val sharedSpaceRepository: SharedSpaceRepository
) {

    operator fun invoke(
        sharedSpaceId: SharedSpaceId,
        membersParameter: MembersParameter = MembersParameter.WithoutMembers,
        rolesParameter: RolesParameter = RolesParameter.WithRole
    ): Flow<State<Either<Failure, Success>>> {
        return flow<State<Either<Failure, Success>>> {
            emitState { Either.right(Success.Loading) }

            val viewState = Either
                .catch { sharedSpaceRepository.getSharedSpace(sharedSpaceId, membersParameter, rolesParameter) }
                .bimap(::GetSharedSpaceFailed, ::GetSharedSpaceSuccess)

            emitState { viewState }
        }
    }
}
