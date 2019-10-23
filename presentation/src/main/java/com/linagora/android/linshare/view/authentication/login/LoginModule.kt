package com.linagora.android.linshare.view.authentication.login

import androidx.lifecycle.ViewModel
import com.linagora.android.linshare.inject.annotation.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap

@Module
internal abstract class LoginModule {

    @ContributesAndroidInjector
    internal abstract fun contributeLoginFragment(): LoginFragment

    @Binds
    @IntoMap
    @ViewModelKey(LoginViewModel::class)
    abstract fun bindViewModel(viewModel: LoginViewModel): ViewModel
}
