package com.linagora.android.linshare.inject

import com.linagora.android.linshare.data.datasource.SharedSpaceDataSource
import com.linagora.android.linshare.data.datasource.network.LinShareSharedSpaceDataSource
import com.linagora.android.linshare.data.repository.sharedspace.SharedSpaceRepositoryImp
import com.linagora.android.linshare.domain.repository.sharedspace.SharedSpaceRepository
import dagger.Binds
import dagger.Module
import javax.inject.Singleton

@Module
abstract class SharedSpaceModule {

    @Binds
    @Singleton
    abstract fun bindLinshareSharedSpaceDataSource(
        linShareSharedSpaceDataSource: LinShareSharedSpaceDataSource
    ): SharedSpaceDataSource

    @Binds
    @Singleton
    abstract fun provideSharedSpaceRepository(sharedSpaceRepositoryImp: SharedSpaceRepositoryImp): SharedSpaceRepository
}
