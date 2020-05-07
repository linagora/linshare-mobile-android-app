package com.linagora.android.linshare.domain.usecases.sharedspace

import arrow.core.Either
import com.google.common.truth.Truth.assertThat
import com.linagora.android.linshare.domain.model.sharedspace.SharedSpaceNodeNested
import com.linagora.android.linshare.domain.repository.sharedspace.SharedSpaceRepository
import com.linagora.android.testshared.SharedSpaceFixtures.ALL_SHARED_SPACE_LIST_VIEW_STATE
import com.linagora.android.testshared.SharedSpaceFixtures.EMPTY_SHARED_SPACE_LIST_VIEW_STATE
import com.linagora.android.testshared.SharedSpaceFixtures.SHARED_SPACE_1
import com.linagora.android.testshared.SharedSpaceFixtures.SHARED_SPACE_2
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

class GetSharedSpaceInteractorTest {

    @Mock
    private lateinit var sharedSpaceRepository: SharedSpaceRepository

    private lateinit var getSharedSpaceInteractor: GetSharedSpaceInteractor

    @BeforeEach
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        getSharedSpaceInteractor = GetSharedSpaceInteractor(sharedSpaceRepository)
    }

    @Test
    fun getSharedSpaceListShouldSuccessWithSharedSpaceList() {
        runBlockingTest {
            `when`(sharedSpaceRepository.getSharedSpaces())
                .thenAnswer { listOf(SHARED_SPACE_1, SHARED_SPACE_2) }

            assertThat(getSharedSpaceInteractor()
                .map { it(INIT_STATE) }
                .toList(ArrayList()))
                .containsExactly(LOADING_STATE, ALL_SHARED_SPACE_LIST_VIEW_STATE)
        }
    }

    @Test
    fun getSharedSpaceListShouldSuccessWithEmptySharedSpaceList() {
        runBlockingTest {
            `when`(sharedSpaceRepository.getSharedSpaces())
                .thenAnswer { emptyList<SharedSpaceNodeNested>() }

            assertThat(getSharedSpaceInteractor()
                .map { it(INIT_STATE) }
                .toList(ArrayList()))
                .containsExactly(LOADING_STATE, EMPTY_SHARED_SPACE_LIST_VIEW_STATE)
        }
    }

    @Test
    fun getSharedSpaceListShouldFailedWhenGetSharedSpaceListFailed() {
        runBlockingTest {
            val exception = RuntimeException("get shared space failed")

            `when`(sharedSpaceRepository.getSharedSpaces())
                .thenThrow(exception)

            assertThat(getSharedSpaceInteractor()
                .map { it(INIT_STATE) }
                .toList(ArrayList()))
                .containsExactly(LOADING_STATE, Either.Left(SharedSpaceFailure(exception)))
        }
    }
}
