package com.linagora.android.linshare.view.sharedspace.details

import androidx.lifecycle.ViewModel
import com.linagora.android.linshare.inject.annotation.FragmentScoped
import com.linagora.android.linshare.inject.annotation.ViewModelKey
import com.linagora.android.linshare.view.dialog.SelectRoleDialog
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap

@Module
internal abstract class SharedSpaceAddMembersModule {
    @FragmentScoped
    @ContributesAndroidInjector
    internal abstract fun contributeSharedSpaceAddMembersFragment(): SharedSpaceAddMemberFragment

    @Binds
    @IntoMap
    @ViewModelKey(SharedSpaceAddMemberViewModel::class)
    internal abstract fun bindSharedSpaceAddMemberViewModel(
        sharedSpaceAddMemberViewModel: SharedSpaceAddMemberViewModel
    ): ViewModel

    @FragmentScoped
    @ContributesAndroidInjector
    internal abstract fun contributeSelectRoleDialog(): SelectRoleDialog
}
