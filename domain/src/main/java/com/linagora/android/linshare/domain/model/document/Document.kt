package com.linagora.android.linshare.domain.model.document

import com.google.gson.annotations.SerializedName
import com.linagora.android.linshare.domain.model.share.Share
import okhttp3.MediaType
import java.util.Date

data class Document(
    @SerializedName("uuid")
    val documentId: DocumentId,
    val description: String? = null,
    val creationDate: Date,
    val modificationDate: Date,
    val expirationDate: Date? = null,
    val ciphered: Boolean,
    val name: String,
    val size: Long,
    val type: MediaType,
    val metaData: String? = null,
    val sha256sum: String,
    val hasThumbnail: Boolean,
    val shared: Int = 0,
    val shares: List<Share>? = emptyList()
) {
    init {
        require(size >= 0) { "size must not be negative" }
        require(shared >= 0) { "shared must not be negative" }
    }
}

fun Document.nameContains(query: String): Boolean {
    return name.toLowerCase().contains(query.toLowerCase())
}
