package com.linagora.android.linshare.adapter.diff

import androidx.recyclerview.widget.DiffUtil
import com.linagora.android.linshare.domain.model.sharedspace.SharedSpaceRole

object RoleDiffCallback : DiffUtil.ItemCallback<SharedSpaceRole>() {
    override fun areItemsTheSame(oldItem: SharedSpaceRole, newItem: SharedSpaceRole): Boolean {
        return oldItem.sharedSpaceRoleId == newItem.sharedSpaceRoleId
    }

    override fun areContentsTheSame(oldItem: SharedSpaceRole, newItem: SharedSpaceRole): Boolean {
        return oldItem == newItem
    }
}
