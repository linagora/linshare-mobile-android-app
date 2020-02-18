package com.linagora.android.linshare.util

import android.webkit.MimeTypeMap
import com.linagora.android.linshare.R
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType

object MimeType {
    const val APPLICATION_DEFAULT = "application/octet-stream"
}

fun MediaType.getDrawableIcon(): Int {
    return when (type) {
        "video" -> R.drawable.ic_movie
        "image" -> R.drawable.ic_picture
        "audio" -> R.drawable.ic_audiotrack_48px
        "application" -> getApplicationIconBaseOnSubType()
        else -> R.drawable.ic_file
    }
}

fun MediaType.getApplicationIconBaseOnSubType(): Int {
    return when (subtype) {
        "pdf" -> R.drawable.ic_pdf
        else -> R.drawable.ic_file
    }
}

fun String.getMediaType(defaultMimeType: String): MediaType {
    return runCatching { defaultMimeType.toMediaType() }
        .getOrElse { this.getMediaTypeFromExtension() }
}

fun String.getMediaTypeFromExtension(): MediaType {
    return MimeTypeMap.getFileExtensionFromUrl(this)
        ?.let { extensions -> MimeTypeMap.getSingleton().getMimeTypeFromExtension(extensions) }
        ?.let { mediaType -> mediaType.toMediaType() }
        ?: MimeType.APPLICATION_DEFAULT.toMediaType()
}
