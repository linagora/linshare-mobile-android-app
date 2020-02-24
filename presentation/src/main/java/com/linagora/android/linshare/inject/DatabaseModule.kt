package com.linagora.android.linshare.inject

import android.content.Context
import com.linagora.android.linshare.data.database.LinShareDatabase
import com.linagora.android.linshare.data.database.downloading.DownloadingTaskDao
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(context: Context): LinShareDatabase {
        return LinShareDatabase.buildDatabase(context)
    }

    @Provides
    @Singleton
    fun provideDownloadingTaskDao(database: LinShareDatabase): DownloadingTaskDao {
        return database.downloadingTaskDao()
    }
}
