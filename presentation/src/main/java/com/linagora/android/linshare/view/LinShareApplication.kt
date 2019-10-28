package com.linagora.android.linshare.view

import com.linagora.android.linshare.BuildConfig
import com.linagora.android.linshare.inject.DaggerAppComponent
import dagger.android.AndroidInjector
import dagger.android.DaggerApplication
import timber.log.Timber

class LinShareApplication : DaggerApplication() {

    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }

    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
        return DaggerAppComponent.factory().create(this)
    }
}
