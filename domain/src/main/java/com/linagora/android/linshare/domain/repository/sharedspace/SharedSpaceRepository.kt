package com.linagora.android.linshare.domain.repository.sharedspace

import com.linagora.android.linshare.domain.model.sharedspace.SharedSpaceNodeNested

interface SharedSpaceRepository {

    suspend fun getSharedSpaces(): List<SharedSpaceNodeNested>
}
