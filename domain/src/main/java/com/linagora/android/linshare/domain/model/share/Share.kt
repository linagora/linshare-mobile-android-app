package com.linagora.android.linshare.domain.model.share

import com.google.gson.annotations.SerializedName
import com.linagora.android.linshare.domain.model.GenericUser
import com.linagora.android.linshare.domain.model.User
import com.linagora.android.linshare.domain.model.document.Document
import okhttp3.MediaType
import java.util.Date

data class Share(
    @SerializedName("uuid")
    val shareId: ShareId,
    val name: String,
    val creationDate: Date,
    val modificationDate: Date,
    val expirationDate: Date,
    val downloaded: Long,
    val document: Document,
    val recipient: GenericUser,
    val description: String,
    val type: MediaType,
    val size: Long,
    val message: String,
    val hasThumbnail: Boolean,
    val ciphered: Boolean,
    val sender: User
) {
    init {
        require(size >= 0) { "size must not be negative" }
        require(downloaded >= 0) { "downloaded must not be negative" }
    }
}
