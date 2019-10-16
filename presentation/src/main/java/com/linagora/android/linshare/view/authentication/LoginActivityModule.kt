package com.linagora.android.linshare.view.authentication

import androidx.lifecycle.ViewModel
import com.linagora.android.linshare.inject.annotation.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class LoginActivityModule {

    @Binds
    @IntoMap
    @ViewModelKey(LoginActivityViewModel::class)
    abstract fun bindViewModel(viewModel: LoginActivityViewModel): ViewModel
}
