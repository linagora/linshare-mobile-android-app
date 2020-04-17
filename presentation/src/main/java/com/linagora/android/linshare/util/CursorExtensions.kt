package com.linagora.android.linshare.util

import android.database.Cursor
import android.provider.MediaStore
import android.provider.OpenableColumns
import com.linagora.android.linshare.domain.model.document.DocumentRequest
import java.io.File

fun Cursor.getDocumentRequest(file: File): DocumentRequest {
    val fileName = getString(getColumnIndex(OpenableColumns.DISPLAY_NAME))
    val mediaType = getColumnIndex(MediaStore.Images.Media.MIME_TYPE)
        .takeIf { columnIndex -> columnIndex > -1 }
        ?.let { columnIndex -> getString(columnIndex) }
        ?.let { mimeType -> fileName.getMediaType(mimeType) }
        ?: fileName.getMediaTypeFromExtension()
    return DocumentRequest(file, fileName, mediaType)
}
