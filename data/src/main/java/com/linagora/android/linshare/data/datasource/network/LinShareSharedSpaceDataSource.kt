package com.linagora.android.linshare.data.datasource.network

import com.linagora.android.linshare.data.api.LinshareApi
import com.linagora.android.linshare.data.datasource.SharedSpaceDataSource
import com.linagora.android.linshare.domain.model.sharespace.ShareSpaceNodeNested
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LinShareSharedSpaceDataSource @Inject constructor(
    private val linshareApi: LinshareApi
) : SharedSpaceDataSource {

    override suspend fun getSharedSpaces(): List<ShareSpaceNodeNested> {
        return linshareApi.getSharedSpaces().sortedByDescending { it.modificationDate }
    }
}
