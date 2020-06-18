package com.linagora.android.linshare.domain.usecases.sharedspace

import arrow.core.Either
import com.google.common.truth.Truth.assertThat
import com.linagora.android.linshare.domain.repository.sharedspace.SharedSpaceRepository
import com.linagora.android.testshared.SharedSpaceDocumentFixtures
import com.linagora.android.testshared.SharedSpaceFixtures.CREATE_WORK_GROUP_REQUEST
import com.linagora.android.testshared.SharedSpaceFixtures.CREATE_WORK_GROUP_SUCCESS_STATE
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

class CreateWorkGroupInteractorTest {

    @Mock
    private lateinit var sharedSpaceRepository: SharedSpaceRepository

    private lateinit var createWorkGroupInteractor: CreateWorkGroupInteractor

    @BeforeEach
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        createWorkGroupInteractor = CreateWorkGroupInteractor(sharedSpaceRepository)
    }

    @Test
    fun createSharedSpaceShouldSuccessWithNewSharedSpace() {
        runBlockingTest {
            `when`(sharedSpaceRepository.createWorkGroup(CREATE_WORK_GROUP_REQUEST))
                .thenAnswer { SharedSpaceDocumentFixtures.SHARED_SPACE_1 }

            assertThat(createWorkGroupInteractor(CREATE_WORK_GROUP_REQUEST)
                    .map { it(INIT_STATE) }
                    .toList(ArrayList()))
                .containsExactly(LOADING_STATE, CREATE_WORK_GROUP_SUCCESS_STATE)
        }
    }

    @Test
    fun createSharedSpaceShouldFailedWhenCreateSharedSpaceFailed() {
        runBlockingTest {
            val exception = RuntimeException("create shared space failed")

            `when`(sharedSpaceRepository.createWorkGroup(CREATE_WORK_GROUP_REQUEST))
                .thenThrow(exception)

            assertThat(createWorkGroupInteractor(CREATE_WORK_GROUP_REQUEST)
                    .map { it(INIT_STATE) }
                    .toList(ArrayList()))
                .containsExactly(LOADING_STATE, Either.Left(CreateWorkGroupFailed(exception)))
        }
    }
}
