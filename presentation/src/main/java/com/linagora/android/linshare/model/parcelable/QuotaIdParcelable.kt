package com.linagora.android.linshare.model.parcelable

import android.os.Parcelable
import com.linagora.android.linshare.domain.model.quota.QuotaId
import com.linagora.android.linshare.model.parceler.UUIDParceler
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.TypeParceler
import java.util.UUID

@Parcelize
@TypeParceler<UUID, UUIDParceler>()
class QuotaIdParcelable(val uuid: UUID) : Parcelable

fun QuotaIdParcelable.toQuotaId(): QuotaId {
    return QuotaId(uuid)
}

fun QuotaId.toParcelable(): QuotaIdParcelable {
    return QuotaIdParcelable(uuid)
}
