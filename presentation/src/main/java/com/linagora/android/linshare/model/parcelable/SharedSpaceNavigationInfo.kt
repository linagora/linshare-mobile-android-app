package com.linagora.android.linshare.model.parcelable

import android.os.Parcelable
import com.linagora.android.linshare.domain.model.sharedspace.WorkGroupNodeId
import com.linagora.android.linshare.view.Navigation
import kotlinx.android.parcel.Parcelize

@Parcelize
class SharedSpaceNavigationInfo(
    val sharedSpaceIdParcelable: SharedSpaceIdParcelable,
    val fileType: Navigation.FileType,
    val nodeIdParcelable: WorkGroupNodeIdParcelable
) : Parcelable

fun SharedSpaceNavigationInfo.getParentNodeId(): WorkGroupNodeId? {
    return fileType.takeIf { it == Navigation.FileType.NORMAL }
        ?.let { nodeIdParcelable.toWorkGroupNodeId() }
}
