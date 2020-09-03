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

package com.linagora.android.testshared

import arrow.core.Either
import com.linagora.android.linshare.domain.model.account.Account
import com.linagora.android.linshare.domain.model.search.QueryString
import com.linagora.android.linshare.domain.model.sharedspace.CreateSharedSpaceNodeRequest
import com.linagora.android.linshare.domain.model.sharedspace.LinShareNodeType
import com.linagora.android.linshare.domain.model.sharedspace.SharedSpace
import com.linagora.android.linshare.domain.model.sharedspace.SharedSpaceId
import com.linagora.android.linshare.domain.model.sharedspace.SharedSpaceRole
import com.linagora.android.linshare.domain.model.sharedspace.SharedSpaceRoleId
import com.linagora.android.linshare.domain.model.sharedspace.SharedSpaceRoleName
import com.linagora.android.linshare.domain.model.sharedspace.TreePath
import com.linagora.android.linshare.domain.model.sharedspace.WorkGroupDocument
import com.linagora.android.linshare.domain.model.sharedspace.WorkGroupFolder
import com.linagora.android.linshare.domain.model.sharedspace.WorkGroupNodeId
import com.linagora.android.linshare.domain.model.sharedspace.WorkGroupNodeType
import com.linagora.android.linshare.domain.model.sharedspace.member.AddMemberRequest
import com.linagora.android.linshare.domain.model.sharedspace.member.SharedSpaceAccount
import com.linagora.android.linshare.domain.model.sharedspace.member.SharedSpaceAccountId
import com.linagora.android.linshare.domain.model.sharedspace.member.SharedSpaceMember
import com.linagora.android.linshare.domain.model.sharedspace.member.SharedSpaceMemberId
import com.linagora.android.linshare.domain.usecases.sharedspace.CreateSharedSpaceFolderSuccessViewState
import com.linagora.android.linshare.domain.usecases.sharedspace.RemoveSharedSpaceNodeSuccessViewState
import com.linagora.android.linshare.domain.usecases.sharedspace.SearchSharedSpaceDocumentViewState
import com.linagora.android.linshare.domain.usecases.sharedspace.SharedSpaceDocumentEmpty
import com.linagora.android.linshare.domain.usecases.sharedspace.SharedSpaceDocumentViewState
import com.linagora.android.linshare.domain.usecases.sharedspace.member.GetMembersSuccess
import com.linagora.android.testshared.TestFixtures.Accounts.QUOTA_UUID
import okhttp3.MediaType
import java.util.Date
import java.util.UUID

object SharedSpaceDocumentFixtures {
    val SHARED_SPACE_ID_1 = SharedSpaceId(UUID.fromString("6c0e1f35-89e5-6bc3-a8d4-156ec8074beb"))

    val PARENT_NODE_ID_1 = WorkGroupNodeId(UUID.fromString("7371802c-f452-47a8-a65c-b5f0b36ce410"))

    val SHARED_SPACE_ID_2 = SharedSpaceId(UUID.fromString("e235c1ed-ec9a-492a-ae5b-5998eb1a1374"))

    val PARENT_NODE_ID_2 = WorkGroupNodeId(UUID.fromString("7371802c-f452-47a8-a65c-b5f0b36ce401"))

    val NODE_ID_1 = WorkGroupNodeId(UUID.fromString("d3f70cc4-efde-49b8-92d7-85fc229b3c05"))

    val WORK_GROUP_FOLDER_1 = WorkGroupFolder(
        workGroupNodeId = WorkGroupNodeId(UUID.fromString("f231c326-64af-4956-82bd-94215dd8d6e2")),
        parentWorkGroupNodeId = WorkGroupNodeId(UUID.fromString("7371802c-f452-47a8-a65c-b5f0b36ce410")),
        creationDate = Date(1574837876965),
        sharedSpaceId = SHARED_SPACE_ID_1,
        modificationDate = Date(1574837876965),
        description = "test description",
        name = "FOLDER1",
        treePath = emptyList(),
        lastAuthor = Account(
            uuid = UUID.fromString("bfe33a5-1cc3-49b8-8442-efb9dffdc989"),
            firstName = "John",
            lastName = "Doe",
            name = "John Doe",
            mail = "user1@linshare.org"),
        type = WorkGroupNodeType.FOLDER
    )

    val WORK_GROUP_FOLDER_2 = WorkGroupFolder(
        workGroupNodeId = WorkGroupNodeId(UUID.fromString("5e50bb85-8255-4ae8-a3d6-afea3a4a71ac")),
        parentWorkGroupNodeId = WorkGroupNodeId(UUID.fromString("7371802c-f452-47a8-a65c-b5f0b36ce410")),
        creationDate = Date(1574837876965),
        sharedSpaceId = SHARED_SPACE_ID_1,
        modificationDate = Date(1574837876965),
        description = "test description 2",
        name = "FOLDER2",
        lastAuthor = Account(
            uuid = UUID.fromString("bfe33a5-1cc3-49b8-8442-efb9dffdc989"),
            firstName = "John",
            lastName = "Doe",
            name = "John Doe",
            mail = "user1@linshare.org"),
        treePath = listOf(TreePath(PARENT_NODE_ID_1, "FOLDER1")),
        type = WorkGroupNodeType.FOLDER
    )

