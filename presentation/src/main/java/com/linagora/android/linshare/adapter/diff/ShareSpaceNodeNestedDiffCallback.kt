package com.linagora.android.linshare.adapter.diff

import androidx.recyclerview.widget.DiffUtil
import com.linagora.android.linshare.domain.model.sharespace.ShareSpaceNodeNested

object ShareSpaceNodeNestedDiffCallback : DiffUtil.ItemCallback<ShareSpaceNodeNested>() {

    override fun areItemsTheSame(oldItem: ShareSpaceNodeNested, newItem: ShareSpaceNodeNested): Boolean {
        return oldItem.shareSpaceId == newItem.shareSpaceId
    }

    override fun areContentsTheSame(oldItem: ShareSpaceNodeNested, newItem: ShareSpaceNodeNested): Boolean {
        return oldItem == newItem
    }
}
