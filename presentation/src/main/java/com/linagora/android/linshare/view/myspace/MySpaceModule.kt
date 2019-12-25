package com.linagora.android.linshare.view.myspace

import androidx.lifecycle.ViewModel
import com.linagora.android.linshare.inject.annotation.FragmentScoped
import com.linagora.android.linshare.inject.annotation.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap

@Module
internal abstract class MySpaceModule {

    @FragmentScoped
    @ContributesAndroidInjector
    internal abstract fun contributeMySpaceFragment(): MySpaceFragment

    @Binds
    @IntoMap
    @ViewModelKey(MySpaceViewModel::class)
    internal abstract fun bindViewModel(viewModel: MySpaceViewModel): ViewModel
}
