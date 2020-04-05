package com.linagora.android.linshare.view.share

import androidx.lifecycle.ViewModel
import com.linagora.android.linshare.inject.annotation.FragmentScoped
import com.linagora.android.linshare.inject.annotation.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap

@Module
internal abstract class ShareFragmentModule {

    @FragmentScoped
    @ContributesAndroidInjector
    internal abstract fun contributeShareFragment(): ShareFragment

    @Binds
    @IntoMap
    @ViewModelKey(ShareFragmentViewModel::class)
    internal abstract fun bindShareFragmentViewModel(shareFragmentViewModel: ShareFragmentViewModel): ViewModel
}
