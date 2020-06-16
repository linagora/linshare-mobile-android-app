package com.linagora.android.linshare.domain.usecases.sharedspace.member

import arrow.core.Either
import com.google.common.truth.Truth.assertThat
import com.linagora.android.linshare.domain.repository.sharedspace.SharedSpaceMemberRepository
import com.linagora.android.testshared.SharedSpaceDocumentFixtures.ADD_BAR_FOO_MEMBER_REQUEST
import com.linagora.android.testshared.SharedSpaceDocumentFixtures.BAR_FOO_MEMBER
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

class AddMemberTest {
    @Mock
    lateinit var sharedSpaceMemberRepository: SharedSpaceMemberRepository

    private lateinit var addMember: AddMember

    @BeforeEach
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        addMember = AddMember(sharedSpaceMemberRepository)
    }

    @Test
    fun addMemberShouldAddMemberToSharedSpaceWhenAddMemberRequestValid() = runBlockingTest {
        `when`(sharedSpaceMemberRepository.addMember(ADD_BAR_FOO_MEMBER_REQUEST))
            .thenAnswer { BAR_FOO_MEMBER }

        val addMemberStates = addMember(ADD_BAR_FOO_MEMBER_REQUEST)
            .map { it(INIT_STATE) }
            .toList(ArrayList())

        assertThat(addMemberStates).hasSize(2)
        assertThat(addMemberStates)
            .containsExactly(LOADING_STATE, Either.right(AddMemberSuccess(BAR_FOO_MEMBER)))
    }

    @Test
    fun addMemberShouldGetFailedStateWhenAddMemberFailed() = runBlockingTest {
        val exception = RuntimeException("add member failed")
        `when`(sharedSpaceMemberRepository.addMember(ADD_BAR_FOO_MEMBER_REQUEST))
            .thenThrow(exception)

        val addMemberStates = addMember(ADD_BAR_FOO_MEMBER_REQUEST)
            .map { it(INIT_STATE) }
            .toList(ArrayList())

        assertThat(addMemberStates).hasSize(2)
        assertThat(addMemberStates)
            .containsExactly(LOADING_STATE, Either.left(AddMemberFailed(exception)))
    }
}
