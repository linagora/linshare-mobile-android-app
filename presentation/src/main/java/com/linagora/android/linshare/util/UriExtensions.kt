package com.linagora.android.linshare.util

import android.content.Context
import android.net.Uri
import org.apache.commons.io.IOUtils
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

fun Uri.createTempFile(context: Context): File {
    val tempFile = createTempFile(suffix = UUID.randomUUID().toString())
    FileOutputStream(tempFile)
        .use { IOUtils.copy(context.contentResolver.openInputStream(this), it) }
    return tempFile
}
