package com.linagora.android.linshare.domain.model.download

import com.linagora.android.linshare.domain.model.document.DocumentId
import okhttp3.MediaType

data class DownloadingTask(
    val enqueuedDownloadId: EnqueuedDownloadId,
    val documentId: DocumentId,
    val documentName: String,
    val documentSize: Long,
    val mediaType: MediaType
)
