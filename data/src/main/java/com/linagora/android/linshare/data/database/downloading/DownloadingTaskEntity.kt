package com.linagora.android.linshare.data.database.downloading

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.linagora.android.linshare.data.database.LinShareDatabase.Table
import com.linagora.android.linshare.domain.model.document.DocumentId
import com.linagora.android.linshare.domain.model.download.DownloadingTask
import com.linagora.android.linshare.domain.model.download.EnqueuedDownloadId
import okhttp3.MediaType
import java.util.UUID

@Entity(tableName = Table.DOWNLOADING_TASK)
data class DownloadingTaskEntity(

    @PrimaryKey
    @ColumnInfo(name = "enqueuedId")
    val enqueuedDownloadId: EnqueuedDownloadId,

    val documentUUID: UUID,

    val documentName: String,

    val documentSize: Long,

    val mediaType: MediaType
)

fun DownloadingTask.toEntity(): DownloadingTaskEntity {
    return DownloadingTaskEntity(
        enqueuedDownloadId,
        documentId.uuid,
        documentName,
        documentSize,
        mediaType
    )
}

fun DownloadingTaskEntity.toDownloadingTask(): DownloadingTask {
    return DownloadingTask(
        enqueuedDownloadId,
        DocumentId(documentUUID),
        documentName,
        documentSize,
        mediaType
    )
}
