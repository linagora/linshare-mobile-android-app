package com.linagora.android.linshare.view.main

import androidx.lifecycle.ViewModel
import com.linagora.android.linshare.inject.annotation.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap

@Module
internal abstract class MainFragmentModule {

    @ContributesAndroidInjector
    internal abstract fun contributeMainFragment(): MainFragment

    @Binds
    @IntoMap
    @ViewModelKey(MainFragmentViewModel::class)
    abstract fun bindViewModel(viewModel: MainFragmentViewModel): ViewModel
}
