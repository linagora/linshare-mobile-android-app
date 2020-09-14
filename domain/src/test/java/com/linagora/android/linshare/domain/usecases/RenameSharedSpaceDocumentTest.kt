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

package com.linagora.android.linshare.domain.usecases

import arrow.core.Either
import com.google.common.truth.Truth.assertThat
import com.linagora.android.linshare.domain.model.account.Account
import com.linagora.android.linshare.domain.model.sharedspace.WorkGroupDocument
import com.linagora.android.linshare.domain.model.sharedspace.WorkGroupNodeId
import com.linagora.android.linshare.domain.model.sharedspace.WorkGroupNodeType
import com.linagora.android.linshare.domain.model.sharedspace.toRenameRequest
import com.linagora.android.linshare.domain.repository.sharedspacesdocument.SharedSpacesDocumentRepository
import com.linagora.android.linshare.domain.usecases.sharedspace.RenameFailure
import com.linagora.android.linshare.domain.usecases.sharedspace.RenameSuccess
import com.linagora.android.linshare.domain.usecases.sharedspacedocument.RenameSharedSpaceDocument
import com.linagora.android.testshared.SharedSpaceDocumentFixtures.NODE_ID_1
import com.linagora.android.testshared.SharedSpaceDocumentFixtures.SHARED_SPACE_ID_1
import com.linagora.android.testshared.SharedSpaceDocumentFixtures.WORK_GROUP_DOCUMENT_1
import com.linagora.android.testshared.TestFixtures.State.INIT_STATE
import com.linagora.android.testshared.TestFixtures.State.LOADING_STATE
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runBlockingTest
import okhttp3.MediaType
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import java.util.Date
import java.util.UUID
import javax.inject.Singleton

@Singleton
class RenameSharedSpaceDocumentTest {

    @Mock
    lateinit var sharedSpacesDocumentRepository: SharedSpacesDocumentRepository

    private lateinit var renameSharedSpaceDocument: RenameSharedSpaceDocument

    @BeforeEach
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        renameSharedSpaceDocument = RenameSharedSpaceDocument(sharedSpacesDocumentRepository)
    }

    @Test
    fun renameWorkgroupNodeShouldReturnSuccessStateWhenRequestIsValid() = runBlockingTest {
        val newWorkGroupNode = WorkGroupDocument(
            workGroupNodeId = NODE_ID_1,
            name = "New Document.odt",
            parentWorkGroupNodeId = WorkGroupNodeId(UUID.fromString("7371802c-f452-47a8-a65c-b5f0b36ce410")),
            sharedSpaceId = SHARED_SPACE_ID_1,
            description = "document 1",
            creationDate = Date(1588664701182),
            modificationDate = Date(1588664701182),
            lastAuthor = Account(
                uuid = UUID.fromString("bfe33a5-1cc3-49b8-8442-efb9dffdc989"),
                firstName = "John",
                lastName = "Doe",
                name = "John Doe",
                mail = "user1@linshare.org"),
            size = 78480,
            mimeType = MediaType.parse("application/vnd.oasis.opendocument.text")!!,
            hasRevision = false,
            sha256sum = "d6747b1e2516e8ff545a9d454aebe1e89b6f55f720cae0625a2a767259559842",
            uploadDate = Date(1588664701182),
            hasThumbnail = true,
            treePath = emptyList(),
            type = WorkGroupNodeType.DOCUMENT
        )

        `when`(sharedSpacesDocumentRepository.renameSharedSpaceNode(
                SHARED_SPACE_ID_1,
                NODE_ID_1,
                WORK_GROUP_DOCUMENT_1.toRenameRequest("New Document.odt")))
            .thenAnswer { newWorkGroupNode }

        val states = renameSharedSpaceDocument(
                SHARED_SPACE_ID_1,
                NODE_ID_1,
                WORK_GROUP_DOCUMENT_1.toRenameRequest("New Document.odt"))
            .map { it(INIT_STATE) }
            .toList(ArrayList())

        assertThat(states).hasSize(2)
        assertThat(states).containsExactly(LOADING_STATE, Either.right(RenameSuccess(newWorkGroupNode)))
    }

    @Test
    fun renameWorkgroupNodeShouldReturnFailedStateWhenRequestIsInvalid() = runBlockingTest {
        val renameException = RuntimeException()

        `when`(sharedSpacesDocumentRepository.renameSharedSpaceNode(
                SHARED_SPACE_ID_1,
                NODE_ID_1,
                WORK_GROUP_DOCUMENT_1.toRenameRequest("New Document.odt")))
            .thenThrow(renameException)

        val states = renameSharedSpaceDocument(
                SHARED_SPACE_ID_1,
                NODE_ID_1,
                WORK_GROUP_DOCUMENT_1.toRenameRequest("New Document.odt"))
            .map { it(INIT_STATE) }
            .toList(ArrayList())

        assertThat(states).hasSize(2)
        assertThat(states).containsExactly(LOADING_STATE, Either.left(RenameFailure(renameException)))
    }
}
