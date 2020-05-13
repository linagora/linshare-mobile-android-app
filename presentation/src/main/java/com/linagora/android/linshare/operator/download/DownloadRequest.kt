package com.linagora.android.linshare.operator.download

import com.linagora.android.linshare.domain.model.document.Document
import com.linagora.android.linshare.domain.model.download.DownloadType
import com.linagora.android.linshare.domain.model.share.Share
import com.linagora.android.linshare.domain.model.sharedspace.WorkGroupDocument
import com.linagora.android.linshare.domain.network.Endpoint
import com.linagora.android.linshare.domain.network.ServicePath
import okhttp3.MediaType
import java.util.UUID

data class DownloadRequest(
    val downloadName: String,
    val downloadSize: Long,
    val downloadMediaType: MediaType,
    val downloadDataId: UUID,
    val downloadType: DownloadType,
    val sharedSpaceId: UUID? = null
)

fun Document.toDownloadRequest(): DownloadRequest {
    return DownloadRequest(
        downloadName = name,
        downloadSize = size,
        downloadMediaType = type,
        downloadDataId = documentId.uuid,
        downloadType = DownloadType.DOCUMENT
    )
}

fun Share.toDownloadRequest(): DownloadRequest {
    return DownloadRequest(
        downloadName = name,
        downloadSize = size,
        downloadMediaType = type,
        downloadDataId = shareId.uuid,
        downloadType = DownloadType.SHARE
    )
}

fun WorkGroupDocument.toDownloadRequest(): DownloadRequest {
    return DownloadRequest(
        downloadName = name,
        downloadSize = size,
        downloadMediaType = mimeType,
        downloadDataId = workGroupNodeId.uuid,
        downloadType = DownloadType.SHARED_SPACE_DOCUMENT,
        sharedSpaceId = sharedSpaceId.uuid
    )
}

fun DownloadRequest.toServicePath(): ServicePath {
    val path = when (downloadType) {
        DownloadType.DOCUMENT -> Endpoint.DOCUMENT_PATH
        DownloadType.SHARE -> Endpoint.RECEIVED_SHARES_PATH
        DownloadType.SHARED_SPACE_DOCUMENT -> {
            require(sharedSpaceId != null) { "Can not download shared space document without sharedSpaceId" }
            "${Endpoint.SHARED_SPACE_PATH}/$sharedSpaceId/${Endpoint.NODES}"
        }
    }
    return ServicePath("$path/$downloadDataId/${Endpoint.DOWNLOAD}")
}
