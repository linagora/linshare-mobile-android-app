package com.linagora.android.linshare.domain.usecases.sharedspace.member

import arrow.core.Either
import com.linagora.android.linshare.domain.model.sharedspace.member.AddMemberRequest
import com.linagora.android.linshare.domain.repository.sharedspace.SharedSpaceMemberRepository
import com.linagora.android.linshare.domain.usecases.utils.Failure
import com.linagora.android.linshare.domain.usecases.utils.State
import com.linagora.android.linshare.domain.usecases.utils.Success
import com.linagora.android.linshare.domain.utils.emitState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AddMember @Inject constructor(
    private val sharedSpaceMemberRepository: SharedSpaceMemberRepository
) {
    operator fun invoke(addMemberRequest: AddMemberRequest): Flow<State<Either<Failure, Success>>> {
        return flow<State<Either<Failure, Success>>> {
            emitState { Either.right(Success.Loading) }

            val addMemberState = Either.catch { sharedSpaceMemberRepository.addMember(addMemberRequest) }
                .bimap(::AddMemberFailed, ::AddMemberSuccess)

            emitState { addMemberState }
        }
    }
}
