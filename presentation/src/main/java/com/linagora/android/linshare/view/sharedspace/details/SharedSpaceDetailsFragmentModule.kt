package com.linagora.android.linshare.view.sharedspace.details

import com.linagora.android.linshare.inject.annotation.ChildFragmentScoped
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
internal abstract class SharedSpaceDetailsFragmentModule {

    @ChildFragmentScoped
    @ContributesAndroidInjector
    internal abstract fun contributeMemberFragment(): SharedSpaceMembersFragment
}
