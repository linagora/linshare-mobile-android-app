package com.linagora.android.testshared

import arrow.core.Either
import com.linagora.android.linshare.domain.model.sharespace.LinShareNodeType
import com.linagora.android.linshare.domain.model.sharespace.ShareSpaceId
import com.linagora.android.linshare.domain.model.sharespace.ShareSpaceNodeNested
import com.linagora.android.linshare.domain.model.sharespace.SharedSpaceRole
import com.linagora.android.linshare.domain.model.sharespace.SharedSpaceRoleName
import com.linagora.android.linshare.domain.usecases.sharedspace.EmptySharedSpaceState
import com.linagora.android.linshare.domain.usecases.sharedspace.SharedSpaceViewState
import java.util.Date
import java.util.UUID

object SharedSpaceFixtures {

    val SHARED_SPACE_1 = ShareSpaceNodeNested(
        shareSpaceId = ShareSpaceId(UUID.fromString("6c0e1f35-89e5-432e-a8d4-17c8d2c3b5fa")),
        role = SharedSpaceRole(UUID.fromString("6c0e1f35-89e5-432e-a8d4-17c8d2c3b5fa"), SharedSpaceRoleName.ADMIN),
        name = "workgroup 1",
        creationDate = Date(1574837876965),
        modificationDate = Date(1574837876965),
        nodeType = LinShareNodeType.WORK_GROUP
    )

    val SHARED_SPACE_2 = ShareSpaceNodeNested(
        shareSpaceId = ShareSpaceId(UUID.fromString("6c0e1f35-89e5-432e-a8d4-17c8d2c3b5fa")),
        role = SharedSpaceRole(UUID.fromString("6c0e1f35-89e5-432e-a8d4-17c8d2c3b5fa"), SharedSpaceRoleName.READER),
        name = "workgroup 2",
        creationDate = Date(1574837876965),
        modificationDate = Date(1574837876965),
        nodeType = LinShareNodeType.WORK_GROUP
    )

    private val ALL_SHARED_SPACE = SharedSpaceViewState(listOf(SHARED_SPACE_1, SHARED_SPACE_2))

    val ALL_SHARED_SPACE_LIST_VIEW_STATE = Either.Right(ALL_SHARED_SPACE)

    val EMPTY_SHARED_SPACE_LIST_VIEW_STATE = Either.Right(EmptySharedSpaceState)
}