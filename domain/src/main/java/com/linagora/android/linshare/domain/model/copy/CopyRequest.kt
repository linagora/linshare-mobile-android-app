package com.linagora.android.linshare.domain.model.copy

import com.linagora.android.linshare.domain.model.share.Share
import java.util.UUID

data class CopyRequest(val uuid: UUID, val kind: SpaceType)

fun Share.toCopyRequest(spaceType: SpaceType): CopyRequest {
    return CopyRequest(shareId.uuid, spaceType)
}
