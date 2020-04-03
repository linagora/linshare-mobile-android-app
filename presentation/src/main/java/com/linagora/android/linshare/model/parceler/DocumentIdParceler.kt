package com.linagora.android.linshare.model.parceler

import android.os.Parcel
import com.linagora.android.linshare.domain.model.document.DocumentId
import kotlinx.android.parcel.Parceler
import java.util.UUID

object DocumentIdParceler : Parceler<DocumentId> {

    override fun create(parcel: Parcel): DocumentId {
        return DocumentId(UUID.fromString(parcel.readString()))
    }

    override fun DocumentId.write(parcel: Parcel, flags: Int) {
        parcel.writeString(uuid.toString())
    }
}
