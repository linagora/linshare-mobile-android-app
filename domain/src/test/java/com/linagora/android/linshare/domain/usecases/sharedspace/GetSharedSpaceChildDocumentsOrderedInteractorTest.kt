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

package com.linagora.android.linshare.domain.usecases.sharedspace

import arrow.core.Either
import com.google.common.truth.Truth
import com.linagora.android.linshare.domain.model.order.OrderListConfigurationType
import com.linagora.android.linshare.domain.model.sharedspace.WorkGroupNode
import com.linagora.android.linshare.domain.repository.sharedspacesdocument.SharedSpacesDocumentRepository
import com.linagora.android.testshared.SharedSpaceDocumentFixtures
import com.linagora.android.testshared.TestFixtures
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runBlockingTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations

class GetSharedSpaceChildDocumentsOrderedInteractorTest {
    @Mock
    lateinit var sharedSpacesDocumentRepository: SharedSpacesDocumentRepository

    private lateinit var getSharedSpaceChildDocumentsOrderedInteractor: GetSharedSpaceChildDocumentsOrderedInteractor

    @BeforeEach
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        getSharedSpaceChildDocumentsOrderedInteractor = GetSharedSpaceChildDocumentsOrderedInteractor(sharedSpacesDocumentRepository)
    }

    @Test
    fun getAllNodesShouldReturnAListOfDocumentAndFolder() = runBlockingTest {
        Mockito.`when`(sharedSpacesDocumentRepository.getAllChildNodes(SharedSpaceDocumentFixtures.SHARED_SPACE_ID_1, SharedSpaceDocumentFixtures.PARENT_NODE_ID_1))
            .thenAnswer { listOf(SharedSpaceDocumentFixtures.WORK_GROUP_FOLDER_1, SharedSpaceDocumentFixtures.WORK_GROUP_FOLDER_2, SharedSpaceDocumentFixtures.WORK_GROUP_DOCUMENT_1, SharedSpaceDocumentFixtures.WORK_GROUP_DOCUMENT_2) }

        Truth.assertThat(getSharedSpaceChildDocumentsOrderedInteractor(SharedSpaceDocumentFixtures.SHARED_SPACE_ID_1, SharedSpaceDocumentFixtures.PARENT_NODE_ID_1, OrderListConfigurationType.AscendingModificationDate)
            .map { it(TestFixtures.State.INIT_STATE) }
            .toList(ArrayList()))
            .containsExactly(TestFixtures.State.LOADING_STATE, SharedSpaceDocumentFixtures.STATE_SHARED_DOCUMENT_IN_SPACE_1)
    }

    @Test
    fun getAllNodesShouldReturnAListOfDocumentAndFolderWhenNoParentNodeIsProvided() = runBlockingTest {
        Mockito.`when`(sharedSpacesDocumentRepository.getAllChildNodes(SharedSpaceDocumentFixtures.SHARED_SPACE_ID_1, null))
            .thenAnswer { listOf(SharedSpaceDocumentFixtures.WORK_GROUP_FOLDER_1, SharedSpaceDocumentFixtures.WORK_GROUP_FOLDER_2, SharedSpaceDocumentFixtures.WORK_GROUP_DOCUMENT_1, SharedSpaceDocumentFixtures.WORK_GROUP_DOCUMENT_2) }

        Truth.assertThat(getSharedSpaceChildDocumentsOrderedInteractor(SharedSpaceDocumentFixtures.SHARED_SPACE_ID_1, null, OrderListConfigurationType.AscendingModificationDate)
            .map { it(TestFixtures.State.INIT_STATE) }
            .toList(ArrayList()))
            .containsExactly(TestFixtures.State.LOADING_STATE, SharedSpaceDocumentFixtures.STATE_SHARED_DOCUMENT_IN_SPACE_1)
    }

    @Test
    fun getAllNodesShouldReturnWhenNoMoreDocumentInSharedSpace() = runBlockingTest {
        Mockito.`when`(sharedSpacesDocumentRepository.getAllChildNodes(SharedSpaceDocumentFixtures.SHARED_SPACE_ID_2, SharedSpaceDocumentFixtures.PARENT_NODE_ID_1))
            .thenAnswer { emptyList<WorkGroupNode>() }

        Truth.assertThat(getSharedSpaceChildDocumentsOrderedInteractor(SharedSpaceDocumentFixtures.SHARED_SPACE_ID_2, SharedSpaceDocumentFixtures.PARENT_NODE_ID_1, OrderListConfigurationType.AscendingModificationDate)
            .map { it(TestFixtures.State.INIT_STATE) }
            .toList(ArrayList()))
            .containsExactly(TestFixtures.State.LOADING_STATE, SharedSpaceDocumentFixtures.STATE_EMPTY_SHARED_DOCUMENT_IN_SPACE_2)
    }

    @Test
    fun getAllNodesShouldReturnAnExceptionWhenFailure() = runBlockingTest {
        val exception = RuntimeException("get nodes failed")

        Mockito.`when`(sharedSpacesDocumentRepository.getAllChildNodes(SharedSpaceDocumentFixtures.SHARED_SPACE_ID_2, SharedSpaceDocumentFixtures.PARENT_NODE_ID_1))
            .thenThrow(exception)

        Truth.assertThat(getSharedSpaceChildDocumentsOrderedInteractor(SharedSpaceDocumentFixtures.SHARED_SPACE_ID_2, SharedSpaceDocumentFixtures.PARENT_NODE_ID_1, OrderListConfigurationType.AscendingModificationDate)
            .map { it(TestFixtures.State.INIT_STATE) }
            .toList(ArrayList()))
            .containsExactly(TestFixtures.State.LOADING_STATE, Either.left(SharedSpaceDocumentFailure(exception)))
    }

    @Test
    fun getAllNodesShouldReturnListOrderedByAscendingName() = runBlockingTest {
        Mockito.`when`(sharedSpacesDocumentRepository.getAllChildNodes(SharedSpaceDocumentFixtures.SHARED_SPACE_ID_1, SharedSpaceDocumentFixtures.PARENT_NODE_ID_1))
            .thenAnswer { listOf(SharedSpaceDocumentFixtures.WORK_GROUP_DOCUMENT_2, SharedSpaceDocumentFixtures.WORK_GROUP_DOCUMENT_3, SharedSpaceDocumentFixtures.WORK_GROUP_FOLDER_1, SharedSpaceDocumentFixtures.WORK_GROUP_FOLDER_2) }

        val sortedList = Either.right(
            SharedSpaceDocumentViewState(
                listOf(
                    SharedSpaceDocumentFixtures.WORK_GROUP_DOCUMENT_2,
                    SharedSpaceDocumentFixtures.WORK_GROUP_DOCUMENT_3,
                    SharedSpaceDocumentFixtures.WORK_GROUP_FOLDER_1,
                    SharedSpaceDocumentFixtures.WORK_GROUP_FOLDER_2
                )
            )
        )

        Truth.assertThat(getSharedSpaceChildDocumentsOrderedInteractor(SharedSpaceDocumentFixtures.SHARED_SPACE_ID_1, SharedSpaceDocumentFixtures.PARENT_NODE_ID_1, OrderListConfigurationType.AscendingName)
            .map { it(TestFixtures.State.INIT_STATE) }
            .toList(ArrayList()))
            .containsExactly(TestFixtures.State.LOADING_STATE, sortedList)
    }

    @Test
    fun getAllNodesShouldReturnListOrderedByDescendingName() = runBlockingTest {
        Mockito.`when`(sharedSpacesDocumentRepository.getAllChildNodes(SharedSpaceDocumentFixtures.SHARED_SPACE_ID_1, SharedSpaceDocumentFixtures.PARENT_NODE_ID_1))
            .thenAnswer { listOf(SharedSpaceDocumentFixtures.WORK_GROUP_DOCUMENT_2, SharedSpaceDocumentFixtures.WORK_GROUP_DOCUMENT_3, SharedSpaceDocumentFixtures.WORK_GROUP_FOLDER_1, SharedSpaceDocumentFixtures.WORK_GROUP_FOLDER_2) }

        val sortedList = Either.right(
            SharedSpaceDocumentViewState(
                listOf(
                    SharedSpaceDocumentFixtures.WORK_GROUP_FOLDER_2,
                    SharedSpaceDocumentFixtures.WORK_GROUP_FOLDER_1,
                    SharedSpaceDocumentFixtures.WORK_GROUP_DOCUMENT_3,
                    SharedSpaceDocumentFixtures.WORK_GROUP_DOCUMENT_2
                )
            )
        )

        Truth.assertThat(getSharedSpaceChildDocumentsOrderedInteractor(SharedSpaceDocumentFixtures.SHARED_SPACE_ID_1, SharedSpaceDocumentFixtures.PARENT_NODE_ID_1, OrderListConfigurationType.DescendingName)
            .map { it(TestFixtures.State.INIT_STATE) }
            .toList(ArrayList()))
            .containsExactly(TestFixtures.State.LOADING_STATE, sortedList)
    }

    @Test
    fun getAllNodesShouldReturnListOrderedByAscendingModificationDate() = runBlockingTest {
        Mockito.`when`(sharedSpacesDocumentRepository.getAllChildNodes(SharedSpaceDocumentFixtures.SHARED_SPACE_ID_1, SharedSpaceDocumentFixtures.PARENT_NODE_ID_1))
            .thenAnswer { listOf(SharedSpaceDocumentFixtures.WORK_GROUP_DOCUMENT_2, SharedSpaceDocumentFixtures.WORK_GROUP_DOCUMENT_3, SharedSpaceDocumentFixtures.WORK_GROUP_FOLDER_1, SharedSpaceDocumentFixtures.WORK_GROUP_FOLDER_2) }

        val sortedList = Either.right(
            SharedSpaceDocumentViewState(
                listOf(
                    SharedSpaceDocumentFixtures.WORK_GROUP_FOLDER_1,
                    SharedSpaceDocumentFixtures.WORK_GROUP_FOLDER_2,
                    SharedSpaceDocumentFixtures.WORK_GROUP_DOCUMENT_3,
                    SharedSpaceDocumentFixtures.WORK_GROUP_DOCUMENT_2
                )
            )
        )

        Truth.assertThat(getSharedSpaceChildDocumentsOrderedInteractor(SharedSpaceDocumentFixtures.SHARED_SPACE_ID_1, SharedSpaceDocumentFixtures.PARENT_NODE_ID_1, OrderListConfigurationType.AscendingModificationDate)
            .map { it(TestFixtures.State.INIT_STATE) }
            .toList(ArrayList()))
            .containsExactly(TestFixtures.State.LOADING_STATE, sortedList)
    }

    @Test
    fun getAllNodesShouldReturnListOrderedByDescendingModificationDate() = runBlockingTest {
        Mockito.`when`(sharedSpacesDocumentRepository.getAllChildNodes(SharedSpaceDocumentFixtures.SHARED_SPACE_ID_1, SharedSpaceDocumentFixtures.PARENT_NODE_ID_1))
            .thenAnswer { listOf(SharedSpaceDocumentFixtures.WORK_GROUP_DOCUMENT_2, SharedSpaceDocumentFixtures.WORK_GROUP_DOCUMENT_3, SharedSpaceDocumentFixtures.WORK_GROUP_FOLDER_1, SharedSpaceDocumentFixtures.WORK_GROUP_FOLDER_2) }

        val sortedList = Either.right(
            SharedSpaceDocumentViewState(
                listOf(
                    SharedSpaceDocumentFixtures.WORK_GROUP_DOCUMENT_2,
                    SharedSpaceDocumentFixtures.WORK_GROUP_DOCUMENT_3,
                    SharedSpaceDocumentFixtures.WORK_GROUP_FOLDER_1,
                    SharedSpaceDocumentFixtures.WORK_GROUP_FOLDER_2
                )
            )
        )

        Truth.assertThat(getSharedSpaceChildDocumentsOrderedInteractor(SharedSpaceDocumentFixtures.SHARED_SPACE_ID_1, SharedSpaceDocumentFixtures.PARENT_NODE_ID_1, OrderListConfigurationType.DescendingModificationDate)
            .map { it(TestFixtures.State.INIT_STATE) }
            .toList(ArrayList()))
            .containsExactly(TestFixtures.State.LOADING_STATE, sortedList)
    }

    @Test
    fun getAllNodesShouldReturnListOrderedByAscendingCreationDate() = runBlockingTest {
        Mockito.`when`(sharedSpacesDocumentRepository.getAllChildNodes(SharedSpaceDocumentFixtures.SHARED_SPACE_ID_1, SharedSpaceDocumentFixtures.PARENT_NODE_ID_1))
            .thenAnswer { listOf(SharedSpaceDocumentFixtures.WORK_GROUP_DOCUMENT_2, SharedSpaceDocumentFixtures.WORK_GROUP_DOCUMENT_3, SharedSpaceDocumentFixtures.WORK_GROUP_FOLDER_1, SharedSpaceDocumentFixtures.WORK_GROUP_FOLDER_2) }

        val sortedList = Either.right(
            SharedSpaceDocumentViewState(
                listOf(
                    SharedSpaceDocumentFixtures.WORK_GROUP_FOLDER_1,
                    SharedSpaceDocumentFixtures.WORK_GROUP_FOLDER_2,
                    SharedSpaceDocumentFixtures.WORK_GROUP_DOCUMENT_3,
                    SharedSpaceDocumentFixtures.WORK_GROUP_DOCUMENT_2
                )
            )
        )

        Truth.assertThat(getSharedSpaceChildDocumentsOrderedInteractor(SharedSpaceDocumentFixtures.SHARED_SPACE_ID_1, SharedSpaceDocumentFixtures.PARENT_NODE_ID_1, OrderListConfigurationType.AscendingCreationDate)
            .map { it(TestFixtures.State.INIT_STATE) }
            .toList(ArrayList()))
            .containsExactly(TestFixtures.State.LOADING_STATE, sortedList)
    }

    @Test
    fun getAllNodesShouldReturnListOrderedByDescendingCreationDate() = runBlockingTest {
        Mockito.`when`(sharedSpacesDocumentRepository.getAllChildNodes(SharedSpaceDocumentFixtures.SHARED_SPACE_ID_1, SharedSpaceDocumentFixtures.PARENT_NODE_ID_1))
            .thenAnswer { listOf(SharedSpaceDocumentFixtures.WORK_GROUP_DOCUMENT_2, SharedSpaceDocumentFixtures.WORK_GROUP_DOCUMENT_3, SharedSpaceDocumentFixtures.WORK_GROUP_FOLDER_1, SharedSpaceDocumentFixtures.WORK_GROUP_FOLDER_2) }

        val sortedList = Either.right(
            SharedSpaceDocumentViewState(
                listOf(
                    SharedSpaceDocumentFixtures.WORK_GROUP_DOCUMENT_2,
                    SharedSpaceDocumentFixtures.WORK_GROUP_DOCUMENT_3,
                    SharedSpaceDocumentFixtures.WORK_GROUP_FOLDER_1,
                    SharedSpaceDocumentFixtures.WORK_GROUP_FOLDER_2
                )
            )
        )

        Truth.assertThat(getSharedSpaceChildDocumentsOrderedInteractor(SharedSpaceDocumentFixtures.SHARED_SPACE_ID_1, SharedSpaceDocumentFixtures.PARENT_NODE_ID_1, OrderListConfigurationType.DescendingCreationDate)
            .map { it(TestFixtures.State.INIT_STATE) }
            .toList(ArrayList()))
            .containsExactly(TestFixtures.State.LOADING_STATE, sortedList)
    }

    @Test
    fun getAllNodesShouldReturnListOrderedByAscendingFileSize() = runBlockingTest {
        Mockito.`when`(sharedSpacesDocumentRepository.getAllChildNodes(SharedSpaceDocumentFixtures.SHARED_SPACE_ID_1, SharedSpaceDocumentFixtures.PARENT_NODE_ID_1))
            .thenAnswer { listOf(SharedSpaceDocumentFixtures.WORK_GROUP_DOCUMENT_2, SharedSpaceDocumentFixtures.WORK_GROUP_DOCUMENT_3, SharedSpaceDocumentFixtures.WORK_GROUP_FOLDER_1, SharedSpaceDocumentFixtures.WORK_GROUP_FOLDER_2) }

        val sortedList = Either.right(
            SharedSpaceDocumentViewState(
                listOf(
                    SharedSpaceDocumentFixtures.WORK_GROUP_DOCUMENT_3,
                    SharedSpaceDocumentFixtures.WORK_GROUP_DOCUMENT_2,
                    SharedSpaceDocumentFixtures.WORK_GROUP_FOLDER_1,
                    SharedSpaceDocumentFixtures.WORK_GROUP_FOLDER_2
                )
            )
        )

        Truth.assertThat(getSharedSpaceChildDocumentsOrderedInteractor(SharedSpaceDocumentFixtures.SHARED_SPACE_ID_1, SharedSpaceDocumentFixtures.PARENT_NODE_ID_1, OrderListConfigurationType.AscendingFileSize)
            .map { it(TestFixtures.State.INIT_STATE) }
            .toList(ArrayList()))
            .containsExactly(TestFixtures.State.LOADING_STATE, sortedList)
    }

    @Test
    fun getAllNodesShouldReturnListOrderedByDescendingFileSize() = runBlockingTest {
        Mockito.`when`(sharedSpacesDocumentRepository.getAllChildNodes(SharedSpaceDocumentFixtures.SHARED_SPACE_ID_1, SharedSpaceDocumentFixtures.PARENT_NODE_ID_1))
            .thenAnswer { listOf(SharedSpaceDocumentFixtures.WORK_GROUP_DOCUMENT_2, SharedSpaceDocumentFixtures.WORK_GROUP_DOCUMENT_3, SharedSpaceDocumentFixtures.WORK_GROUP_FOLDER_1, SharedSpaceDocumentFixtures.WORK_GROUP_FOLDER_2) }

        val sortedList = Either.right(
            SharedSpaceDocumentViewState(
                listOf(
                    SharedSpaceDocumentFixtures.WORK_GROUP_FOLDER_1,
                    SharedSpaceDocumentFixtures.WORK_GROUP_FOLDER_2,
                    SharedSpaceDocumentFixtures.WORK_GROUP_DOCUMENT_2,
                    SharedSpaceDocumentFixtures.WORK_GROUP_DOCUMENT_3
                )
            )
        )

        Truth.assertThat(getSharedSpaceChildDocumentsOrderedInteractor(SharedSpaceDocumentFixtures.SHARED_SPACE_ID_1, SharedSpaceDocumentFixtures.PARENT_NODE_ID_1, OrderListConfigurationType.DescendingFileSize)
            .map { it(TestFixtures.State.INIT_STATE) }
            .toList(ArrayList()))
            .containsExactly(TestFixtures.State.LOADING_STATE, sortedList)
    }
}
