package com.linagora.android.linshare.domain.model.sharedspace

import java.util.Date

interface WorkGroupNode {
    val workGroupNodeId: WorkGroupNodeId
    val parentWorkGroupNodeId: WorkGroupNodeId
    val creationDate: Date
    val sharedSpaceId: SharedSpaceId
    val modificationDate: Date
    val description: String?
    val name: String
    val treePath: List<TreePath>
}

fun WorkGroupNode.nameContains(query: String): Boolean {
    return name.toLowerCase().contains(query.toLowerCase())
}
