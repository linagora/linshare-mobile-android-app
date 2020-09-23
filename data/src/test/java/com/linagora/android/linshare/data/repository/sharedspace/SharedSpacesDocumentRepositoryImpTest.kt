/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 *
 * Copyright (C) 2020 LINAGORA
 *
 * This program is free software: you can redistribute it and/or modify it under the
 * terms of the GNU Affero General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later version,
 * provided you comply with the Additional Terms applicable for LinShare software by
 * Linagora pursuant to Section 7 of the GNU Affero General Public License,
 * subsections (b), (c), and (e), pursuant to which you must notably (i) retain the
 * display in the interface of the “LinShare™” trademark/logo, the "Libre & Free" mention,
 * the words “You are using the Free and Open Source version of LinShare™, powered by
 * Linagora © 2009–2020. Contribute to Linshare R&D by subscribing to an Enterprise
 * offer!”. You must also retain the latter notice in all asynchronous messages such as
 * e-mails sent with the Program, (ii) retain all hypertext links between LinShare and
 * http://www.linshare.org, between linagora.com and Linagora, and (iii) refrain from
 * infringing Linagora intellectual property rights over its trademarks and commercial
 * brands. Other Additional Terms apply, see
 * <http://www.linshare.org/licenses/LinShare-License_AfferoGPL-v3.pdf>
 * for more details.
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for
 * more details.
 * You should have received a copy of the GNU Affero General Public License and its
 * applicable Additional Terms for LinShare along with this program. If not, see
 * <http://www.gnu.org/licenses/> for the GNU Affero General Public License version
 *  3 and <http://www.linshare.org/licenses/LinShare-License_AfferoGPL-v3.pdf> for
 *  the Additional Terms applicable to LinShare software.
 */

package com.linagora.android.linshare.data.repository.sharedspace

