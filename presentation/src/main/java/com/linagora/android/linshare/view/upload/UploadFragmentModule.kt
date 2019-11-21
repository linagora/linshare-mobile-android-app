package com.linagora.android.linshare.view.upload

import com.linagora.android.linshare.inject.annotation.FragmentScoped
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
internal abstract class UploadFragmentModule {

    @FragmentScoped
    @ContributesAndroidInjector
    internal abstract fun contributeUploadFragment(): UploadFragment
}
