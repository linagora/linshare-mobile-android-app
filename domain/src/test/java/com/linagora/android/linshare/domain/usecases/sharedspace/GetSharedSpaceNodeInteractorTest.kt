package com.linagora.android.linshare.domain.usecases.sharedspace

import arrow.core.Either
import com.google.common.truth.Truth.assertThat
import com.linagora.android.linshare.domain.repository.sharedspacesdocument.SharedSpacesDocumentRepository
import com.linagora.android.testshared.SharedSpaceDocumentFixtures.NODE_ID_1
import com.linagora.android.testshared.SharedSpaceDocumentFixtures.SHARED_SPACE_ID_1
import com.linagora.android.testshared.SharedSpaceDocumentFixtures.WORK_GROUP_DOCUMENT_1
import com.linagora.android.testshared.TestFixtures.State.INIT_STATE
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runBlockingTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations

class GetSharedSpaceNodeInteractorTest {
    @Mock
    lateinit var sharedSpaceDocumentRepository: SharedSpacesDocumentRepository

    private lateinit var getSharedSpaceNodeInteractor: GetSharedSpaceNodeInteractor

    @BeforeEach
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        getSharedSpaceNodeInteractor = GetSharedSpaceNodeInteractor(sharedSpaceDocumentRepository)
    }

    @Test
    fun getShareSpaceNodeShouldReturnStateWithSharedSpaceDocument() = runBlockingTest {
        `when`(sharedSpaceDocumentRepository.getSharedSpaceNode(SHARED_SPACE_ID_1, NODE_ID_1))
            .thenAnswer { WORK_GROUP_DOCUMENT_1 }

        assertThat(getSharedSpaceNodeInteractor(SHARED_SPACE_ID_1, NODE_ID_1)
            .map { it(INIT_STATE) }
            .toList(ArrayList())
        ).containsExactly(Either.right(GetSharedSpaceNodeSuccess(WORK_GROUP_DOCUMENT_1)))
    }

    @Test
    fun getShareSpaceNodeShouldReturnFailureStateWhenHaveAProblem() = runBlockingTest {
        val exception = RuntimeException("get node failed")
        `when`(sharedSpaceDocumentRepository.getSharedSpaceNode(SHARED_SPACE_ID_1, NODE_ID_1))
            .thenThrow(exception)

        assertThat(getSharedSpaceNodeInteractor(SHARED_SPACE_ID_1, NODE_ID_1)
            .map { it(INIT_STATE) }
            .toList(ArrayList())
        ).containsExactly(Either.left(GetSharedSpaceNodeFail(exception)))
    }
}
