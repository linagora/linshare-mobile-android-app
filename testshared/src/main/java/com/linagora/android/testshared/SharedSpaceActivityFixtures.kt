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
import com.linagora.android.linshare.domain.model.audit.AuditLogEntryId
import com.linagora.android.linshare.domain.model.audit.AuditLogEntryType
import com.linagora.android.linshare.domain.model.audit.LogAction
import com.linagora.android.linshare.domain.model.audit.LogActionCause
import com.linagora.android.linshare.domain.model.audit.workgroup.SharedSpaceMemberAuditLogEntry
import com.linagora.android.linshare.domain.model.audit.workgroup.SharedSpaceNodeAuditLogEntry
import com.linagora.android.linshare.domain.model.audit.workgroup.WorkGroupDocumentAuditLogEntry
import com.linagora.android.linshare.domain.model.audit.workgroup.WorkGroupFolderAuditLogEntry
import com.linagora.android.linshare.domain.model.copy.SpaceType
import com.linagora.android.linshare.domain.model.quota.QuotaId
import com.linagora.android.linshare.domain.model.sharedspace.LinShareNodeType
import com.linagora.android.linshare.domain.model.sharedspace.SharedSpace
import com.linagora.android.linshare.domain.model.sharedspace.SharedSpaceId
import com.linagora.android.linshare.domain.model.sharedspace.SharedSpaceNodeNested
import com.linagora.android.linshare.domain.model.sharedspace.SharedSpaceRole
import com.linagora.android.linshare.domain.model.sharedspace.SharedSpaceRoleId
import com.linagora.android.linshare.domain.model.sharedspace.SharedSpaceRoleName
import com.linagora.android.linshare.domain.model.sharedspace.VersioningParameter
import com.linagora.android.linshare.domain.model.sharedspace.WorkGroupCopy
import com.linagora.android.linshare.domain.model.sharedspace.WorkGroupDocument
import com.linagora.android.linshare.domain.model.sharedspace.WorkGroupDocumentRevision
import com.linagora.android.linshare.domain.model.sharedspace.WorkGroupFolder
import com.linagora.android.linshare.domain.model.sharedspace.WorkGroupLight
import com.linagora.android.linshare.domain.model.sharedspace.WorkGroupNodeId
import com.linagora.android.linshare.domain.model.sharedspace.WorkGroupNodeType
import com.linagora.android.linshare.domain.model.sharedspace.member.SharedSpaceAccount
import com.linagora.android.linshare.domain.model.sharedspace.member.SharedSpaceAccountId
import com.linagora.android.linshare.domain.model.sharedspace.member.SharedSpaceMember
import com.linagora.android.linshare.domain.model.sharedspace.member.SharedSpaceMemberId
import com.linagora.android.linshare.domain.usecases.sharedspace.activity.GetActivitiesSuccess
import okhttp3.MediaType
import java.util.Date
import java.util.UUID

