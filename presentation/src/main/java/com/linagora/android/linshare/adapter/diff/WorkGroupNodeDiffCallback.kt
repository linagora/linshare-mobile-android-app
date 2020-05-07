package com.linagora.android.linshare.adapter.diff

import androidx.recyclerview.widget.DiffUtil
import com.linagora.android.linshare.domain.model.sharedspace.WorkGroupNode

object WorkGroupNodeDiffCallback : DiffUtil.ItemCallback<WorkGroupNode>() {
    override fun areItemsTheSame(oldItem: WorkGroupNode, newItem: WorkGroupNode): Boolean {
        return oldItem.workGroupNodeId == newItem.workGroupNodeId
    }

    override fun areContentsTheSame(oldItem: WorkGroupNode, newItem: WorkGroupNode): Boolean {
        return oldItem.equals(newItem)
    }
}
