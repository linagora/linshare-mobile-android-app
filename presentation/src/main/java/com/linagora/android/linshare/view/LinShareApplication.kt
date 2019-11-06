package com.linagora.android.linshare.view

import com.jakewharton.threetenabp.AndroidThreeTen
import com.linagora.android.linshare.BuildConfig
import com.linagora.android.linshare.inject.DaggerAppComponent
import dagger.android.AndroidInjector
import dagger.android.DaggerApplication
import timber.log.Timber

class LinShareApplication : DaggerApplication() {

    override fun onCreate() {
        super.onCreate()

        AndroidThreeTen.init(this)

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }

    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
        return DaggerAppComponent.factory().create(this)
    }
}
