package com.linagora.android.linshare.data.database.downloading

import androidx.annotation.VisibleForTesting
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.linagora.android.linshare.data.database.LinShareDatabase.Table

@Dao
interface DownloadingTaskDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun storeTask(task: DownloadingTaskEntity)

    @Query("SELECT * from ${Table.DOWNLOADING_TASK}")
    suspend fun getAllTasks(): List<DownloadingTaskEntity>

    @Delete
    suspend fun removeTask(task: DownloadingTaskEntity)

    @Query("DELETE from ${Table.DOWNLOADING_TASK}")
    @VisibleForTesting
    suspend fun removeAll()
}
