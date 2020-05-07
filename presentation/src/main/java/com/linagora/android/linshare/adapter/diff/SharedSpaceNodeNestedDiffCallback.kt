package com.linagora.android.linshare.adapter.diff

import androidx.recyclerview.widget.DiffUtil
import com.linagora.android.linshare.domain.model.sharedspace.SharedSpaceNodeNested

object SharedSpaceNodeNestedDiffCallback : DiffUtil.ItemCallback<SharedSpaceNodeNested>() {

    override fun areItemsTheSame(oldItem: SharedSpaceNodeNested, newItem: SharedSpaceNodeNested): Boolean {
        return oldItem.sharedSpaceId == newItem.sharedSpaceId
    }

    override fun areContentsTheSame(oldItem: SharedSpaceNodeNested, newItem: SharedSpaceNodeNested): Boolean {
        return oldItem == newItem
    }
}
