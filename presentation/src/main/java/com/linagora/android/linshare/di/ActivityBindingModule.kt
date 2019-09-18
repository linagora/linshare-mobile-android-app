package com.linagora.android.linshare.di

import com.linagora.android.linshare.di.annotation.ActivityScoped
import com.linagora.android.linshare.view.MainActivity
import com.linagora.android.linshare.view.MainActivityModule

import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
@Suppress("UNUSED")
abstract class ActivityBindingModule {

    @ActivityScoped
    @ContributesAndroidInjector(modules = [MainActivityModule::class])
    internal abstract fun mainActivity(): MainActivity
}