package com.linagora.android.linshare.util

import com.linagora.android.linshare.R
import okhttp3.MediaType

object MimeType {
    const val APPLICATION_DEFAULT = "application/octet-stream"
}

fun MediaType.getDrawableIcon(): Int {
    return when (type) {
        "video" -> R.drawable.ic_movie_48px
        "image" -> R.drawable.ic_photo_48px
        "audio" -> R.drawable.ic_audiotrack_48px
        else -> R.drawable.ic_file
    }
}
