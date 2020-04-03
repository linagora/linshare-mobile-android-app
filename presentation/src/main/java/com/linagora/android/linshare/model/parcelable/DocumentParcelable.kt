package com.linagora.android.linshare.model.parcelable

import android.os.Parcelable
import com.linagora.android.linshare.domain.model.document.Document
import com.linagora.android.linshare.domain.model.document.DocumentId
import com.linagora.android.linshare.model.parceler.DocumentIdParceler
import com.linagora.android.linshare.model.parceler.MediaTypeParceler
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.TypeParceler
import okhttp3.MediaType
import java.util.Date

@Parcelize
@TypeParceler<DocumentId, DocumentIdParceler>()
@TypeParceler<MediaType, MediaTypeParceler>()
class DocumentParcelable(
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
    val shared: Int = 0
) : Parcelable

fun DocumentParcelable.toDocument(): Document {
    return Document(
        documentId,
        description,
        creationDate,
        modificationDate,
        expirationDate,
        ciphered,
        name,
        size,
        type,
        metaData,
        sha256sum,
        hasThumbnail,
        shared
    )
}

fun Document.toParcelable(): DocumentParcelable {
    return DocumentParcelable(
        documentId,
        description,
        creationDate,
        modificationDate,
        expirationDate,
        ciphered,
        name,
        size,
        type,
        metaData,
        sha256sum,
        hasThumbnail,
        shared
    )
}
