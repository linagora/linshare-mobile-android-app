package com.linagora.android.linshare.model.parceler

import android.os.Parcel
import kotlinx.android.parcel.Parceler
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType

object MediaTypeParceler : Parceler<MediaType> {

    override fun create(parcel: Parcel): MediaType {
        return parcel.readString()!!.toMediaType()
    }

    override fun MediaType.write(parcel: Parcel, flags: Int) {
        parcel.writeString(toString())
    }
}
