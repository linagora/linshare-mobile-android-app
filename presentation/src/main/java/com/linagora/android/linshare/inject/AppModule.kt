package com.linagora.android.linshare.inject

import android.content.Context
import com.linagora.android.linshare.view.LinShareApplication
import dagger.Module
import dagger.Provides

@Module
class AppModule {

    @Provides
    fun provideContext(application: LinShareApplication): Context {
        return application.applicationContext
    }
}
