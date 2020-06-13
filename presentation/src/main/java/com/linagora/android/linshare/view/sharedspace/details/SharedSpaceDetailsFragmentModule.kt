package com.linagora.android.linshare.view.sharedspace.details

import androidx.lifecycle.ViewModel
import com.linagora.android.linshare.inject.annotation.ChildFragmentScoped
import com.linagora.android.linshare.inject.annotation.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap

@Module
internal abstract class SharedSpaceDetailsFragmentModule {

    @ChildFragmentScoped
    @ContributesAndroidInjector
    internal abstract fun contributeMemberFragment(): SharedSpaceMembersFragment

    @Binds
    @IntoMap
    @ViewModelKey(SharedSpaceMemberViewModel::class)
    internal abstract fun bindMemberFragmentViewModel(
        sharedSpaceMemberViewModel: SharedSpaceMemberViewModel
    ): ViewModel
}
