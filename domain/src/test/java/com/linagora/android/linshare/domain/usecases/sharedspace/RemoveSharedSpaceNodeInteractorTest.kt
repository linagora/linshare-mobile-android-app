package com.linagora.android.linshare.domain.usecases.sharedspace

import arrow.core.Either
import com.google.common.truth.Truth.assertThat
import com.linagora.android.linshare.domain.repository.sharedspacesdocument.SharedSpacesDocumentRepository
import com.linagora.android.testshared.SharedSpaceDocumentFixtures.PARENT_NODE_ID_1
import com.linagora.android.testshared.SharedSpaceDocumentFixtures.REMOVE_SHARED_SPACE_DOCUMENT_SUCCESS_VIEW_STATE
import com.linagora.android.testshared.SharedSpaceDocumentFixtures.SHARED_SPACE_ID_1
import com.linagora.android.testshared.SharedSpaceDocumentFixtures.WORK_GROUP_DOCUMENT_1
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

class RemoveSharedSpaceNodeInteractorTest {

    @Mock
    private lateinit var sharedSpacesDocumentRepository: SharedSpacesDocumentRepository

    private lateinit var removeSharedSpaceNodeInteractor: RemoveSharedSpaceNodeInteractor

    @BeforeEach
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        removeSharedSpaceNodeInteractor = RemoveSharedSpaceNodeInteractor(sharedSpacesDocumentRepository)
    }

    @Test
    fun removeSharedSpaceNodeShouldSuccess() {
        runBlockingTest {
            `when`(sharedSpacesDocumentRepository.removeSharedSpaceNode(SHARED_SPACE_ID_1, PARENT_NODE_ID_1))
                .thenAnswer { WORK_GROUP_DOCUMENT_1 }

            assertThat(removeSharedSpaceNodeInteractor(SHARED_SPACE_ID_1, PARENT_NODE_ID_1)
                .map { it(INIT_STATE) }
                .toList(ArrayList()))
                .containsExactly(LOADING_STATE, REMOVE_SHARED_SPACE_DOCUMENT_SUCCESS_VIEW_STATE)
        }
    }

    @Test
    fun removeSharedSpaceNodeShouldFailed() {
        runBlockingTest {
            val exception = RuntimeException("remove document failed")

            `when`(sharedSpacesDocumentRepository.removeSharedSpaceNode(SHARED_SPACE_ID_1, PARENT_NODE_ID_1))
                .thenThrow(exception)

            assertThat(removeSharedSpaceNodeInteractor(SHARED_SPACE_ID_1, PARENT_NODE_ID_1)
                .map { it(INIT_STATE) }
                .toList(ArrayList()))
                .containsExactly(LOADING_STATE, Either.Left(RemoveSharedSpaceNodeFailure(exception)))
        }
    }

    @Test
    fun removeNotFoundSharedSpaceNodeShouldFailed() {
        runBlockingTest {
            val exception = RemoveNotFoundSharedSpaceDocumentException

            `when`(sharedSpacesDocumentRepository.removeSharedSpaceNode(SHARED_SPACE_ID_1, PARENT_NODE_ID_1))
                .thenThrow(exception)

            assertThat(removeSharedSpaceNodeInteractor(SHARED_SPACE_ID_1, PARENT_NODE_ID_1)
                .map { it(INIT_STATE) }
                .toList(ArrayList()))
                .containsExactly(LOADING_STATE, Either.Left(RemoveNodeNotFoundSharedSpaceState))
        }
    }
}
