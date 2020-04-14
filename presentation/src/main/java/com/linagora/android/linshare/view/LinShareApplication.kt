package com.linagora.android.linshare.view

import android.util.Log
import androidx.work.Configuration
import androidx.work.WorkerFactory
import com.jakewharton.threetenabp.AndroidThreeTen
import com.linagora.android.linshare.BuildConfig
import com.linagora.android.linshare.inject.DaggerAppComponent
import dagger.android.AndroidInjector
import dagger.android.DaggerApplication
import timber.log.Timber
import javax.inject.Inject

open class LinShareApplication : DaggerApplication(), Configuration.Provider {

    @Inject lateinit var workerFactory: WorkerFactory

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

    override fun getWorkManagerConfiguration(): Configuration {
        return Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .setMinimumLoggingLevel(Log.INFO)
            .build()
    }
}
