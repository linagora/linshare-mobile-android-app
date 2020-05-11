package com.linagora.android.linshare.adapter.sharedspace

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.linagora.android.linshare.adapter.diff.WorkGroupNodeDiffCallback
import com.linagora.android.linshare.databinding.SharedSpaceDocumentRowItemBinding
import com.linagora.android.linshare.domain.model.sharedspace.WorkGroupNode
import com.linagora.android.linshare.view.base.ListItemBehavior

class SharedSpaceDocumentAdapter constructor(
    private val listItemBehavior: ListItemBehavior<WorkGroupNode>
) : ListAdapter<WorkGroupNode, SharedSpaceDocumentViewHolder>(WorkGroupNodeDiffCallback) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SharedSpaceDocumentViewHolder {
        return SharedSpaceDocumentViewHolder(
            SharedSpaceDocumentRowItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ),
            listItemBehavior
        )
    }

    override fun onBindViewHolder(holder: SharedSpaceDocumentViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

class SharedSpaceDocumentViewHolder(
    private val binding: SharedSpaceDocumentRowItemBinding,
    private val listItemBehavior: ListItemBehavior<WorkGroupNode>
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(workGroupNode: WorkGroupNode) {
        binding.node = workGroupNode
        binding.listItemBehavior = listItemBehavior
        binding.executePendingBindings()
    }
}
