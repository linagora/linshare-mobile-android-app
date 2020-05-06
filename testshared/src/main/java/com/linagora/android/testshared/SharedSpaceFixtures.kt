package com.linagora.android.testshared

import arrow.core.Either

import com.linagora.android.linshare.domain.model.sharedspace.LinShareNodeType
import com.linagora.android.linshare.domain.model.sharedspace.SharedSpaceId
import com.linagora.android.linshare.domain.model.sharedspace.SharedSpaceNodeNested
import com.linagora.android.linshare.domain.model.sharedspace.SharedSpaceRole
import com.linagora.android.linshare.domain.model.sharedspace.SharedSpaceRoleName
import com.linagora.android.linshare.domain.model.search.QueryString
import com.linagora.android.linshare.domain.usecases.sharedspace.EmptySharedSpaceState
import com.linagora.android.linshare.domain.usecases.sharedspace.NoResultsSearchSharedSpace
import com.linagora.android.linshare.domain.usecases.sharedspace.SharedSpaceViewState
import java.util.Date
import java.util.UUID

object SharedSpaceFixtures {

    val SHARED_SPACE_1 = SharedSpaceNodeNested(
        sharedSpaceId = SharedSpaceId(UUID.fromString("6c0e1f35-89e5-432e-a8d4-17c8d2c3b5fa")),
        role = SharedSpaceRole(UUID.fromString("6c0e1f35-89e5-432e-a8d4-17c8d2c3b5fa"), SharedSpaceRoleName.ADMIN),
        name = "workgroup 1",
        creationDate = Date(1574837876965),
        modificationDate = Date(1574837876965),
        nodeType = LinShareNodeType.WORK_GROUP
    )

    val SHARED_SPACE_2 = SharedSpaceNodeNested(
        sharedSpaceId = SharedSpaceId(UUID.fromString("6c0e1f35-89e5-432e-a8d4-17c8d2c3b5fa")),
        role = SharedSpaceRole(UUID.fromString("6c0e1f35-89e5-432e-a8d4-17c8d2c3b5fa"), SharedSpaceRoleName.READER),
        name = "workgroup 2",
        creationDate = Date(1574837876965),
        modificationDate = Date(1574837876965),
        nodeType = LinShareNodeType.WORK_GROUP
    )

    private val ALL_SHARED_SPACE = SharedSpaceViewState(listOf(SHARED_SPACE_1, SHARED_SPACE_2))

    val ALL_SHARED_SPACE_LIST_VIEW_STATE = Either.Right(ALL_SHARED_SPACE)

    val EMPTY_SHARED_SPACE_LIST_VIEW_STATE = Either.Left(EmptySharedSpaceState)

    val NOT_FOUND_SHARED_SPACE_STATE = Either.left(NoResultsSearchSharedSpace)

    val QUERY_STRING_SHARED_SPACE = QueryString("query_string")
}
