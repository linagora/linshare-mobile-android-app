package com.linagora.android.linshare.view

import androidx.lifecycle.ViewModel
import com.linagora.android.linshare.inject.annotation.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
@Suppress("UNUSED")
abstract class MainActivityModule {

    @Binds
    @IntoMap
    @ViewModelKey(MainActivityViewModel::class)
    abstract fun bindViewModel(viewModel: MainActivityViewModel): ViewModel
}
