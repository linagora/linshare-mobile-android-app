package com.linagora.android.linshare.inject.sharedspace.role

import com.linagora.android.linshare.data.datasource.network.LinShareSharedSpaceRoleDataSource
import com.linagora.android.linshare.data.datasource.sharedspace.roles.SharedSpaceRoleDataSource
import com.linagora.android.linshare.data.repository.sharedspace.SharedSpaceRoleRepositoryImp
import com.linagora.android.linshare.domain.repository.sharedspace.sharedspaceroles.SharedSpaceRoleRepository
import dagger.Binds
import dagger.Module
import javax.inject.Singleton

@Module
internal abstract class SharedSpaceRoleModule {

    @Binds
    @Singleton
    internal abstract fun bindSharedSpaceRoleRepository(
        sharedSpaceRoleRepositoryImp: SharedSpaceRoleRepositoryImp
    ): SharedSpaceRoleRepository

    @Binds
    @Singleton
    internal abstract fun bindSharedSpaceRoleDataSource(
        linShareSharedSpaceRoleDataSource: LinShareSharedSpaceRoleDataSource
    ): SharedSpaceRoleDataSource
}
