package com.linagora.android.linshare.domain.repository.download

import com.linagora.android.linshare.domain.model.download.DownloadingTask

interface DownloadingRepository {

    suspend fun storeTask(downloadingTask: DownloadingTask)

    suspend fun getAllTasks(): List<DownloadingTask>

    suspend fun removeTask(downloadingTask: DownloadingTask)
}
