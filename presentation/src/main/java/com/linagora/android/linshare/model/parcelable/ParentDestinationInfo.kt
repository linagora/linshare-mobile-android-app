package com.linagora.android.linshare.model.parcelable

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class ParentDestinationInfo(
    val parentNodeId: WorkGroupNodeIdParcelable,
    val parentNodeName: String
) : Parcelable
