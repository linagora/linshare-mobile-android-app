package com.linagora.android.linshare.view.main

import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
internal abstract class MainFragmentModule {

    @ContributesAndroidInjector
    internal abstract fun contributeMainFragment(): MainFragment
}
