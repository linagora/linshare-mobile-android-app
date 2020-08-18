/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 *
 * Copyright (C) 2020 LINAGORA
 *
 * This program is free software: you can redistribute it and/or modify it under the
 * terms of the GNU Affero General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later version,
 * provided you comply with the Additional Terms applicable for LinShare software by
 * Linagora pursuant to Section 7 of the GNU Affero General Public License,
 * subsections (b), (c), and (e), pursuant to which you must notably (i) retain the
 * display in the interface of the “LinShare™” trademark/logo, the "Libre & Free" mention,
 * the words “You are using the Free and Open Source version of LinShare™, powered by
 * Linagora © 2009–2020. Contribute to Linshare R&D by subscribing to an Enterprise
 * offer!”. You must also retain the latter notice in all asynchronous messages such as
 * e-mails sent with the Program, (ii) retain all hypertext links between LinShare and
 * http://www.linshare.org, between linagora.com and Linagora, and (iii) refrain from
 * infringing Linagora intellectual property rights over its trademarks and commercial
 * brands. Other Additional Terms apply, see
 * <http://www.linshare.org/licenses/LinShare-License_AfferoGPL-v3.pdf>
 * for more details.
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for
 * more details.
 * You should have received a copy of the GNU Affero General Public License and its
 * applicable Additional Terms for LinShare along with this program. If not, see
 * <http://www.gnu.org/licenses/> for the GNU Affero General Public License version
 *  3 and <http://www.linshare.org/licenses/LinShare-License_AfferoGPL-v3.pdf> for
 *  the Additional Terms applicable to LinShare software.
 */

package com.linagora.android.linshare.view.sharedspace.details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import arrow.core.Either
import com.google.android.material.snackbar.Snackbar
import com.linagora.android.linshare.R
import com.linagora.android.linshare.databinding.FragmentSharedSpaceMemberBinding
import com.linagora.android.linshare.domain.model.OperatorType
import com.linagora.android.linshare.domain.model.sharedspace.SharedSpace
import com.linagora.android.linshare.domain.model.sharedspace.SharedSpaceId
import com.linagora.android.linshare.domain.model.sharedspace.SharedSpaceRole
import com.linagora.android.linshare.domain.model.sharedspace.member.SharedSpaceMember
import com.linagora.android.linshare.domain.usecases.sharedspace.OpenAddMembers
import com.linagora.android.linshare.domain.usecases.sharedspace.member.DeleteMemberSuccess
import com.linagora.android.linshare.domain.usecases.sharedspace.member.EditWorkGroupMemberRoleSuccess
import com.linagora.android.linshare.domain.usecases.sharedspace.member.OnDeleteWorkGroupMemberClick
import com.linagora.android.linshare.domain.usecases.sharedspace.member.OnShowConfirmDeleteMemberClick
import com.linagora.android.linshare.domain.usecases.sharedspace.role.OnSelectRoleClickForUpdate
import com.linagora.android.linshare.domain.usecases.sharedspace.role.OnSelectedRoleForUpdate
import com.linagora.android.linshare.domain.usecases.utils.Success
import com.linagora.android.linshare.model.parcelable.toParcelable
import com.linagora.android.linshare.util.dismissDialogFragmentByTag
import com.linagora.android.linshare.util.filterNetworkViewEvent
import com.linagora.android.linshare.util.getParentViewModel
import com.linagora.android.linshare.util.getViewModel
import com.linagora.android.linshare.view.dialog.ConfirmDeleteWorkGroupMemberDialog
import com.linagora.android.linshare.view.dialog.SelectRoleForUpdateDialog
import com.linagora.android.linshare.view.widget.errorLayout
import dagger.android.support.DaggerFragment
import javax.inject.Inject

