package com.linagora.android.linshare.domain.repository.sharespace

import com.linagora.android.linshare.domain.model.sharespace.ShareSpaceNodeNested

interface SharedSpaceRepository {

    suspend fun getSharedSpaces(): List<ShareSpaceNodeNested>
}
