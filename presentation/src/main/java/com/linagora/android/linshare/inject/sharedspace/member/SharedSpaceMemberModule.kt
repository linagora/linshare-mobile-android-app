package com.linagora.android.linshare.inject.sharedspace.member

import com.linagora.android.linshare.data.datasource.network.LinShareSharedSpaceMemberDataSource
import com.linagora.android.linshare.data.datasource.sharedspace.member.SharedSpaceMemberDataSource
import com.linagora.android.linshare.data.repository.sharedspace.SharedSpaceMemberRepositoryImp
import com.linagora.android.linshare.domain.repository.sharedspace.SharedSpaceMemberRepository
import dagger.Binds
import dagger.Module
import javax.inject.Singleton

@Module
abstract class SharedSpaceMemberModule {
    @Binds
    @Singleton
    internal abstract fun bindSharedSpaceMemberRepository(
        sharedSpaceMemberRepository: SharedSpaceMemberRepositoryImp
    ): SharedSpaceMemberRepository

    @Binds
    @Singleton
    internal abstract fun bindLinShareSharedSpaceMemberDataSource(
        linShareSharedSpaceMemberDataSource: LinShareSharedSpaceMemberDataSource
    ): SharedSpaceMemberDataSource
}