object SharedSpaceActivityFixtures {
    val CREATE_FOLDER_LOG = WorkGroupFolderAuditLogEntry(
        actor = Account(
            uuid = UUID.fromString("bfe33a5-1cc3-49b8-8442-efb9dffdc989"),
            firstName = "John",
            lastName = "Doe",
            name = "John Doe",
            mail = "user1@linshare.org"
        ),
        auditLogEntryId = AuditLogEntryId(UUID.fromString("6b69a958-6ce8-4fdd-947c-3dd3dc20d755")),
        resourceUuid = UUID.fromString("7e9ad93b-f389-497e-9e1c-b4b73505ee6b"),
        creationDate = Date(1597054319800),
        authUser = Account(
            uuid = UUID.fromString("bfe33a5-1cc3-49b8-8442-efb9dffdc989"),
            firstName = "John",
            lastName = "Doe",
            name = "John Doe",
            mail = "user1@linshare.org"
        ),
        type = AuditLogEntryType.WORKGROUP_FOLDER,
        action = LogAction.CREATE,
        workGroup = WorkGroupLight(
            workGroupNodeId = WorkGroupNodeId(UUID.fromString("85f403d4-b5f8-4759-8d8b-447a8141102f")),
            name = "AT_WorkGroup 202008051904",
            creationDate = Date(1596629091048)
        ),
        resource = WorkGroupFolder(
            workGroupNodeId = WorkGroupNodeId(UUID.fromString("7e9ad93b-f389-497e-9e1c-b4b73505ee6b")),
            parentWorkGroupNodeId = WorkGroupNodeId(UUID.fromString("e91a9480-7812-4de2-b161-77481daf5b1c")),
            creationDate = Date(1597054319799),
            modificationDate = Date(1597054319799),
            lastAuthor = Account(
                uuid = UUID.fromString("bfe33a5-1cc3-49b8-8442-efb9dffdc989"),
                firstName = "John",
                lastName = "Doe",
                name = "John Doe",
                mail = "user1@linshare.org"),
            name = "Abc",
            type = WorkGroupNodeType.FOLDER,
            sharedSpaceId = SharedSpaceId(UUID.fromString("85f403d4-b5f8-4759-8d8b-447a8141102f")),
            description = null,
            treePath = emptyList()
        )
    )

    val CREATE_MEMBER_LOG = SharedSpaceMemberAuditLogEntry(
        actor = Account(
            uuid = UUID.fromString("bfe33a5-1cc3-49b8-8442-efb9dffdc989"),
            firstName = "John",
            lastName = "Doe",
            name = "John Doe",
            mail = "user1@linshare.org"
        ),
        auditLogEntryId = AuditLogEntryId(UUID.fromString("e8386ab4-e5b9-4d2d-b5f1-add50217df8b")),
        resourceUuid = UUID.fromString("0194c0b4-c638-46ee-a590-1699455894bc"),
        creationDate = Date(1597054319800),
        authUser = Account(
            uuid = UUID.fromString("bfe33a5-1cc3-49b8-8442-efb9dffdc989"),
            firstName = "John",
            lastName = "Doe",
            name = "John Doe",
            mail = "user1@linshare.org"
        ),
        type = AuditLogEntryType.WORKGROUP_MEMBER,
        action = LogAction.CREATE,
        workGroup = WorkGroupLight(
            workGroupNodeId = WorkGroupNodeId(UUID.fromString("85f403d4-b5f8-4759-8d8b-447a8141102f")),
            name = "AT_WorkGroup 202008051904",
            creationDate = Date(1596629091048)
        ),
        resource = SharedSpaceMember(
            sharedSpaceMemberId = SharedSpaceMemberId(UUID.fromString("0194c0b4-c638-46ee-a590-1699455894bc")),
            sharedSpaceNode = SharedSpaceNodeNested(
                sharedSpaceId = SharedSpaceId(UUID.fromString("85f403d4-b5f8-4759-8d8b-447a8141102f")),
                nodeType = LinShareNodeType.WORK_GROUP,
                name = "AT_WorkGroup 202008051904",
                creationDate = Date(1596629091044),
                modificationDate = Date(1597036104370),
                role = SharedSpaceRole(
                    sharedSpaceRoleId = SharedSpaceRoleId(UUID.fromString("b206c2ba-37de-491e-8e9c-88ed3be70682")),
                    name = SharedSpaceRoleName.CONTRIBUTOR
                )
            ),
            role = SharedSpaceRole(
                sharedSpaceRoleId = SharedSpaceRoleId(UUID.fromString("b206c2ba-37de-491e-8e9c-88ed3be70682")),
                name = SharedSpaceRoleName.CONTRIBUTOR
            ),
            sharedSpaceAccount = SharedSpaceAccount(
                sharedSpaceAccountId = SharedSpaceAccountId(UUID.fromString("b1980d34-8dc2-4278-9600-07b9515e7839")),
                firstName = "Jane",
                lastName = "Smith",
                name = "Jane Smith",
                mail = "user2@linshare.org"
            ),
            creationDate = Date(1597053351645),
            modificationDate = Date(1597053351645)
        )
    )

