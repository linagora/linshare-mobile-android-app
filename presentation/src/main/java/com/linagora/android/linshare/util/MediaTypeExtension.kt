package com.linagora.android.linshare.util

import com.linagora.android.linshare.R
import okhttp3.MediaType

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
