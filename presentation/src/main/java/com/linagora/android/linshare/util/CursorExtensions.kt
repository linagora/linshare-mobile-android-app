package com.linagora.android.linshare.util

import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import android.provider.OpenableColumns
import com.linagora.android.linshare.model.upload.UploadDocumentRequest

fun Cursor.getUploadDocumentRequest(uri: Uri): UploadDocumentRequest {
    val fileName = getString(getColumnIndex(OpenableColumns.DISPLAY_NAME))
    val fileSize = getLong(getColumnIndex(OpenableColumns.SIZE))
    val mediaType = getColumnIndex(MediaStore.Images.Media.MIME_TYPE)
        .takeIf { columnIndex -> columnIndex > -1 }
        ?.let { columnIndex -> getString(columnIndex) }
        ?.let { mimeType -> fileName.getMediaType(mimeType) }
        ?: fileName.getMediaTypeFromExtension()
    return UploadDocumentRequest(uri, fileSize, fileName, mediaType)
}
