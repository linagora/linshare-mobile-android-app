package com.linagora.android.linshare.model.parcelable

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class UploadDestinationInfo(
    val sharedSpaceDestinationInfo: SharedSpaceDestinationInfo,
    val parentDestinationInfo: ParentDestinationInfo
) : Parcelable