class SharedSpaceMembersFragment(private val sharedSpace: SharedSpace) : DaggerFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var sharedSpaceDetailsViewModel: SharedSpaceDetailsViewModel

    private lateinit var sharedSpaceMemberViewModel: SharedSpaceMemberViewModel

    private lateinit var binding: FragmentSharedSpaceMemberBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSharedSpaceMemberBinding.inflate(inflater, container, false)
            .apply { lifecycleOwner = viewLifecycleOwner }
        initViewModel(binding)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpSwipeRefreshLayout()
        sharedSpaceMemberViewModel.initData(sharedSpace.sharedSpaceId)
    }

    private fun initViewModel(binding: FragmentSharedSpaceMemberBinding) {
        sharedSpaceDetailsViewModel = getParentViewModel(viewModelFactory)
        sharedSpaceMemberViewModel = getViewModel(viewModelFactory)
        binding.viewModel = sharedSpaceMemberViewModel
        binding.ownRoleName = sharedSpace.role.name
        binding.internetAvailable = sharedSpaceMemberViewModel.internetAvailable
        observeViewState()
    }

    private fun observeViewState() {
        sharedSpaceDetailsViewModel.viewState.observe(viewLifecycleOwner, Observer { state ->
            state.map { success -> when (success) {
                is Success.ViewEvent -> reactToViewEvent(success)
            } }
        })

        sharedSpaceMemberViewModel.viewState.observe(viewLifecycleOwner, Observer { state ->
            state.map { success -> when (success) {
                is Success.ViewEvent -> reactToViewEventMemberFragment(success)
                is Success.ViewState -> reactToViewStateMemberFragment(success)
            } }
        })
    }

    private fun reactToViewStateMemberFragment(viewState: Success.ViewState) {
        when (viewState) {
            is EditWorkGroupMemberRoleSuccess, is DeleteMemberSuccess -> sharedSpaceMemberViewModel.getAllMembers(sharedSpace.sharedSpaceId)
        }
    }

    private fun reactToViewEvent(viewEvent: Success.ViewEvent) {
        when (viewEvent) {
            is OpenAddMembers -> navigateToAddMembersFragment(viewEvent.sharedSpaceId)
        }
        sharedSpaceDetailsViewModel.dispatchResetState()
    }

    private fun handleCannotExecuteViewEvent(operatorType: OperatorType) {
        val messageId = when (operatorType) {
            is OperatorType.OnSelectedRoleForUpdate -> R.string.can_not_change_member_role_without_network
            else -> R.string.can_not_process_without_network
        }
        dismissSelectRoleForUpdateDialog()
        Snackbar.make(binding.root, getString(messageId), Snackbar.LENGTH_SHORT)
            .errorLayout(requireContext())
            .setAnchorView(binding.anchorSnackbar)
            .show()
        sharedSpaceMemberViewModel.dispatchResetState()
    }

    private fun handleViewEvent(viewEvent: Success.ViewEvent) {
        when (viewEvent) {
            is OnSelectRoleClickForUpdate -> showSelectRoleForUpdateDialog(viewEvent.lastSelectedRole, viewEvent.sharedSpaceMember)
            is OnSelectedRoleForUpdate -> onSelectedRoleForUpdate(viewEvent.selectedRole, viewEvent.sharedSpaceMember)
            is OnShowConfirmDeleteMemberClick -> showConfirmDeleteMemberDialog(viewEvent.member, sharedSpace)
            is OnDeleteWorkGroupMemberClick -> deleteMember(sharedSpace, viewEvent.member)
        }
        sharedSpaceMemberViewModel.dispatchResetState()
    }

    private fun deleteMember(sharedSpace: SharedSpace, sharedSpaceMember: SharedSpaceMember) {
        sharedSpaceMemberViewModel.deleteMember(sharedSpace.sharedSpaceId, sharedSpaceMember.sharedSpaceMemberId)
    }

    private fun showConfirmDeleteMemberDialog(sharedSpaceMember: SharedSpaceMember, sharedSpace: SharedSpace) {
        ConfirmDeleteWorkGroupMemberDialog(
            title = String.format(
                getString(R.string.confirm_remove_member_from_work_group),
                sharedSpaceMember.sharedSpaceAccount.name,
                sharedSpace.name),
            negativeText = getString(R.string.cancel),
            positiveText = getString(R.string.delete),
            onPositiveCallback = { sharedSpaceMemberViewModel.dispatchState(Either.right(OnDeleteWorkGroupMemberClick(sharedSpaceMember))) }
        ).show(childFragmentManager, "confirm_delete_member_dialog")
    }

    private fun reactToViewEventMemberFragment(viewEvent: Success.ViewEvent) {
        when (val filteredViewEvent = viewEvent.filterNetworkViewEvent(sharedSpaceMemberViewModel.internetAvailable.value)) {
            is Success.CancelViewEvent -> handleCannotExecuteViewEvent(filteredViewEvent.operatorType)
            else -> handleViewEvent(filteredViewEvent)
        }
    }

    private fun onSelectedRoleForUpdate(selectedRole: SharedSpaceRole, sharedSpaceMember: SharedSpaceMember) {
        dismissSelectRoleForUpdateDialog()
        sharedSpaceMemberViewModel.editMemberInSharedSpace(selectedRole, sharedSpaceMember)
    }

    private fun dismissSelectRoleForUpdateDialog() {
        childFragmentManager.dismissDialogFragmentByTag(SelectRoleForUpdateDialog.TAG)
    }

    private fun showSelectRoleForUpdateDialog(
        lastSelectedRole: SharedSpaceRole,
        sharedSpaceMember: SharedSpaceMember
    ) {
        dismissSelectRoleForUpdateDialog()
        sharedSpaceMemberViewModel.listSharedSpaceRoles.value?.let {
            SelectRoleForUpdateDialog(
                it,
                lastSelectedRole,
                sharedSpaceMember,
                sharedSpaceMemberViewModel.onSelectRoleForUpdateBehavior
            ).show(childFragmentManager, SelectRoleForUpdateDialog.TAG)
        }
    }

    private fun setUpSwipeRefreshLayout() {
        binding.swipeLayoutMember.setColorSchemeResources(R.color.colorPrimary)
    }

    private fun navigateToAddMembersFragment(sharedSpaceId: SharedSpaceId) {
        val action = SharedSpaceDetailsFragmentDirections
            .actionNavigationSharedSpaceToSharedSpaceAddMemberFragment(sharedSpaceId.toParcelable(), sharedSpace.role.name, sharedSpace.name)
        findNavController().navigate(action)
    }
}