    val COPY_DOCUMENT_LOG = WorkGroupDocumentAuditLogEntry(
        actor = Account(
            uuid = UUID.fromString("bfe33a5-1cc3-49b8-8442-efb9dffdc989"),
            firstName = "John",
            lastName = "Doe",
            name = "John Doe",
            mail = "user1@linshare.org"
        ),
        auditLogEntryId = AuditLogEntryId(UUID.fromString("ee2c8e61-db23-41a9-a591-044215111bd6")),
        resourceUuid = UUID.fromString("d2008aea-4bef-4461-b186-3a29536da211"),
        fromResourceUuid = UUID.fromString("3e9ce6f8-4376-4f46-88d1-1ee4a21baba6"),
        creationDate = Date(1597036123168),
        authUser = Account(
            uuid = UUID.fromString("bfe33a5-1cc3-49b8-8442-efb9dffdc989"),
            firstName = "John",
            lastName = "Doe",
            name = "John Doe",
            mail = "user1@linshare.org"
        ),
        type = AuditLogEntryType.WORKGROUP_DOCUMENT,
        action = LogAction.CREATE,
        cause = LogActionCause.COPY,
        workGroup = WorkGroupLight(
            workGroupNodeId = WorkGroupNodeId(UUID.fromString("85f403d4-b5f8-4759-8d8b-447a8141102f")),
            name = "AT_WorkGroup 202008051904",
            creationDate = Date(1596629091048)
        ),
        resource = WorkGroupDocument(
            workGroupNodeId = WorkGroupNodeId(UUID.fromString("7e9ad93b-f389-497e-9e1c-b4b73505ee6b")),
            parentWorkGroupNodeId = WorkGroupNodeId(UUID.fromString("e91a9480-7812-4de2-b161-77481daf5b1c")),
            creationDate = Date(1597042097838),
            modificationDate = Date(1597042097838),
            name = "command.txt_Wed",
            type = WorkGroupNodeType.DOCUMENT,
            sharedSpaceId = SharedSpaceId(UUID.fromString("5716b014-a92f-4d8a-af94-034505c5b74c")),
            description = null,
            treePath = emptyList(),
            hasRevision = false,
            hasThumbnail = false,
            mimeType = MediaType.parse("text/plain")!!,
            size = 192,
            sha256sum = "d6747b1e2516e8ff545a9d454aebe1e89b6f55f720cae0625a2a767259559842",
            lastAuthor = Account(
                uuid = UUID.fromString("bfe33a5-1cc3-49b8-8442-efb9dffdc989"),
                firstName = "John",
                lastName = "Doe",
                name = "John Doe",
                mail = "user1@linshare.org"),
            uploadDate = Date(1597042097838)
        ),
        copiedFrom = WorkGroupCopy(
            workGroupNodeId = WorkGroupNodeId(UUID.fromString("3e9ce6f8-4376-4f46-88d1-1ee4a21baba6")),
            nodeType = WorkGroupNodeType.DOCUMENT,
            kind = SpaceType.SHARED_SPACE,
            name = "command.txt_Wed",
            contextUuid = WorkGroupNodeId(UUID.fromString("03da718f-2f5b-48b7-984d-d765ba27c668"))
        )
    )

