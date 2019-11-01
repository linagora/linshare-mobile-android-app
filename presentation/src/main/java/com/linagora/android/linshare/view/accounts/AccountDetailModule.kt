package com.linagora.android.linshare.view.accounts

import androidx.lifecycle.ViewModel
import com.linagora.android.linshare.inject.annotation.FragmentScoped
import com.linagora.android.linshare.inject.annotation.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap

@Module
internal abstract class AccountDetailModule {

    @FragmentScoped
    @ContributesAndroidInjector
    internal abstract fun contributeAccountDetailFragment(): AccountDetailsFragment

    @Binds
    @IntoMap
    @ViewModelKey(AccountDetailsViewModel::class)
    abstract fun bindAccountDetailViewModel(accountDetailsViewModel: AccountDetailsViewModel): ViewModel
}
