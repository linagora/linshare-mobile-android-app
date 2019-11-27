package com.linagora.android.linshare.domain.model.document

import okhttp3.MediaType
import java.util.Date
import java.util.UUID

data class Document(
    val uuid: UUID,
    val description: String? = null,
    val creationDate: Date,
    val modificationDate: Date,
    val expirationDate: Date,
    val ciphered: Boolean,
    val name: String,
    val size: Long,
    val type: MediaType,
    val metaData: String? = null,
    val sha256sum: String,
    val hasThumbnail: Boolean,
    val shared: Int = 0
) {
    init {
        require(size >= 0) { "size must not be negative" }
        require(shared >= 0) { "shared must not be negative" }
    }
}
