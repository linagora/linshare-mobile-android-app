package com.linagora.android.testshared

import arrow.core.Either
import com.linagora.android.linshare.domain.model.search.QueryString
import com.linagora.android.linshare.domain.model.sharedspace.LinShareNodeType
import com.linagora.android.linshare.domain.model.sharedspace.SharedSpaceId
import com.linagora.android.linshare.domain.model.sharedspace.SharedSpaceNodeNested
import com.linagora.android.linshare.domain.model.sharedspace.SharedSpaceRole
import com.linagora.android.linshare.domain.model.sharedspace.SharedSpaceRoleName
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
        role = SharedSpaceRole(UUID.fromString("6c0e1f35-89e5-432e-a8d4-17c8d2c3b5fa"), SharedSpaceRoleName.ADMIN),
        name = "workgroup 1",
        creationDate = Date(1574837876965),
        modificationDate = Date(1574837876965),
        nodeType = LinShareNodeType.WORK_GROUP
    )

    val SHARED_SPACE_ID_2 = SharedSpaceId(UUID.fromString("6c0e1f35-89e5-432e-a8d4-17c8d2c3b5fa"))

    val SHARED_SPACE_2 = SharedSpaceNodeNested(
        sharedSpaceId = SHARED_SPACE_ID_2,
        role = SharedSpaceRole(UUID.fromString("6c0e1f35-89e5-432e-a8d4-17c8d2c3b5fa"), SharedSpaceRoleName.READER),
        name = "workgroup 2",
        creationDate = Date(1574837876965),
        modificationDate = Date(1574837876965),
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
        uuid = UUID.fromString("234be74d-2966-41c1-9dee-e47c8c63c14e"),
        name = SharedSpaceRoleName.ADMIN
    )

    val WRITER_ROLE = SharedSpaceRole(
        uuid = UUID.fromString("8839654d-cb33-4633-bf3f-f9e805f97f84"),
        name = SharedSpaceRoleName.WRITER
    )

    val CONTRIBUTOR_ROLE = SharedSpaceRole(
        uuid = UUID.fromString("b206c2ba-37de-491e-8e9c-88ed3be70682"),
        name = SharedSpaceRoleName.CONTRIBUTOR
    )

    val READER_ROLE = SharedSpaceRole(
        uuid = UUID.fromString("4ccbed61-71da-42a0-a513-92211953ac95"),
        name = SharedSpaceRoleName.READER
    )
}
