package com.linagora.android.linshare.view.upload

import androidx.lifecycle.ViewModel
import com.linagora.android.linshare.inject.annotation.FragmentScoped
import com.linagora.android.linshare.inject.annotation.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap

@Module
internal abstract class UploadFragmentModule {

    @FragmentScoped
    @ContributesAndroidInjector
    internal abstract fun contributeUploadFragment(): UploadFragment

    @Binds
    @IntoMap
    @ViewModelKey(UploadFragmentViewModel::class)
    abstract fun bindUploadFragmentViewModel(uploadFragmentViewModel: UploadFragmentViewModel): ViewModel
}
