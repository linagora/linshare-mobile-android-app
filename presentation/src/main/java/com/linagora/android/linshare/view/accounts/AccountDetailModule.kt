package com.linagora.android.linshare.view.accounts

import com.linagora.android.linshare.inject.annotation.FragmentScoped
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
internal abstract class AccountDetailModule {

    @FragmentScoped
    @ContributesAndroidInjector
    internal abstract fun contributeAccountDetailFragment(): AccountDetailsFragment
}
