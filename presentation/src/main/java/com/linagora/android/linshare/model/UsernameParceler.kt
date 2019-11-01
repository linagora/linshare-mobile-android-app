package com.linagora.android.linshare.model

import android.os.Parcel
import com.linagora.android.linshare.domain.model.Username
import kotlinx.android.parcel.Parceler

object UsernameParceler : Parceler<Username> {

    override fun create(parcel: Parcel): Username {
        return Username(parcel.readString()!!)
    }

    override fun Username.write(parcel: Parcel, flags: Int) {
        parcel.writeString(username)
    }
}
