package com.linagora.android.linshare.data.repository.sharespace

import com.linagora.android.linshare.data.datasource.SharedSpaceDataSource
import com.linagora.android.linshare.domain.model.sharespace.ShareSpaceNodeNested
import com.linagora.android.linshare.domain.repository.sharespace.SharedSpaceRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SharedSpaceRepositoryImp @Inject constructor(
    private val sharedSpaceDataSource: SharedSpaceDataSource
) : SharedSpaceRepository {
    override suspend fun getSharedSpaces(): List<ShareSpaceNodeNested> {
        return sharedSpaceDataSource.getSharedSpaces()
    }
}
