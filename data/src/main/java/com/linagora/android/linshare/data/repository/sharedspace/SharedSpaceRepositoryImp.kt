package com.linagora.android.linshare.data.repository.sharedspace

import com.linagora.android.linshare.data.datasource.SharedSpaceDataSource
import com.linagora.android.linshare.domain.model.sharedspace.SharedSpaceNodeNested
import com.linagora.android.linshare.domain.repository.sharedspace.SharedSpaceRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SharedSpaceRepositoryImp @Inject constructor(
    private val sharedSpaceDataSource: SharedSpaceDataSource
) : SharedSpaceRepository {
    override suspend fun getSharedSpaces(): List<SharedSpaceNodeNested> {
        return sharedSpaceDataSource.getSharedSpaces()
    }
}
