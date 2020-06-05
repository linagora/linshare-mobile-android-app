package com.linagora.android.linshare.adapter.sharedspace

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.linagora.android.linshare.adapter.diff.SharedSpaceNodeNestedDiffCallback
import com.linagora.android.linshare.databinding.SharedSpaceRowItemBinding
import com.linagora.android.linshare.domain.model.sharedspace.SharedSpaceNodeNested
import com.linagora.android.linshare.view.base.ListItemBehavior

class SharedSpaceAdapter(
    private val itemBehavior: ListItemBehavior<SharedSpaceNodeNested>,
    private val adapterType: AdapterType
) : ListAdapter<SharedSpaceNodeNested, SharedSpaceViewHolder>(SharedSpaceNodeNestedDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SharedSpaceViewHolder {
        return SharedSpaceViewHolder(
            SharedSpaceRowItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ), itemBehavior, adapterType
        )
    }

    override fun onBindViewHolder(holder: SharedSpaceViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

class SharedSpaceViewHolder(
    private val binding: SharedSpaceRowItemBinding,
    private val itemBehavior: ListItemBehavior<SharedSpaceNodeNested>,
    private val adapterType: AdapterType
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(sharedSpaceNodeNested: SharedSpaceNodeNested) {
        binding.adapterType = adapterType
        binding.sharedSpaceNodeNested = sharedSpaceNodeNested
        binding.executePendingBindings()
        binding.listItemBehavior = itemBehavior
    }
}
