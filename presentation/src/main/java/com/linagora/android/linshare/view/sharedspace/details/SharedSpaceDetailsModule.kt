package com.linagora.android.linshare.view.sharedspace.details

import androidx.lifecycle.ViewModel
import com.linagora.android.linshare.inject.annotation.FragmentScoped
import com.linagora.android.linshare.inject.annotation.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap

@Module
internal abstract class SharedSpaceDetailsModule {
    @FragmentScoped
    @ContributesAndroidInjector(modules = [SharedSpaceDetailsFragmentModule::class])
    internal abstract fun contributeSharedSpaceDetailsFragment(): SharedSpaceDetailsFragment

    @Binds
    @IntoMap
    @ViewModelKey(SharedSpaceDetailsViewModel::class)
    internal abstract fun bindSharedSpaceDetailsViewModel(
        sharedSpaceDetailsViewModel: SharedSpaceDetailsViewModel
    ): ViewModel
}
