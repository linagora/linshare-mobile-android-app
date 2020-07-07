/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 *
 * Copyright (C) 2020 LINAGORA
 *
 * This program is free software: you can redistribute it and/or modify it under the
 * terms of the GNU Affero General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later version,
 * provided you comply with the Additional Terms applicable for LinShare software by
 * Linagora pursuant to Section 7 of the GNU Affero General Public License,
 * subsections (b), (c), and (e), pursuant to which you must notably (i) retain the
 * display in the interface of the “LinShare™” trademark/logo, the "Libre & Free" mention,
 * the words “You are using the Free and Open Source version of LinShare™, powered by
 * Linagora © 2009–2020. Contribute to Linshare R&D by subscribing to an Enterprise
 * offer!”. You must also retain the latter notice in all asynchronous messages such as
 * e-mails sent with the Program, (ii) retain all hypertext links between LinShare and
 * http://www.linshare.org, between linagora.com and Linagora, and (iii) refrain from
 * infringing Linagora intellectual property rights over its trademarks and commercial
 * brands. Other Additional Terms apply, see
 * <http://www.linshare.org/licenses/LinShare-License_AfferoGPL-v3.pdf>
 * for more details.
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for
 * more details.
 * You should have received a copy of the GNU Affero General Public License and its
 * applicable Additional Terms for LinShare along with this program. If not, see
 * <http://www.gnu.org/licenses/> for the GNU Affero General Public License version
 *  3 and <http://www.linshare.org/licenses/LinShare-License_AfferoGPL-v3.pdf> for
 *  the Additional Terms applicable to LinShare software.
 */

package com.linagora.android.linshare.util

import android.webkit.MimeTypeMap
import com.linagora.android.linshare.R
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType

object MimeType {
    const val APPLICATION_DEFAULT = "application/octet-stream"

    const val ALL_TYPE = "*/*"
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
