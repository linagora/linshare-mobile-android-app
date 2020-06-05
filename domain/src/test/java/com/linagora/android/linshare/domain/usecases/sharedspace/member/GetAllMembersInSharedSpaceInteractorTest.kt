package com.linagora.android.linshare.domain.usecases.sharedspace.member

import arrow.core.Either
import com.google.common.truth.Truth.assertThat
import com.linagora.android.linshare.domain.model.sharedspace.member.SharedSpaceMember
import com.linagora.android.linshare.domain.repository.sharedspace.SharedSpaceMemberRepository
import com.linagora.android.testshared.SharedSpaceDocumentFixtures.BAR_FOO_MEMBER
import com.linagora.android.testshared.SharedSpaceDocumentFixtures.GET_MEMBERS_SUCCESS_STATE
import com.linagora.android.testshared.SharedSpaceDocumentFixtures.JOHN_DOE_MEMBER
import com.linagora.android.testshared.SharedSpaceFixtures.SHARED_SPACE_ID_1
import com.linagora.android.testshared.TestFixtures.State.INIT_STATE
import com.linagora.android.testshared.TestFixtures.State.LOADING_STATE
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runBlockingTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations

class GetAllMembersInSharedSpaceInteractorTest {
    @Mock
    lateinit var sharedSpaceMemberRepository: SharedSpaceMemberRepository

    private lateinit var getAllMembersInSharedSpaceInteractor: GetAllMembersInSharedSpaceInteractor

    @BeforeEach
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        getAllMembersInSharedSpaceInteractor =
            GetAllMembersInSharedSpaceInteractor(sharedSpaceMemberRepository)
    }

    @Test
    fun getAllMembersShouldReturnSuccessState() = runBlockingTest {
        `when`(sharedSpaceMemberRepository.getAllMembers(SHARED_SPACE_ID_1))
            .thenAnswer { listOf(JOHN_DOE_MEMBER, BAR_FOO_MEMBER) }

        val states = getAllMembersInSharedSpaceInteractor(SHARED_SPACE_ID_1)
            .map { it(INIT_STATE) }
            .toList(ArrayList())

        assertThat(states).containsExactly(LOADING_STATE, GET_MEMBERS_SUCCESS_STATE)
    }

    @Test
    fun getAllMemberShouldReturnNoResultStateWhenNoMemberInSharedSpace() = runBlockingTest {
        `when`(sharedSpaceMemberRepository.getAllMembers(SHARED_SPACE_ID_1))
            .thenAnswer { emptyList<SharedSpaceMember>() }

        val states = getAllMembersInSharedSpaceInteractor(SHARED_SPACE_ID_1)
            .map { it(INIT_STATE) }
            .toList(ArrayList())

        assertThat(states).containsExactly(LOADING_STATE, Either.left(GetMembersNoResult))
    }

    @Test
    fun getAllMemberShouldReturnFailureStateWhenGetMembersFailed() = runBlockingTest {
        val exception = RuntimeException()
        `when`(sharedSpaceMemberRepository.getAllMembers(SHARED_SPACE_ID_1))
            .thenThrow(exception)

        val states = getAllMembersInSharedSpaceInteractor(SHARED_SPACE_ID_1)
            .map { it(INIT_STATE) }
            .toList(ArrayList())

        assertThat(states).containsExactly(LOADING_STATE, Either.left(GetMembersFailed(exception)))
    }
}
