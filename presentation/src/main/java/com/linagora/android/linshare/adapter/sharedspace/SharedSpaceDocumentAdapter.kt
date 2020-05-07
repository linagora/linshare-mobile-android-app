package com.linagora.android.linshare.adapter.sharedspace

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.linagora.android.linshare.adapter.diff.WorkGroupNodeDiffCallback
import com.linagora.android.linshare.databinding.SharedSpaceDocumentRowItemBinding
import com.linagora.android.linshare.domain.model.sharedspace.WorkGroupNode

class SharedSpaceDocumentAdapter :
    ListAdapter<WorkGroupNode, SharedSpaceDocumentViewHolder>(WorkGroupNodeDiffCallback) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SharedSpaceDocumentViewHolder {
        return SharedSpaceDocumentViewHolder(SharedSpaceDocumentRowItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        ))
    }

    override fun onBindViewHolder(holder: SharedSpaceDocumentViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

class SharedSpaceDocumentViewHolder(
    private val binding: SharedSpaceDocumentRowItemBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(workGroupNode: WorkGroupNode) {
        binding.node = workGroupNode
        binding.executePendingBindings()
    }
}
