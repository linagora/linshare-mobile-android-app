package com.linagora.android.linshare.model.parcelable

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class SharedSpaceDestinationInfo(
    val sharedSpaceIdParcelable: SharedSpaceIdParcelable,
    val sharedSpaceName: String,
    val sharedSpaceQuotaId: QuotaIdParcelable
) : Parcelable
