package com.linagora.android.linshare.view.receivedshares

import androidx.lifecycle.ViewModel
import com.linagora.android.linshare.inject.annotation.FragmentScoped
import com.linagora.android.linshare.inject.annotation.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap

@Module
internal abstract class ReceivedSharesModule {

    @FragmentScoped
    @ContributesAndroidInjector
    internal abstract fun contributeReceivedSharesFragment(): ReceivedSharesFragment

    @Binds
    @IntoMap
    @ViewModelKey(ReceivedSharesViewModel::class)
    abstract fun bindAccountReceivedSharesModel(receivedSharesViewModel: ReceivedSharesViewModel): ViewModel

    @FragmentScoped
    @ContributesAndroidInjector
    internal abstract fun contributeReceivedShareContextMenuDialog(): ReceivedShareContextMenuDialog
}
