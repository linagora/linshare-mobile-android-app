package com.linagora.android.linshare.view.sharedspace

import androidx.lifecycle.ViewModel
import com.linagora.android.linshare.inject.annotation.FragmentScoped
import com.linagora.android.linshare.inject.annotation.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap

@Module
internal abstract class SharedSpacePresentationModule {

    @FragmentScoped
    @ContributesAndroidInjector
    internal abstract fun contributeSharedSpaceFragment(): SharedSpaceFragment

    @Binds
    @IntoMap
    @ViewModelKey(SharedSpaceViewModel::class)
    abstract fun bindSharedSpaceViewModel(receivedSharesViewModel: SharedSpaceViewModel): ViewModel

    @FragmentScoped
    @ContributesAndroidInjector
    internal abstract fun contributeSharedSpaceContextMenuDialog(): SharedSpaceContextMenuDialog
}
