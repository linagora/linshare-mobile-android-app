package com.linagora.android.linshare.domain.usecases.sharedspace

import arrow.core.Either
import com.google.common.truth.Truth.assertThat
import com.linagora.android.linshare.domain.model.sharedspace.MembersParameter
import com.linagora.android.linshare.domain.model.sharedspace.RolesParameter
import com.linagora.android.linshare.domain.repository.sharedspace.SharedSpaceRepository
import com.linagora.android.testshared.SharedSpaceDocumentFixtures.SHARED_SPACE_1
import com.linagora.android.testshared.SharedSpaceDocumentFixtures.SHARED_SPACE_ID_1
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

class GetSingleSharedSpaceInteractorTest {

    @Mock
    lateinit var sharedSpaceRepository: SharedSpaceRepository

    private lateinit var getSingleSharedSpaceInteractor: GetSingleSharedSpaceInteractor

    @BeforeEach
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        getSingleSharedSpaceInteractor = GetSingleSharedSpaceInteractor(sharedSpaceRepository)
    }

    @Test
    fun getSingleSharedSpaceShouldSuccessWhenExistASharedSpace() = runBlockingTest {
        `when`(sharedSpaceRepository.getSharedSpace(SHARED_SPACE_ID_1, MembersParameter.WithoutMembers, RolesParameter.WithRole))
            .thenAnswer { SHARED_SPACE_1 }

        assertThat(getSingleSharedSpaceInteractor(SHARED_SPACE_ID_1, MembersParameter.WithoutMembers, RolesParameter.WithRole)
            .map { it(INIT_STATE) }
            .toList(ArrayList())
        ).containsExactly(LOADING_STATE, Either.right(GetSharedSpaceSuccess(SHARED_SPACE_1)))
    }

    @Test
    fun getSingleSharedSpaceShouldFailure() = runBlockingTest {
        val exception = RuntimeException("can not get shared space")
        `when`(sharedSpaceRepository.getSharedSpace(SHARED_SPACE_ID_1, MembersParameter.WithoutMembers, RolesParameter.WithRole))
            .thenThrow(exception)

        assertThat(getSingleSharedSpaceInteractor(SHARED_SPACE_ID_1, MembersParameter.WithoutMembers, RolesParameter.WithRole)
            .map { it(INIT_STATE) }
            .toList(ArrayList())
        ).containsExactly(LOADING_STATE, Either.left(GetSharedSpaceFailed(exception)))
    }
}