import com.google.common.truth.Truth.assertThat
import com.linagora.android.linshare.data.datasource.sharedspacesdocument.SharedSpacesDocumentDataSource
import com.linagora.android.linshare.domain.model.sharedspace.WorkGroupNode
import com.linagora.android.linshare.domain.usecases.sharedspace.CreateSharedSpaceNodeException
import com.linagora.android.linshare.domain.usecases.sharedspace.RemoveNotFoundSharedSpaceDocumentException
import com.linagora.android.linshare.domain.usecases.sharedspace.RemoveSharedSpaceNodeException
import com.linagora.android.testshared.CopyFixtures
import com.linagora.android.testshared.DuplicateFixtures
import com.linagora.android.testshared.SharedSpaceDocumentFixtures.CREATE_SHARED_SPACE_NODE_REQUEST
import com.linagora.android.testshared.SharedSpaceDocumentFixtures.CREATE_SHARED_SPACE_NODE_REQUEST_WITH_PARENT_NULL
import com.linagora.android.testshared.SharedSpaceDocumentFixtures.NODE_ID_1
import com.linagora.android.testshared.SharedSpaceDocumentFixtures.PARENT_NODE_ID_1
import com.linagora.android.testshared.SharedSpaceDocumentFixtures.PARENT_NODE_ID_2
import com.linagora.android.testshared.SharedSpaceDocumentFixtures.QUERY_SHARED_SPACE_DOCUMENT
import com.linagora.android.testshared.SharedSpaceDocumentFixtures.SHARED_SPACE_ID_1
import com.linagora.android.testshared.SharedSpaceDocumentFixtures.SHARED_SPACE_ID_2
import com.linagora.android.testshared.SharedSpaceDocumentFixtures.WORK_GROUP_DOCUMENT_1
import com.linagora.android.testshared.SharedSpaceDocumentFixtures.WORK_GROUP_DOCUMENT_2
import com.linagora.android.testshared.SharedSpaceDocumentFixtures.WORK_GROUP_FOLDER_1
import kotlinx.coroutines.test.runBlockingTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
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

    @Test
    fun searchDocumentShouldReturnMatchedListDocument() = runBlockingTest {
        `when`(sharedSpacesDocumentDataSource.searchSharedSpaceDocument(
            SHARED_SPACE_ID_1, NODE_ID_1, QUERY_SHARED_SPACE_DOCUMENT))
            .thenAnswer { listOf(WORK_GROUP_DOCUMENT_1, WORK_GROUP_DOCUMENT_2) }

        assertThat(sharedSpacesDocumentRepositoryImp.searchSharedSpaceDocuments(
            SHARED_SPACE_ID_1, NODE_ID_1, QUERY_SHARED_SPACE_DOCUMENT))
            .containsExactly(WORK_GROUP_DOCUMENT_1, WORK_GROUP_DOCUMENT_2)
    }

    @Test
    fun searchDocumentShouldReturnEmptyListDocument() = runBlockingTest {
        `when`(sharedSpacesDocumentDataSource.searchSharedSpaceDocument(
            SHARED_SPACE_ID_1, NODE_ID_1, QUERY_SHARED_SPACE_DOCUMENT))
            .thenAnswer { emptyList<WorkGroupNode>() }

        assertThat(sharedSpacesDocumentRepositoryImp.searchSharedSpaceDocuments(
            SHARED_SPACE_ID_1, NODE_ID_1, QUERY_SHARED_SPACE_DOCUMENT))
            .isEmpty()
    }

    @Test
    fun searchDocumentShouldThrowWhenSearchHaveError() = runBlockingTest {
        `when`(sharedSpacesDocumentDataSource.searchSharedSpaceDocument(
            SHARED_SPACE_ID_1, NODE_ID_1, QUERY_SHARED_SPACE_DOCUMENT))
            .thenThrow(RuntimeException())

        assertThrows<RuntimeException> {
            runBlockingTest {
                sharedSpacesDocumentRepositoryImp.searchSharedSpaceDocuments(
                    SHARED_SPACE_ID_1, NODE_ID_1, QUERY_SHARED_SPACE_DOCUMENT
                )
            }
        }
    }

    @Test
    fun removeSharedSpaceNodeSuccess() = runBlockingTest {
        `when`(sharedSpacesDocumentRepositoryImp.removeSharedSpaceNode(SHARED_SPACE_ID_1, PARENT_NODE_ID_1))
            .thenAnswer { WORK_GROUP_DOCUMENT_1 }

        val document = sharedSpacesDocumentRepositoryImp.removeSharedSpaceNode(SHARED_SPACE_ID_1, PARENT_NODE_ID_1)
        assertThat(document).isEqualTo(WORK_GROUP_DOCUMENT_1)
    }

    @Test
    fun removeShouldThrowWhenRemoveSharedSpaceNodeFailure() {
        runBlockingTest {
            `when`(sharedSpacesDocumentDataSource.removeSharedSpaceNode(SHARED_SPACE_ID_1, PARENT_NODE_ID_1))
                .thenThrow(RemoveSharedSpaceNodeException(RuntimeException()))

            assertThrows<RemoveSharedSpaceNodeException> {
                runBlockingTest { sharedSpacesDocumentRepositoryImp.removeSharedSpaceNode(SHARED_SPACE_ID_1, PARENT_NODE_ID_1) }
            }
        }
    }

    @Test
    fun removeShouldThrowWhenRemoveSharedSpaceNodeNotFound() {
        runBlockingTest {
            `when`(sharedSpacesDocumentDataSource.removeSharedSpaceNode(SHARED_SPACE_ID_1, PARENT_NODE_ID_1))
                .thenThrow(RemoveNotFoundSharedSpaceDocumentException)

            assertThrows<RemoveNotFoundSharedSpaceDocumentException> {
                runBlockingTest { sharedSpacesDocumentRepositoryImp.removeSharedSpaceNode(SHARED_SPACE_ID_1, PARENT_NODE_ID_1) }
            }
        }
    }

    @Nested
    inner class CopyToSharedSpace {

        @Mock
        lateinit var sharedSpacesDocumentDataSource: SharedSpacesDocumentDataSource

        private lateinit var sharedSpacesDocumentRepositoryImp: SharedSpacesDocumentRepositoryImp

        @BeforeEach
        fun setUp() {
            MockitoAnnotations.initMocks(this)
            sharedSpacesDocumentRepositoryImp =
                SharedSpacesDocumentRepositoryImp(sharedSpacesDocumentDataSource)
        }

        @Test
        fun copyToSharedSpaceShouldReturnSuccessCopiedDocument() = runBlockingTest {
            `when`(sharedSpacesDocumentDataSource.copyToSharedSpace(
                    CopyFixtures.COPY_WORKGROUP_DOCUMENT_TO_SHARED_SPACE,
                    CopyFixtures.DESTINATION_SHARED_SPACE_ID,
                    CopyFixtures.DESTINATION_PARENT_NODE_ID))
                .thenAnswer { listOf(WORK_GROUP_DOCUMENT_1) }

            val copiedDocuments = sharedSpacesDocumentRepositoryImp
                .copyToSharedSpace(
                    CopyFixtures.COPY_WORKGROUP_DOCUMENT_TO_SHARED_SPACE,
                    CopyFixtures.DESTINATION_SHARED_SPACE_ID,
                    CopyFixtures.DESTINATION_PARENT_NODE_ID)

            assertThat(copiedDocuments).hasSize(1)
            assertThat(copiedDocuments[0]).isEqualTo(WORK_GROUP_DOCUMENT_1)
        }

        @Test
        fun copyToSharedSpaceShouldReturnSuccessCopiedDocumentWithoutDestinationParentNodeId() = runBlockingTest {
            `when`(sharedSpacesDocumentDataSource.copyToSharedSpace(
                    CopyFixtures.COPY_WORKGROUP_DOCUMENT_TO_SHARED_SPACE,
                    CopyFixtures.DESTINATION_SHARED_SPACE_ID))
                .thenAnswer { listOf(WORK_GROUP_DOCUMENT_1) }

            val copiedDocuments = sharedSpacesDocumentRepositoryImp
                .copyToSharedSpace(
                    CopyFixtures.COPY_WORKGROUP_DOCUMENT_TO_SHARED_SPACE,
                    CopyFixtures.DESTINATION_SHARED_SPACE_ID)

            assertThat(copiedDocuments).hasSize(1)
            assertThat(copiedDocuments[0]).isEqualTo(WORK_GROUP_DOCUMENT_1)
        }

        @Test
        fun copyToSharedSpaceShouldThrowExceptionWhenCopyFailed() = runBlockingTest {
            val exception = RuntimeException("copy failed")
            `when`(sharedSpacesDocumentDataSource.copyToSharedSpace(
                    CopyFixtures.COPY_WORKGROUP_DOCUMENT_TO_SHARED_SPACE,
                    CopyFixtures.DESTINATION_SHARED_SPACE_ID,
                    CopyFixtures.DESTINATION_PARENT_NODE_ID))
                .thenThrow(exception)

            assertThrows<RuntimeException> { runBlockingTest {
                sharedSpacesDocumentRepositoryImp.copyToSharedSpace(
                    CopyFixtures.COPY_WORKGROUP_DOCUMENT_TO_SHARED_SPACE,
                    CopyFixtures.DESTINATION_SHARED_SPACE_ID,
                    CopyFixtures.DESTINATION_PARENT_NODE_ID
                )
            } }
        }
    }

    @Nested
    inner class CreateSharedSpaceFolder {
        @Test
        fun createSharedSpaceFolderSuccess() = runBlockingTest {
            `when`(sharedSpacesDocumentRepositoryImp.createSharedSpaceFolder(SHARED_SPACE_ID_1, CREATE_SHARED_SPACE_NODE_REQUEST))
                .thenAnswer { WORK_GROUP_FOLDER_1 }

            val folder = sharedSpacesDocumentRepositoryImp.createSharedSpaceFolder(SHARED_SPACE_ID_1, CREATE_SHARED_SPACE_NODE_REQUEST)
            assertThat(folder).isEqualTo(WORK_GROUP_FOLDER_1)
        }

        @Test
        fun createSharedSpaceFolderSuccessWhenParentWorkGroupNodeIdIsNull() = runBlockingTest {
            `when`(sharedSpacesDocumentRepositoryImp.createSharedSpaceFolder(SHARED_SPACE_ID_1, CREATE_SHARED_SPACE_NODE_REQUEST_WITH_PARENT_NULL))
                .thenAnswer { WORK_GROUP_FOLDER_1 }

            val folder = sharedSpacesDocumentRepositoryImp.createSharedSpaceFolder(SHARED_SPACE_ID_1, CREATE_SHARED_SPACE_NODE_REQUEST_WITH_PARENT_NULL)
            assertThat(folder).isEqualTo(WORK_GROUP_FOLDER_1)
        }

        @Test
        fun createSharedSpaceFolderShouldThrowWhenCreateSharedSpaceFolderFailure() {
            runBlockingTest {
                `when`(sharedSpacesDocumentDataSource.createSharedSpaceFolder(SHARED_SPACE_ID_1, CREATE_SHARED_SPACE_NODE_REQUEST))
                    .thenThrow(CreateSharedSpaceNodeException(RuntimeException()))

                assertThrows<CreateSharedSpaceNodeException> {
                    runBlockingTest { sharedSpacesDocumentRepositoryImp.createSharedSpaceFolder(SHARED_SPACE_ID_1, CREATE_SHARED_SPACE_NODE_REQUEST) }
                }
            }
        }
    }

    @Nested
    inner class DuplicateInSharedSpace {
        @Test
        fun duplicateWorkGroupNodeSuccessShouldReturnDuplicatedDocument() {
            runBlockingTest {
                `when`(sharedSpacesDocumentRepositoryImp.duplicateWorkGroupNode(DuplicateFixtures.DUPLICATE_REQUEST_1, SHARED_SPACE_ID_1))
                    .thenAnswer { listOf(WORK_GROUP_DOCUMENT_1) }

                val duplicatedDocuments = sharedSpacesDocumentRepositoryImp
                    .duplicateWorkGroupNode(DuplicateFixtures.DUPLICATE_REQUEST_1, SHARED_SPACE_ID_1)

                assertThat(duplicatedDocuments).hasSize(1)
                assertThat(duplicatedDocuments[0]).isEqualTo(WORK_GROUP_DOCUMENT_1)
            }
        }

        @Test
        fun duplicateWorkGroupNodeShouldFailWhenDataSourceFail() {
            runBlockingTest {
                `when`(sharedSpacesDocumentRepositoryImp.duplicateWorkGroupNode(DuplicateFixtures.DUPLICATE_REQUEST_1, SHARED_SPACE_ID_1))
                    .thenThrow(RuntimeException())

                assertThrows<RuntimeException> {
                    runBlockingTest { sharedSpacesDocumentRepositoryImp.duplicateWorkGroupNode(DuplicateFixtures.DUPLICATE_REQUEST_1, SHARED_SPACE_ID_1) }
                }
            }
        }
    }
}
