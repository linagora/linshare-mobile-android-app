package com.linagora.android.linshare.domain.usecases.sharedspace

import arrow.core.Either
import com.google.common.truth.Truth.assertThat
import com.linagora.android.linshare.domain.model.sharedspace.WorkGroupNode
import com.linagora.android.linshare.domain.repository.sharedspacesdocument.SharedSpacesDocumentRepository
import com.linagora.android.testshared.SharedSpaceDocumentFixtures.PARENT_NODE_ID_1
import com.linagora.android.testshared.SharedSpaceDocumentFixtures.SHARED_SPACE_ID_1
import com.linagora.android.testshared.SharedSpaceDocumentFixtures.SHARED_SPACE_ID_2
import com.linagora.android.testshared.SharedSpaceDocumentFixtures.STATE_EMPTY_SHARED_DOCUMENT_IN_SPACE_2
import com.linagora.android.testshared.SharedSpaceDocumentFixtures.STATE_SHARED_DOCUMENT_IN_SPACE_1
import com.linagora.android.testshared.SharedSpaceDocumentFixtures.WORK_GROUP_DOCUMENT_1
import com.linagora.android.testshared.SharedSpaceDocumentFixtures.WORK_GROUP_DOCUMENT_2
import com.linagora.android.testshared.SharedSpaceDocumentFixtures.WORK_GROUP_FOLDER_1
import com.linagora.android.testshared.SharedSpaceDocumentFixtures.WORK_GROUP_FOLDER_2
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

class GetSharedSpaceChildDocumentsInteractorTest {

    @Mock
    lateinit var sharedSpacesDocumentRepository: SharedSpacesDocumentRepository

    private lateinit var getSharedSpaceChildDocumentsInteractor: GetSharedSpaceChildDocumentsInteractor

    @BeforeEach
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        getSharedSpaceChildDocumentsInteractor = GetSharedSpaceChildDocumentsInteractor(sharedSpacesDocumentRepository)
    }

    @Test
    fun getAllNodesShouldReturnAListOfDocumentAndFolder() = runBlockingTest {
        `when`(sharedSpacesDocumentRepository.getAllChildNodes(SHARED_SPACE_ID_1, PARENT_NODE_ID_1))
            .thenAnswer { listOf(WORK_GROUP_FOLDER_1, WORK_GROUP_FOLDER_2, WORK_GROUP_DOCUMENT_1, WORK_GROUP_DOCUMENT_2) }

        assertThat(getSharedSpaceChildDocumentsInteractor(SHARED_SPACE_ID_1, PARENT_NODE_ID_1)
                .map { it(INIT_STATE) }
                .toList(ArrayList()))
            .containsExactly(LOADING_STATE, STATE_SHARED_DOCUMENT_IN_SPACE_1)
    }

    @Test
    fun getAllNodesShouldReturnAListOfDocumentAndFolderWhenNoParentNodeIsProvided() = runBlockingTest {
        `when`(sharedSpacesDocumentRepository.getAllChildNodes(SHARED_SPACE_ID_1, null))
            .thenAnswer { listOf(WORK_GROUP_FOLDER_1, WORK_GROUP_FOLDER_2, WORK_GROUP_DOCUMENT_1, WORK_GROUP_DOCUMENT_2) }

        assertThat(getSharedSpaceChildDocumentsInteractor(SHARED_SPACE_ID_1, null)
            .map { it(INIT_STATE) }
            .toList(ArrayList()))
            .containsExactly(LOADING_STATE, STATE_SHARED_DOCUMENT_IN_SPACE_1)
    }

    @Test
    fun getAllNodesShouldReturnWhenNoMoreDocumentInSharedSpace() = runBlockingTest {
        `when`(sharedSpacesDocumentRepository.getAllChildNodes(SHARED_SPACE_ID_2, PARENT_NODE_ID_1))
            .thenAnswer { emptyList<WorkGroupNode>() }

        assertThat(getSharedSpaceChildDocumentsInteractor(SHARED_SPACE_ID_2, PARENT_NODE_ID_1)
                .map { it(INIT_STATE) }
                .toList(ArrayList()))
            .containsExactly(LOADING_STATE, STATE_EMPTY_SHARED_DOCUMENT_IN_SPACE_2)
    }

    @Test
    fun getAllNodesShouldReturnAnExceptionWhenFailure() = runBlockingTest {
        val exception = RuntimeException("get nodes failed")

        `when`(sharedSpacesDocumentRepository.getAllChildNodes(SHARED_SPACE_ID_2, PARENT_NODE_ID_1))
            .thenThrow(exception)

        assertThat(getSharedSpaceChildDocumentsInteractor(SHARED_SPACE_ID_2, PARENT_NODE_ID_1)
                .map { it(INIT_STATE) }
                .toList(ArrayList()))
            .containsExactly(LOADING_STATE, Either.left(SharedSpaceDocumentFailure(exception)))
    }
}
