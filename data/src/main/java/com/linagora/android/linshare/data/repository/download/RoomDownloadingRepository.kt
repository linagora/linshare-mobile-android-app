package com.linagora.android.linshare.data.repository.download

import com.linagora.android.linshare.data.database.downloading.DownloadingTaskDao
import com.linagora.android.linshare.data.database.downloading.DownloadingTaskEntity
import com.linagora.android.linshare.data.database.downloading.toDownloadingTask
import com.linagora.android.linshare.data.database.downloading.toEntity
import com.linagora.android.linshare.domain.model.download.DownloadingTask
import com.linagora.android.linshare.domain.repository.download.DownloadingRepository
import javax.inject.Inject

class RoomDownloadingRepository @Inject constructor(
    private val downloadingTaskDao: DownloadingTaskDao
) : DownloadingRepository {

    override suspend fun storeTask(downloadingTask: DownloadingTask) {
        downloadingTaskDao.storeTask(downloadingTask.toEntity())
    }

    override suspend fun getAllTasks(): List<DownloadingTask> {
        return downloadingTaskDao.getAllTasks()
            .map(DownloadingTaskEntity::toDownloadingTask)
    }

    override suspend fun removeTask(downloadingTask: DownloadingTask) {
        downloadingTaskDao.removeTask(downloadingTask.toEntity())
    }
}
