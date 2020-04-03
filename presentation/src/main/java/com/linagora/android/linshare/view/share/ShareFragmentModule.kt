package com.linagora.android.linshare.view.share

import com.linagora.android.linshare.inject.annotation.FragmentScoped
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
internal abstract class ShareFragmentModule {

    @FragmentScoped
    @ContributesAndroidInjector
    internal abstract fun contributeShareFragment(): ShareFragment
}
