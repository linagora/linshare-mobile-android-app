package com.linagora.android.linshare.adapter.role

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.linagora.android.linshare.adapter.diff.RoleDiffCallback
import com.linagora.android.linshare.databinding.SelectRoleItemBinding
import com.linagora.android.linshare.domain.model.sharedspace.SharedSpaceRole
import com.linagora.android.linshare.view.base.OnSelectRoles

class RolesAdapter(
    private val lastSelectedRole: SharedSpaceRole,
    private val onSelectedRoles: OnSelectRoles
) : ListAdapter<SharedSpaceRole, RoleViewHolder>(RoleDiffCallback) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RoleViewHolder {
        return RoleViewHolder(
            SelectRoleItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ),
            onSelectedRoles
        )
    }

    override fun onBindViewHolder(holder: RoleViewHolder, position: Int) {
        return holder.bind(getItem(position), lastSelectedRole)
    }
}

class RoleViewHolder(
    private val binding: SelectRoleItemBinding,
    private val onSelectedRoles: OnSelectRoles
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(sharedSpaceRole: SharedSpaceRole, lastSelectedRole: SharedSpaceRole) {
        binding.thisRole = sharedSpaceRole
        binding.lastSelectedRole = lastSelectedRole
        binding.onSelectRoleBehavior = onSelectedRoles
        binding.executePendingBindings()
    }
}
