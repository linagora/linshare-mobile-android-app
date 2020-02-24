package com.linagora.android.linshare.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.linagora.android.linshare.data.database.downloading.DownloadingTaskDao
import com.linagora.android.linshare.data.database.downloading.DownloadingTaskEntity

@Database(
    entities = [ DownloadingTaskEntity::class ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class LinShareDatabase : RoomDatabase() {

    abstract fun downloadingTaskDao(): DownloadingTaskDao

    object Table {
        const val DOWNLOADING_TASK = "downloadingTasks"
    }

    companion object {
        private const val DATABASE_NAME = "linshare-db"

        fun buildDatabase(context: Context): LinShareDatabase {
            return Room.databaseBuilder(context, LinShareDatabase::class.java, DATABASE_NAME)
                .fallbackToDestructiveMigration()
                .build()
        }
    }
}
