package com.linagora.android.linshare.util

import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import android.provider.OpenableColumns
import com.linagora.android.linshare.domain.model.document.DocumentRequest

fun Cursor.getDocumentRequest(uri: Uri): DocumentRequest {
    val fileName = getString(getColumnIndex(OpenableColumns.DISPLAY_NAME))
    val size = getLong(getColumnIndex(OpenableColumns.SIZE))
    val mediaType = getColumnIndex(MediaStore.Images.Media.MIME_TYPE)
        .takeIf { columnIndex -> columnIndex > -1 }
        ?.let { columnIndex -> getString(columnIndex) }
        ?.let { mimeType -> fileName.getMediaType(mimeType) }
        ?: fileName.getMediaTypeFromExtension()
    return DocumentRequest(uri, fileName, size, mediaType)
}
