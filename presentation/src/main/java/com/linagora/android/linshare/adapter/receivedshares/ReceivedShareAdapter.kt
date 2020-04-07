package com.linagora.android.linshare.adapter.receivedshares

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.linagora.android.linshare.adapter.diff.ReceivedDiffCallback
import com.linagora.android.linshare.databinding.ReceivedShareItemBinding
import com.linagora.android.linshare.domain.model.share.Share
import com.linagora.android.linshare.view.base.ListItemBehavior

class ReceivedAdapter(
    private val itemBehavior: ListItemBehavior<Share>
) : ListAdapter<Share, ReceivedViewHolder>(ReceivedDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReceivedViewHolder {
        return ReceivedViewHolder(
            ReceivedShareItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ),
            itemBehavior
        )
    }

    override fun onBindViewHolder(holder: ReceivedViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

class ReceivedViewHolder(
    private val binding: ReceivedShareItemBinding,
    private val itemBehavior: ListItemBehavior<Share>
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(share: Share) {
        binding.share = share
        binding.itemBehavior = itemBehavior
        binding.executePendingBindings()
    }
}
