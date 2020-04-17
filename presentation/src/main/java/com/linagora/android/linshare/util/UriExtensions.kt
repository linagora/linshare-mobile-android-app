package com.linagora.android.linshare.util

import android.content.Context
import android.net.Uri
import com.linagora.android.linshare.domain.usecases.upload.NotEnoughDeviceStorageException
import com.linagora.android.linshare.util.AndroidUtils.isDeviceNotEnoughStorage
import org.apache.commons.io.IOUtils
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

fun Uri.createTempFile(context: Context): File {
    val tempFile = createTempFile(suffix = UUID.randomUUID().toString())
    FileOutputStream(tempFile).use {
        runCatching { IOUtils.copy(context.contentResolver.openInputStream(this), it) }
            .getOrElse(::haltCreateFileError)
    }
    return tempFile
}

private fun haltCreateFileError(throwable: Throwable) {
    throwable.takeIf { isDeviceNotEnoughStorage(it) }
        ?.let { throw NotEnoughDeviceStorageException }
        ?: throw throwable
}
