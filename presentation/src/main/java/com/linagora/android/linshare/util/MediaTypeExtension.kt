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
        "text" -> getTextIconBaseOnSubType()
        else -> R.drawable.ic_file
    }
}

fun MediaType.getApplicationIconBaseOnSubType(): Int {
    require(type == "application") { "Type is not application" }
    return when (subtype) {
        "pdf" -> R.drawable.ic_pdf
        "vnd.openxmlformats-officedocument.wordprocessingml.document", "msword", "vnd.oasis.opendocument.text" -> R.drawable.ic_doc
        "vnd.oasis.opendocument.spreadsheet", "vnd.openxmlformats-officedocument.spreadsheetml.sheet", "vnd.ms-excel" -> R.drawable.ic_sheets
        "vnd.ms-powerpoint", "vnd.openxmlformats-officedocument.presentationml.presentation", "octet-stream", "vnd.oasis.opendocument.presentation" -> R.drawable.ic_slide
        else -> R.drawable.ic_file
    }
}

fun MediaType.getTextIconBaseOnSubType(): Int {
    require(type == "text") { "Type is not text" }
    return when (subtype) {
        "plain", "comma-separated-values" -> R.drawable.ic_sheets
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
