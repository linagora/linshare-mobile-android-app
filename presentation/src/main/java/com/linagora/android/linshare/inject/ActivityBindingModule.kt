package com.linagora.android.linshare.inject

import com.linagora.android.linshare.inject.annotation.ActivityScoped
import com.linagora.android.linshare.view.MainActivity
import com.linagora.android.linshare.view.MainActivityModule
import com.linagora.android.linshare.view.accounts.AccountDetailModule
import com.linagora.android.linshare.view.authentication.login.LoginModule
import com.linagora.android.linshare.view.authentication.wizard.WizardModule
import com.linagora.android.linshare.view.main.MainFragmentModule
import com.linagora.android.linshare.view.myspace.MySpaceModule
import com.linagora.android.linshare.view.receivedshares.ReceivedSharesModule
import com.linagora.android.linshare.view.search.SearchModule
import com.linagora.android.linshare.view.share.ShareFragmentModule
import com.linagora.android.linshare.view.sharedspace.SharedSpacePresentationModule
import com.linagora.android.linshare.view.sharedspace.details.SharedSpaceDetailsModule
import com.linagora.android.linshare.view.sharedspacedestination.SharedSpaceDestinationModule
import com.linagora.android.linshare.view.sharedspacedocument.SharedSpaceDocumentPresentationModule
import com.linagora.android.linshare.view.sharedspacedocumentdestination.SharedSpaceDocumentDestinantionModule
import com.linagora.android.linshare.view.splash.SplashActivity
import com.linagora.android.linshare.view.splash.SplashActivityModule
import com.linagora.android.linshare.view.upload.UploadFragmentModule
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
@Suppress("UNUSED")
abstract class ActivityBindingModule {

    @ActivityScoped
    @ContributesAndroidInjector(modules = [SplashActivityModule::class])
    internal abstract fun splashActivity(): SplashActivity

    @ActivityScoped
    @ContributesAndroidInjector(modules = [
        MainActivityModule::class,
        MainFragmentModule::class,
        WizardModule::class,
        LoginModule::class,
        AccountDetailModule::class,
        UploadFragmentModule::class,
        MySpaceModule::class,
        SearchModule::class,
        ShareFragmentModule::class,
        ReceivedSharesModule::class,
        SharedSpacePresentationModule::class,
        SharedSpaceDocumentPresentationModule::class,
        SharedSpaceDetailsModule::class,
        SharedSpaceDestinationModule::class,
        SharedSpaceDocumentDestinantionModule::class
    ])
    internal abstract fun mainActivity(): MainActivity
}
