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
import com.linagora.android.linshare.domain.model.search.QueryString
import com.linagora.android.linshare.domain.model.sharedspace.CreateWorkGroupRequest
import com.linagora.android.linshare.domain.model.sharedspace.LinShareNodeType
import com.linagora.android.linshare.domain.model.sharedspace.SharedSpaceId
import com.linagora.android.linshare.domain.model.sharedspace.SharedSpaceNodeNested
import com.linagora.android.linshare.domain.model.sharedspace.SharedSpaceRole
import com.linagora.android.linshare.domain.model.sharedspace.SharedSpaceRoleId
import com.linagora.android.linshare.domain.model.sharedspace.SharedSpaceRoleName
import com.linagora.android.linshare.domain.usecases.sharedspace.CreateWorkGroupSuccess
import com.linagora.android.linshare.domain.usecases.sharedspace.EmptySharedSpaceState
import com.linagora.android.linshare.domain.usecases.sharedspace.NoResultsSearchSharedSpace
import com.linagora.android.linshare.domain.usecases.sharedspace.SearchSharedSpaceViewState
import com.linagora.android.linshare.domain.usecases.sharedspace.SharedSpaceViewState
import java.util.Date
import java.util.UUID

object SharedSpaceFixtures {

    val SHARED_SPACE_ID_1 = SharedSpaceId(UUID.fromString("6c0e1f35-89e5-432e-a8d4-17c8d2c3b5fa"))

    val SHARED_SPACE_1 = SharedSpaceNodeNested(
        sharedSpaceId = SHARED_SPACE_ID_1,
        role = SharedSpaceRole(SharedSpaceRoleId(UUID.fromString("6c0e1f35-89e5-432e-a8d4-17c8d2c3b5fa")), SharedSpaceRoleName.ADMIN),
        name = "workgroup 1",
        creationDate = Date(1574837876965),
        modificationDate = Date(1574837876965),
        nodeType = LinShareNodeType.WORK_GROUP
    )

    private val SHARED_SPACE_ID_2 = SharedSpaceId(UUID.fromString("6c0e1f35-89e5-432e-a8d4-17c8d2c3b5fa"))

    val SHARED_SPACE_2 = SharedSpaceNodeNested(
        sharedSpaceId = SHARED_SPACE_ID_2,
        role = SharedSpaceRole(SharedSpaceRoleId(UUID.fromString("6c0e1f35-89e5-432e-a8d4-17c8d2c3b5fa")), SharedSpaceRoleName.READER),
        name = "workgroup 2",
        creationDate = Date(1574837876965),
        modificationDate = Date(1574837876965),
        nodeType = LinShareNodeType.WORK_GROUP
    )

    private val SHARED_SPACE_ID_3 = SharedSpaceId(UUID.fromString("6c0e1f35-89e5-432e-a8d4-17c8d2c3b5fa"))

    val SHARED_SPACE_3 = SharedSpaceNodeNested(
        sharedSpaceId = SHARED_SPACE_ID_3,
        role = SharedSpaceRole(SharedSpaceRoleId(UUID.fromString("6c0e1f35-89e5-432e-a8d4-17c8d2c3b5fa")), SharedSpaceRoleName.CONTRIBUTOR),
        name = "workgroup 2",
        creationDate = Date(1574837876965),
        modificationDate = Date(1574837876965),
        nodeType = LinShareNodeType.WORK_GROUP
    )

    private val SHARED_SPACE_ID_4 = SharedSpaceId(UUID.fromString("6c0e1f35-89e5-432e-a8d4-17c8d2c3b5fa"))

    val SHARED_SPACE_4 = SharedSpaceNodeNested(
        sharedSpaceId = SHARED_SPACE_ID_4,
        role = SharedSpaceRole(SharedSpaceRoleId(UUID.fromString("6c0e1f35-89e5-432e-a8d4-17c8d2c3b5fa")), SharedSpaceRoleName.WRITER),
        name = "workgroup 2",
        creationDate = Date(1574837876965),
        modificationDate = Date(1574837876965),
        nodeType = LinShareNodeType.WORK_GROUP
    )

