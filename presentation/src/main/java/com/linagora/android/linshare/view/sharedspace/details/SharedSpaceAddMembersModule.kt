package com.linagora.android.linshare.view.sharedspace.details

import com.linagora.android.linshare.inject.annotation.FragmentScoped
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
internal abstract class SharedSpaceAddMembersModule {
    @FragmentScoped
    @ContributesAndroidInjector
    internal abstract fun contributeSharedSpaceAddMembersFragment(): SharedSpaceAddMemberFragment
}
