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