    private val SHARED_SPACE_ID_5 = SharedSpaceId(UUID.fromString("6c0e1f35-89e5-432e-a8d4-17c8d2c3b5fa"))

    val SHARED_SPACE_5 = SharedSpaceNodeNested(
        sharedSpaceId = SHARED_SPACE_ID_5,
        role = SharedSpaceRole(SharedSpaceRoleId(UUID.fromString("6c0e1f35-89e5-432e-a8d4-17c8d2c3b5fa")), SharedSpaceRoleName.WRITER),
        name = "workgroup 5",
        creationDate = Date(1574837876965),
        modificationDate = Date(1574837876965),
        nodeType = LinShareNodeType.WORK_GROUP
    )

    private val SHARED_SPACE_ID_6 = SharedSpaceId(UUID.fromString("6c0e1f35-89e5-432e-a8d4-17c8d2c3b5fa"))

    val SHARED_SPACE_6 = SharedSpaceNodeNested(
        sharedSpaceId = SHARED_SPACE_ID_6,
        role = SharedSpaceRole(SharedSpaceRoleId(UUID.fromString("6c0e1f35-89e5-432e-a8d4-17c8d2c3b5fa")), SharedSpaceRoleName.WRITER),
        name = "workgroup 6",
        creationDate = Date(1574837876966),
        modificationDate = Date(1574837876966),
        nodeType = LinShareNodeType.WORK_GROUP
    )

    private val ALL_SHARED_SPACE = SharedSpaceViewState(listOf(SHARED_SPACE_1, SHARED_SPACE_2))

    private val SEARCH_SHARED_SPACE = SearchSharedSpaceViewState(listOf(SHARED_SPACE_1, SHARED_SPACE_2))

    val ALL_SHARED_SPACE_LIST_VIEW_STATE = Either.Right(ALL_SHARED_SPACE)

    val SEARCH_SHARED_SPACE_LIST_VIEW_STATE = Either.Right(SEARCH_SHARED_SPACE)

    val EMPTY_SHARED_SPACE_LIST_VIEW_STATE = Either.Left(EmptySharedSpaceState)

    val NOT_FOUND_SHARED_SPACE_STATE = Either.left(NoResultsSearchSharedSpace)

    val QUERY_STRING_SHARED_SPACE = QueryString("query_string")

    val ADMIN_ROLE = SharedSpaceRole(
        sharedSpaceRoleId = SharedSpaceRoleId(UUID.fromString("234be74d-2966-41c1-9dee-e47c8c63c14e")),
        name = SharedSpaceRoleName.ADMIN
    )

    val WRITER_ROLE = SharedSpaceRole(
        sharedSpaceRoleId = SharedSpaceRoleId(UUID.fromString("8839654d-cb33-4633-bf3f-f9e805f97f84")),
        name = SharedSpaceRoleName.WRITER
    )

    val CONTRIBUTOR_ROLE = SharedSpaceRole(
        sharedSpaceRoleId = SharedSpaceRoleId(UUID.fromString("b206c2ba-37de-491e-8e9c-88ed3be70682")),
        name = SharedSpaceRoleName.CONTRIBUTOR
    )

    val READER_ROLE = SharedSpaceRole(
        sharedSpaceRoleId = SharedSpaceRoleId(UUID.fromString("4ccbed61-71da-42a0-a513-92211953ac95")),
        name = SharedSpaceRoleName.READER
    )

    val CREATE_WORK_GROUP_REQUEST = CreateWorkGroupRequest("WorkGroup 1", LinShareNodeType.WORK_GROUP)

    val CREATE_WORK_GROUP_SUCCESS_STATE = Either.Right(CreateWorkGroupSuccess(SharedSpaceDocumentFixtures.SHARED_SPACE_1))
}
