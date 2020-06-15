package com.linagora.android.linshare.view.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.linagora.android.linshare.adapter.role.RolesAdapter
import com.linagora.android.linshare.databinding.DialogSelectRolesBinding
import com.linagora.android.linshare.domain.model.sharedspace.SharedSpaceRole
import com.linagora.android.linshare.util.getParentViewModel
import com.linagora.android.linshare.view.sharedspace.details.SharedSpaceAddMemberViewModel
import javax.inject.Inject

class SelectRoleDialog(
    private val roles: List<SharedSpaceRole>,
    private val lastSelectedRole: SharedSpaceRole
) : DaggerBottomSheetDialogFragment() {

    companion object {
        const val TAG = "selectRole"
    }

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var sharedSpaceAddMemberViewModel: SharedSpaceAddMemberViewModel

    private lateinit var binding: DialogSelectRolesBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DialogSelectRolesBinding.inflate(inflater, container, false)
        initView()
        return binding.root
    }

    private fun initView() {
        sharedSpaceAddMemberViewModel = getParentViewModel(viewModelFactory)
        binding.roleList.adapter = initListRole()
        binding.executePendingBindings()
    }

    private fun initListRole(): RolesAdapter {
        val adapter = RolesAdapter(lastSelectedRole, sharedSpaceAddMemberViewModel.onSelectRoleBehavior)
        adapter.submitList(roles)
        return adapter
    }
}
