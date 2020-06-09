package com.linagora.android.linshare.adapter.member

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.linagora.android.linshare.adapter.diff.SharedSpaceDiffCallback
import com.linagora.android.linshare.databinding.MemberRowItemBinding
import com.linagora.android.linshare.domain.model.sharedspace.member.SharedSpaceMember

class SharedSpaceMemberAdapter() :
    ListAdapter<SharedSpaceMember, SharedSpaceMemberViewHolder>(SharedSpaceDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SharedSpaceMemberViewHolder {
        return SharedSpaceMemberViewHolder(
            MemberRowItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: SharedSpaceMemberViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

class SharedSpaceMemberViewHolder(
    private val binding: MemberRowItemBinding
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(sharedSpaceMember: SharedSpaceMember) {
        binding.member = sharedSpaceMember
        binding.executePendingBindings()
    }
}
