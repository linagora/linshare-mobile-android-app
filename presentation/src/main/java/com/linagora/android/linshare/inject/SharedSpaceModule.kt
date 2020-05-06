package com.linagora.android.linshare.inject

import com.linagora.android.linshare.data.datasource.SharedSpaceDataSource
import com.linagora.android.linshare.data.datasource.network.LinShareSharedSpaceDataSource
import com.linagora.android.linshare.data.repository.sharespace.SharedSpaceRepositoryImp
import com.linagora.android.linshare.domain.repository.sharespace.SharedSpaceRepository
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
    abstract fun provideShareSpaceRepository(sharedSpaceRepositoryImp: SharedSpaceRepositoryImp): SharedSpaceRepository
}
