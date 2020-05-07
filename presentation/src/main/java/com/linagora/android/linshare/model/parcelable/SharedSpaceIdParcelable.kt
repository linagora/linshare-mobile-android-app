package com.linagora.android.linshare.model.parcelable

import android.os.Parcelable
import com.linagora.android.linshare.domain.model.sharedspace.SharedSpaceId
import com.linagora.android.linshare.model.parceler.UUIDParceler
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.TypeParceler
import java.util.UUID

@Parcelize
@TypeParceler<UUID, UUIDParceler>()
class SharedSpaceIdParcelable(val uuid: UUID) : Parcelable

fun SharedSpaceIdParcelable.toSharedSpaceId(): SharedSpaceId {
    return SharedSpaceId(uuid)
}

fun SharedSpaceId.toParcelable(): SharedSpaceIdParcelable {
    return SharedSpaceIdParcelable(uuid)
}
