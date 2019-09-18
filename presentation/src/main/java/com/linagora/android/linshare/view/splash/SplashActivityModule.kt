package com.linagora.android.linshare.view.splash

import androidx.lifecycle.ViewModel

import com.linagora.android.linshare.inject.annotation.ViewModelKey

import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class SplashActivityModule {

    @Binds
    @IntoMap
    @ViewModelKey(SplashActivityViewModel::class)
    abstract fun bindViewModel(viewModel: SplashActivityViewModel): ViewModel
}