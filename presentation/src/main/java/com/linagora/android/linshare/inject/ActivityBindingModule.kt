package com.linagora.android.linshare.inject

import com.linagora.android.linshare.inject.annotation.ActivityScoped
import com.linagora.android.linshare.view.MainActivity
import com.linagora.android.linshare.view.MainActivityModule
import com.linagora.android.linshare.view.authentication.LoginActivity
import com.linagora.android.linshare.view.authentication.LoginActivityModule
import com.linagora.android.linshare.view.authentication.wizard.WizardModule
import com.linagora.android.linshare.view.splash.SplashActivity
import com.linagora.android.linshare.view.splash.SplashActivityModule
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
        WizardModule::class
    ])
    internal abstract fun mainActivity(): MainActivity

    @ActivityScoped
    @ContributesAndroidInjector(modules = [LoginActivityModule::class])
    internal abstract fun loginActivity(): LoginActivity
}