    val WORK_GROUP_DOCUMENT_1 = WorkGroupDocument(
        workGroupNodeId = NODE_ID_1,
        name = "Document_Daily report_090420.odt",
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

    val WORK_GROUP_DOCUMENT_2 = WorkGroupDocument(
        workGroupNodeId = WorkGroupNodeId(UUID.fromString("f26e2cea-9bf6-4a24-b4a3-6f33418ad3c7")),
        name = "Document2.odt",
        parentWorkGroupNodeId = WorkGroupNodeId(UUID.fromString("7371802c-f452-47a8-a65c-b5f0b36ce410")),
        sharedSpaceId = SHARED_SPACE_ID_1,
        description = "document 2",
        creationDate = Date(1588664701275),
        modificationDate = Date(1588664701275),
        lastAuthor = Account(
            uuid = UUID.fromString("bfe33a5-1cc3-49b8-8442-efb9dffdc989"),
            firstName = "John",
            lastName = "Doe",
            name = "John Doe",
            mail = "user1@linshare.org"),
        size = 78481,
        mimeType = MediaType.parse("application/vnd.oasis.opendocument.text")!!,
        hasRevision = false,
        sha256sum = "1bc66785d75d22f7aa0cf7870b2baa8187f0bf7cd3eed65fd590b73cc8d56794",
        uploadDate = Date(1588664701275),
        hasThumbnail = true,
        treePath = listOf(TreePath(PARENT_NODE_ID_1, "FOLDER1")),
        type = WorkGroupNodeType.DOCUMENT
    )

    val STATE_SHARED_DOCUMENT_IN_SPACE_1 = Either.right(SharedSpaceDocumentViewState(
        listOf(WORK_GROUP_FOLDER_1, WORK_GROUP_FOLDER_2, WORK_GROUP_DOCUMENT_1, WORK_GROUP_DOCUMENT_2)
    ))

    val STATE_EMPTY_SHARED_DOCUMENT_IN_SPACE_2 = Either.right(SharedSpaceDocumentEmpty)

    val SHARED_SPACE_1 = SharedSpace(
        sharedSpaceId = SHARED_SPACE_ID_1,
        name = "ABC",
        creationDate = Date(1588664667969),
        modificationDate = Date(1588928300716),
        nodeType = LinShareNodeType.WORK_GROUP,
        role = SharedSpaceRole(SharedSpaceRoleId(UUID.fromString("234be74d-2966-41c1-9dee-e47c8c63c14e")), SharedSpaceRoleName.ADMIN),
        quotaId = QUOTA_UUID
    )

    val QUERY_SHARED_SPACE_DOCUMENT = QueryString("document")

    val SEARCH_SHARED_SPACE_DOCUMENT_STATE = Either.right(SearchSharedSpaceDocumentViewState(
        listOf(WORK_GROUP_DOCUMENT_1, WORK_GROUP_DOCUMENT_2))
    )
    val REMOVE_SHARED_SPACE_DOCUMENT_SUCCESS_VIEW_STATE = Either.Right(RemoveSharedSpaceNodeSuccessViewState(WORK_GROUP_DOCUMENT_1))

    private val JOHN_DOE_ACCOUNT = SharedSpaceAccount(
        SharedSpaceAccountId(UUID.fromString("8bfe33a5-1cc3-49b8-8442-efb9dffdc989")),
        "John Doe",
        "John",
        "Doe",
        "john@linshare.com"
    )

    private val JOHN_DOE_ACCOUNT_ID = SharedSpaceMemberId(UUID.fromString("bfdf5b4c-2fff-4abd-b139-323b8ad91790"))

    val JOHN_DOE_MEMBER = SharedSpaceMember(
        JOHN_DOE_ACCOUNT_ID,
        SharedSpaceFixtures.SHARED_SPACE_1,
        SharedSpaceRole(SharedSpaceRoleId(UUID.fromString("234be74d-2966-41c1-9dee-e47c8c63c14e")), SharedSpaceRoleName.READER),
        JOHN_DOE_ACCOUNT,
        Date(1585499403825),
        Date(1585499715142)
    )

    private val BAR_FOO_ACCOUNT_ID = SharedSpaceAccountId(UUID.fromString("b1980d34-8dc2-4278-9600-07b9515e7839"))

    private val BAR_FOO_ACCOUNT = SharedSpaceAccount(
        BAR_FOO_ACCOUNT_ID,
        "Jane Smith",
        "Jane",
        "Smith",
        "jane@linshare.com"
    )

    val BAR_FOO_MEMBER = SharedSpaceMember(
        SharedSpaceMemberId(UUID.fromString("86c05687-dbd8-4cdf-bece-76d2dd4b2fc4")),
        SharedSpaceFixtures.SHARED_SPACE_1,
        SharedSpaceRole(SharedSpaceRoleId(UUID.fromString("234be74d-2966-41c1-9dee-e47c8c63c14e")), SharedSpaceRoleName.ADMIN),
        BAR_FOO_ACCOUNT,
        Date(1584032316455),
        Date(1584032316455)
    )

    val GET_MEMBERS_SUCCESS_STATE = Either.right(
        GetMembersSuccess(listOf(JOHN_DOE_MEMBER, BAR_FOO_MEMBER)))

    val ADD_BAR_FOO_MEMBER_REQUEST = AddMemberRequest(
        BAR_FOO_ACCOUNT_ID,
        SHARED_SPACE_ID_1,
        SharedSpaceRoleId(UUID.fromString("234be74d-2966-41c1-9dee-e47c8c63c14e"))
    )

    val CREATE_SHARED_SPACE_NODE_REQUEST = CreateSharedSpaceNodeRequest(
        "NEW FOLDER",
        PARENT_NODE_ID_1,
        WorkGroupNodeType.FOLDER
    )

    val CREATE_SHARED_SPACE_NODE_REQUEST_WITH_PARENT_NULL = CreateSharedSpaceNodeRequest(
        "NEW FOLDER",
        null,
        WorkGroupNodeType.FOLDER
    )

    val STATE_CREATE_FOLDER_SUCCESS = Either.right(CreateSharedSpaceFolderSuccessViewState(WORK_GROUP_FOLDER_1))
}
