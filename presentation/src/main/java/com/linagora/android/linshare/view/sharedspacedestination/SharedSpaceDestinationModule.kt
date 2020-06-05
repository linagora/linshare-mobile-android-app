package com.linagora.android.linshare.view.sharedspacedestination

import androidx.lifecycle.ViewModel
import com.linagora.android.linshare.inject.annotation.FragmentScoped
import com.linagora.android.linshare.inject.annotation.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap

@Module
internal abstract class SharedSpaceDestinationModule {

    @FragmentScoped
    @ContributesAndroidInjector
    internal abstract fun contributeSharedSpaceDestinationFragment(): SharedSpaceDestinationFragment

    @Binds
    @IntoMap
    @ViewModelKey(SharedSpaceDestinationViewModel::class)
    abstract fun bindSharedSpaceDestinationViewModel(sharedSpaceDestinationViewModel: SharedSpaceDestinationViewModel): ViewModel
}
