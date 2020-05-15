package com.linagora.android.linshare.data.datasource.network

import com.linagora.android.linshare.domain.model.document.DocumentRequest
import com.linagora.android.linshare.domain.model.upload.OnTransfer
import com.linagora.android.linshare.domain.model.upload.TotalBytes
import com.linagora.android.linshare.domain.model.upload.TransferredBytes
import okhttp3.MediaType
import okhttp3.RequestBody
import okio.BufferedSink
import okio.Okio
import java.io.File

class MeasurableUploadRequestBody(
    private val contentType: MediaType,
    private val file: File,
    private val onTransfer: OnTransfer
) : RequestBody() {

    companion object {
        const val SEGMENT_SIZE = 8 * 1024L
    }

    override fun contentType(): MediaType? = contentType

    override fun contentLength(): Long = file.length()

    override fun writeTo(sink: BufferedSink) {
        val total = TotalBytes(contentLength())
        Okio.source(file)
            .use { source ->
                var transfer = 0L
                while (true) {
                    source.read(sink.buffer(), SEGMENT_SIZE)
                        .takeIf { read -> read != -1L }
                        ?.let { read ->
                            sink.emitCompleteSegments()
                            transfer += read
                            onTransfer(TransferredBytes(transfer), total)
                        }
                        ?: break
                }
            }
    }
}

fun DocumentRequest.toMeasureRequestBody(onTransfer: OnTransfer): MeasurableUploadRequestBody {
    return MeasurableUploadRequestBody(
        contentType = mediaType,
        file = file,
        onTransfer = onTransfer
    )
}