    val CREATE_DOCUMENT_REVISION_LOG = WorkGroupDocumentAuditLogEntry(
        actor = Account(
            uuid = UUID.fromString("bfe33a5-1cc3-49b8-8442-efb9dffdc989"),
            firstName = "John",
            lastName = "Doe",
            name = "John Doe",
            mail = "user1@linshare.org"
        ),
        auditLogEntryId = AuditLogEntryId(UUID.fromString("ee2c8e61-db23-41a9-a591-044215111bd6")),
        resourceUuid = UUID.fromString("d2008aea-4bef-4461-b186-3a29536da211"),
        creationDate = Date(1597036123168),
        authUser = Account(
            uuid = UUID.fromString("bfe33a5-1cc3-49b8-8442-efb9dffdc989"),
            firstName = "John",
            lastName = "Doe",
            name = "John Doe",
            mail = "user1@linshare.org"
        ),
        type = AuditLogEntryType.WORKGROUP_DOCUMENT_REVISION,
        action = LogAction.CREATE,
        cause = LogActionCause.COPY,
        workGroup = WorkGroupLight(
            workGroupNodeId = WorkGroupNodeId(UUID.fromString("85f403d4-b5f8-4759-8d8b-447a8141102f")),
            name = "AT_WorkGroup 202008051904",
            creationDate = Date(1596629091048)
        ),
        resource = WorkGroupDocumentRevision(
            workGroupNodeId = WorkGroupNodeId(UUID.fromString("d2008aea-4bef-4461-b186-3a29536da211")),
            parentWorkGroupNodeId = WorkGroupNodeId(UUID.fromString("42daf9a5-d072-4767-8b61-2579a03fe381")),
            creationDate = Date(1597036123165),
            modificationDate = Date(1597036123165),
            name = "image.png",
            lastAuthor = Account(
                uuid = UUID.fromString("bfe33a5-1cc3-49b8-8442-efb9dffdc989"),
                firstName = "John",
                lastName = "Doe",
                name = "John Doe",
                mail = "user1@linshare.org"),
            type = WorkGroupNodeType.DOCUMENT_REVISION,
            sharedSpaceId = SharedSpaceId(UUID.fromString("85f403d4-b5f8-4759-8d8b-447a8141102f")),
            description = null,
            treePath = emptyList(),
            hasRevision = false,
            hasThumbnail = false,
            mimeType = MediaType.parse("image/png")!!,
            size = 2650,
            sha256sum = "767620895e68174c278483c2e97d98313b102cc3f4d8fc6babf6c224377956ea",
            uploadDate = Date(1597036123165)
        )
    )

    val CREATE_WORKGROUP_LOG = SharedSpaceNodeAuditLogEntry(
        actor = Account(
            uuid = UUID.fromString("bfe33a5-1cc3-49b8-8442-efb9dffdc989"),
            firstName = "John",
            lastName = "Doe",
            name = "John Doe",
            mail = "user1@linshare.org"
        ),
        auditLogEntryId = AuditLogEntryId(UUID.fromString("012c8c4d-c2a1-45b6-9ca4-9ea5cf18a68f")),
        resourceUuid = UUID.fromString("85f403d4-b5f8-4759-8d8b-447a8141102f"),
        creationDate = Date(1596629091058),
        authUser = Account(
            uuid = UUID.fromString("bfe33a5-1cc3-49b8-8442-efb9dffdc989"),
            firstName = "John",
            lastName = "Doe",
            name = "John Doe",
            mail = "user1@linshare.org"
        ),
        type = AuditLogEntryType.WORKGROUP,
        action = LogAction.CREATE,
        cause = LogActionCause.COPY,
        resource = SharedSpace(
            sharedSpaceId = SharedSpaceId(UUID.fromString("85f403d4-b5f8-4759-8d8b-447a8141102f")),
            name = "AT_WorkGroup 202008051904",
            creationDate = Date(),
            modificationDate = Date(),
            nodeType = LinShareNodeType.WORK_GROUP,
            quotaId = QuotaId(UUID.fromString("577ffc94-fc6e-4929-b3dc-c61836d1fafe")),
            role = SharedSpaceRole(
                sharedSpaceRoleId = SharedSpaceRoleId(UUID.fromString("b206c2ba-37de-491e-8e9c-88ed3be70682")),
                name = SharedSpaceRoleName.ADMIN
            ),
            versioningParameters = VersioningParameter(true)
        )
    )

    val ALL_ACTIVITIES = listOf(CREATE_FOLDER_LOG, CREATE_MEMBER_LOG, COPY_DOCUMENT_LOG, CREATE_DOCUMENT_REVISION_LOG, CREATE_WORKGROUP_LOG)

    val ALL_ACTIVITIES_STATE = Either.right(GetActivitiesSuccess(ALL_ACTIVITIES))
}
