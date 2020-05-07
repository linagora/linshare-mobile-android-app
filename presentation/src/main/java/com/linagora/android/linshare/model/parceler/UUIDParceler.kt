package com.linagora.android.linshare.model.parceler

import android.os.Parcel
import kotlinx.android.parcel.Parceler
import java.util.UUID

object UUIDParceler : Parceler<UUID> {

    override fun create(parcel: Parcel): UUID {
        return UUID.fromString(parcel.readString())
    }

    override fun UUID.write(parcel: Parcel, flags: Int) {
        parcel.writeString(this.toString())
    }
}
