package com.linagora.android.linshare.data.database.downloading

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.linagora.android.linshare.data.database.LinShareDatabase.Table
import com.linagora.android.linshare.domain.model.download.DownloadType
import com.linagora.android.linshare.domain.model.download.DownloadingTask
import com.linagora.android.linshare.domain.model.download.EnqueuedDownloadId
import okhttp3.MediaType
import java.util.UUID

@Entity(tableName = Table.DOWNLOADING_TASK)
data class DownloadingTaskEntity(

    @PrimaryKey
    @ColumnInfo(name = "enqueuedId")
    val enqueuedDownloadId: EnqueuedDownloadId,

    val downloadDataId: UUID,

    val downloadName: String,

    val downloadSize: Long,

    val mediaType: MediaType,

    val downloadType: DownloadType,

    val sharedSpaceId: UUID? = null
)

fun DownloadingTask.toEntity(): DownloadingTaskEntity {
    return DownloadingTaskEntity(
        enqueuedDownloadId,
        downloadDataId,
        downloadName,
        downloadSize,
        mediaType,
        downloadType
    )
}

fun DownloadingTaskEntity.toDownloadingTask(): DownloadingTask {
    return DownloadingTask(
        enqueuedDownloadId,
        downloadDataId,
        downloadName,
        downloadSize,
        mediaType,
        downloadType
    )
}
