package com.linagora.android.linshare.domain.usecases.sharedspace.member

import arrow.core.Either
import com.linagora.android.linshare.domain.model.sharedspace.SharedSpaceId
import com.linagora.android.linshare.domain.model.sharedspace.member.SharedSpaceMember
import com.linagora.android.linshare.domain.repository.sharedspace.SharedSpaceMemberRepository
import com.linagora.android.linshare.domain.usecases.utils.Failure
import com.linagora.android.linshare.domain.usecases.utils.State
import com.linagora.android.linshare.domain.usecases.utils.Success
import com.linagora.android.linshare.domain.utils.emitState
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetAllMembersInSharedSpaceInteractor @Inject constructor(
    private val sharedSpaceMemberRepository: SharedSpaceMemberRepository
) {

    operator fun invoke(sharedSpaceId: SharedSpaceId) = flow<State<Either<Failure, Success>>> {
        emitState { Either.right(Success.Loading) }

        val getMemberState = Either.catch { sharedSpaceMemberRepository.getAllMembers(sharedSpaceId) }
            .fold(
                ifLeft = { Either.left(GetMembersFailed(it)) },
                ifRight = this@GetAllMembersInSharedSpaceInteractor::generateGetMembersState
            )

        emitState { getMemberState }
    }

    private fun generateGetMembersState(members: List<SharedSpaceMember>): Either<Failure, Success> {
        return members.takeIf { it.isNotEmpty() }
            ?.let { Either.right(GetMembersSuccess(it)) }
            ?: Either.left(GetMembersNoResult)
    }
}
