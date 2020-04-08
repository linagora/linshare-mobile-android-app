package com.linagora.android.linshare.adapter.diff

import androidx.recyclerview.widget.DiffUtil
import com.linagora.android.linshare.domain.model.share.Share

object ReceivedDiffCallback : DiffUtil.ItemCallback<Share>() {

    override fun areItemsTheSame(oldItem: Share, newItem: Share): Boolean {
        return oldItem.shareId == newItem.shareId
    }

    override fun areContentsTheSame(oldItem: Share, newItem: Share): Boolean {
        return oldItem == newItem
    }
}
