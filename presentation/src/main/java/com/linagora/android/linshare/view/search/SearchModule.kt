package com.linagora.android.linshare.view.search

import com.linagora.android.linshare.inject.annotation.FragmentScoped
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
internal abstract class SearchModule {

    @FragmentScoped
    @ContributesAndroidInjector
    internal abstract fun contributeSearchFragment(): SearchFragment
}
