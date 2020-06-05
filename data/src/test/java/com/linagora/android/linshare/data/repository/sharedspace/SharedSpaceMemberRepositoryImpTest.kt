package com.linagora.android.linshare.data.repository.sharedspace

import com.google.common.truth.Truth.assertThat
import com.linagora.android.linshare.data.datasource.sharedspace.member.SharedSpaceMemberDataSource
import com.linagora.android.linshare.domain.model.sharedspace.member.SharedSpaceMember
import com.linagora.android.testshared.SharedSpaceDocumentFixtures.BAR_FOO_MEMBER
import com.linagora.android.testshared.SharedSpaceDocumentFixtures.JOHN_DOE_MEMBER
import com.linagora.android.testshared.SharedSpaceFixtures.SHARED_SPACE_ID_1
import kotlinx.coroutines.test.runBlockingTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations

class SharedSpaceMemberRepositoryImpTest {

    @Mock
    lateinit var linSharedSpaceMemberDataSource: SharedSpaceMemberDataSource

    private lateinit var sharedSpaceMemberRepositoryImp: SharedSpaceMemberRepositoryImp

    @BeforeEach
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        sharedSpaceMemberRepositoryImp = SharedSpaceMemberRepositoryImp(linSharedSpaceMemberDataSource)
    }

    @Test
    fun getAllMembersShouldReturnAllMemberInSharedSpace() = runBlockingTest {
        `when`(linSharedSpaceMemberDataSource.getAllMembers(SHARED_SPACE_ID_1))
            .thenAnswer { listOf(JOHN_DOE_MEMBER, BAR_FOO_MEMBER) }

        val members = sharedSpaceMemberRepositoryImp.getAllMembers(SHARED_SPACE_ID_1)

        assertThat(members).containsExactly(JOHN_DOE_MEMBER, BAR_FOO_MEMBER)
    }

    @Test
    fun getAllMembersShouldNoMemberInSharedSpace() = runBlockingTest {
        `when`(linSharedSpaceMemberDataSource.getAllMembers(SHARED_SPACE_ID_1))
            .thenAnswer { emptyList<SharedSpaceMember>() }

        val members = sharedSpaceMemberRepositoryImp.getAllMembers(SHARED_SPACE_ID_1)

        assertThat(members).isEmpty()
    }

    @Test
    fun getAllMembersShouldThrowWhenGetMembersFailed() = runBlockingTest {
        val exception = RuntimeException("get member failed")
        `when`(linSharedSpaceMemberDataSource.getAllMembers(SHARED_SPACE_ID_1))
            .thenThrow(exception)

        assertThrows<RuntimeException> { runBlockingTest {
            sharedSpaceMemberRepositoryImp.getAllMembers(SHARED_SPACE_ID_1) } }
    }
}
