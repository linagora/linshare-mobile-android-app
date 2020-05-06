package com.linagora.android.linshare.data.datasource

import com.linagora.android.linshare.domain.model.sharespace.ShareSpaceNodeNested

interface SharedSpaceDataSource {

    suspend fun getSharedSpaces(): List<ShareSpaceNodeNested>
}
