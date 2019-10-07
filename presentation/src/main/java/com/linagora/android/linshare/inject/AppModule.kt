package com.linagora.android.linshare.inject

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import com.linagora.android.linshare.view.LinShareApplication
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class AppModule {

    @Provides
    fun provideContext(application: LinShareApplication): Context {
        return application.applicationContext
    }

    @Provides
    @Singleton
    fun provideSharedPreferences(application: LinShareApplication): SharedPreferences {
        return PreferenceManager.getDefaultSharedPreferences(application)
    }
}
