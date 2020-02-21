package com.linagora.android.linshare.domain.model.download

import okhttp3.MediaType
import java.util.UUID

data class DownloadingTask(
    val enqueuedDownloadId: EnqueuedDownloadId,
    val documentUUID: UUID,
    val documentName: String,
    val documentSize: Long,
    val mediaType: MediaType
)
