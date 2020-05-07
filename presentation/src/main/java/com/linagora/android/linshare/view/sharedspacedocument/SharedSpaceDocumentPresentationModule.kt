package com.linagora.android.linshare.view.sharedspacedocument

import androidx.lifecycle.ViewModel
import com.linagora.android.linshare.inject.annotation.FragmentScoped
import com.linagora.android.linshare.inject.annotation.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap

@Module
internal abstract class SharedSpaceDocumentPresentationModule {
    @FragmentScoped
    @ContributesAndroidInjector
    internal abstract fun contributeSharedSpaceDocumentFragment(): SharedSpaceDocumentFragment

    @Binds
    @IntoMap
    @ViewModelKey(SharedSpaceDocumentViewModel::class)
    abstract fun bindSharedSpaceDocumentViewModel(sharedSpaceDocumentViewModel: SharedSpaceDocumentViewModel): ViewModel
}
