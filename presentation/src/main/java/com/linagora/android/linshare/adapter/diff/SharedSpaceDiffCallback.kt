package com.linagora.android.linshare.adapter.diff

import androidx.recyclerview.widget.DiffUtil
import com.linagora.android.linshare.domain.model.sharedspace.member.SharedSpaceMember

object SharedSpaceDiffCallback : DiffUtil.ItemCallback<SharedSpaceMember>() {
    override fun areItemsTheSame(oldItem: SharedSpaceMember, newItem: SharedSpaceMember): Boolean {
        return oldItem.sharedSpaceMemberId == newItem.sharedSpaceMemberId
    }

    override fun areContentsTheSame(oldItem: SharedSpaceMember, newItem: SharedSpaceMember): Boolean {
        return oldItem == newItem
    }
}
