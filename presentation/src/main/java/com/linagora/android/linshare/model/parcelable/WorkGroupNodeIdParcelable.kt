package com.linagora.android.linshare.model.parcelable

import android.os.Parcelable
import com.linagora.android.linshare.domain.model.sharedspace.WorkGroupNodeId
import com.linagora.android.linshare.model.parceler.UUIDParceler
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.TypeParceler
import java.util.UUID

@Parcelize
@TypeParceler<UUID, UUIDParceler>()
class WorkGroupNodeIdParcelable(val uuid: UUID) : Parcelable

fun WorkGroupNodeIdParcelable.toWorkGroupNodeId(): WorkGroupNodeId {
    return WorkGroupNodeId(uuid)
}

fun WorkGroupNodeId.toParcelable(): WorkGroupNodeIdParcelable {
    return WorkGroupNodeIdParcelable(uuid)
}
