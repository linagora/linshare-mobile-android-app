package com.linagora.android.linshare.domain.model.download

import okhttp3.MediaType
import java.util.UUID

data class DownloadingTask(
    val enqueuedDownloadId: EnqueuedDownloadId,
    val downloadDataId: UUID,
    val downloadName: String,
    val downloadSize: Long,
    val mediaType: MediaType,
    val downloadType: DownloadType
)
