package com.linagora.android.linshare.adapter.sharedspace

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.linagora.android.linshare.adapter.diff.ShareSpaceNodeNestedDiffCallback
import com.linagora.android.linshare.databinding.SharedSpaceRowItemBinding
import com.linagora.android.linshare.domain.model.sharespace.ShareSpaceNodeNested

class SharedSpaceAdapter : ListAdapter<ShareSpaceNodeNested, SharedSpaceViewHolder>(ShareSpaceNodeNestedDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SharedSpaceViewHolder {
        return SharedSpaceViewHolder(SharedSpaceRowItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: SharedSpaceViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

class SharedSpaceViewHolder(
    private val binding: SharedSpaceRowItemBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(shareSpaceNodeNested: ShareSpaceNodeNested) {
        binding.shareSpaceNodeNested = shareSpaceNodeNested
        binding.executePendingBindings()
    }
}
