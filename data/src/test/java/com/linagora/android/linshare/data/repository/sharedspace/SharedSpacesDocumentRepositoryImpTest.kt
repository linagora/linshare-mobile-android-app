package com.linagora.android.linshare.data.repository.sharedspace

import com.google.common.truth.Truth.assertThat
import com.linagora.android.linshare.data.datasource.sharedspacesdocument.SharedSpacesDocumentDataSource
import com.linagora.android.linshare.domain.model.sharedspace.WorkGroupNode
import com.linagora.android.testshared.SharedSpaceDocumentFixtures.NODE_ID_1
import com.linagora.android.testshared.SharedSpaceDocumentFixtures.PARENT_NODE_ID_1
import com.linagora.android.testshared.SharedSpaceDocumentFixtures.PARENT_NODE_ID_2
import com.linagora.android.testshared.SharedSpaceDocumentFixtures.SHARED_SPACE_ID_1
import com.linagora.android.testshared.SharedSpaceDocumentFixtures.SHARED_SPACE_ID_2
import com.linagora.android.testshared.SharedSpaceDocumentFixtures.WORK_GROUP_DOCUMENT_1
import com.linagora.android.testshared.SharedSpaceDocumentFixtures.WORK_GROUP_DOCUMENT_2
import com.linagora.android.testshared.SharedSpaceDocumentFixtures.WORK_GROUP_FOLDER_1
import kotlinx.coroutines.test.runBlockingTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations

class SharedSpacesDocumentRepositoryImpTest {
    @Mock
    lateinit var sharedSpacesDocumentDataSource: SharedSpacesDocumentDataSource

    private lateinit var sharedSpacesDocumentRepositoryImp: SharedSpacesDocumentRepositoryImp

    @BeforeEach
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        sharedSpacesDocumentRepositoryImp = SharedSpacesDocumentRepositoryImp(sharedSpacesDocumentDataSource)
    }

    @Test
    fun getAllNodesShouldReturnAllNodesOfSharedSpace() = runBlockingTest {
        `when`(sharedSpacesDocumentDataSource.getAllChildNodes(SHARED_SPACE_ID_1, PARENT_NODE_ID_1))
            .thenAnswer { listOf(WORK_GROUP_FOLDER_1, WORK_GROUP_DOCUMENT_1, WORK_GROUP_DOCUMENT_2) }

        assertThat(sharedSpacesDocumentRepositoryImp.getAllChildNodes(SHARED_SPACE_ID_1, PARENT_NODE_ID_1))
            .containsExactly(WORK_GROUP_FOLDER_1, WORK_GROUP_DOCUMENT_1, WORK_GROUP_DOCUMENT_2)
    }

    @Test
    fun getAllNodesShouldReturnAllNodesOfSharedSpaceWhenWithoutParentNodeId() = runBlockingTest {
        `when`(sharedSpacesDocumentDataSource.getAllChildNodes(SHARED_SPACE_ID_1, null))
            .thenAnswer { listOf(WORK_GROUP_FOLDER_1, WORK_GROUP_DOCUMENT_1, WORK_GROUP_DOCUMENT_2) }

        assertThat(sharedSpacesDocumentRepositoryImp.getAllChildNodes(SHARED_SPACE_ID_1))
            .containsExactly(WORK_GROUP_FOLDER_1, WORK_GROUP_DOCUMENT_1, WORK_GROUP_DOCUMENT_2)
    }

    @Test
    fun getAllNodesShouldReturnEmptyWhenNoNodesExist() = runBlockingTest {
        `when`(sharedSpacesDocumentDataSource.getAllChildNodes(SHARED_SPACE_ID_2, PARENT_NODE_ID_2))
            .thenAnswer { emptyList<WorkGroupNode>() }

        assertThat(sharedSpacesDocumentRepositoryImp.getAllChildNodes(SHARED_SPACE_ID_2, PARENT_NODE_ID_2))
            .isEmpty()
    }

    @Test
    fun getNodeShouldReturnADocumentWhenExisted() = runBlockingTest {
        `when`(sharedSpacesDocumentDataSource.getSharedSpaceNode(SHARED_SPACE_ID_1, NODE_ID_1))
            .thenAnswer { WORK_GROUP_DOCUMENT_1 }

        assertThat(sharedSpacesDocumentRepositoryImp.getSharedSpaceNode(SHARED_SPACE_ID_1, NODE_ID_1))
            .isEqualTo(WORK_GROUP_DOCUMENT_1)
    }
}
