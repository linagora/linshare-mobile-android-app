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
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import arrow.core.Either
import com.google.android.material.snackbar.Snackbar
import com.linagora.android.linshare.R
import com.linagora.android.linshare.databinding.FragmentAddMemberBinding
import com.linagora.android.linshare.domain.model.OperatorType
import com.linagora.android.linshare.domain.model.autocomplete.AutoCompletePattern
import com.linagora.android.linshare.domain.model.autocomplete.AutoCompleteResult
import com.linagora.android.linshare.domain.model.autocomplete.AutoCompleteType
import com.linagora.android.linshare.domain.model.autocomplete.ThreadMemberAutoCompleteRequest
import com.linagora.android.linshare.domain.model.sharedspace.SharedSpaceId
import com.linagora.android.linshare.domain.model.sharedspace.SharedSpaceRole
import com.linagora.android.linshare.domain.model.sharedspace.member.AddMemberRequest
import com.linagora.android.linshare.domain.model.sharedspace.member.SharedSpaceAccountId
import com.linagora.android.linshare.domain.model.sharedspace.member.SharedSpaceMember
import com.linagora.android.linshare.domain.usecases.sharedspace.member.AddMemberFailed
import com.linagora.android.linshare.domain.usecases.sharedspace.member.AddMemberSuccess
import com.linagora.android.linshare.domain.usecases.sharedspace.member.DeleteMemberSuccess
import com.linagora.android.linshare.domain.usecases.sharedspace.member.EditWorkGroupMemberRoleSuccess
import com.linagora.android.linshare.domain.usecases.sharedspace.member.OnDeleteWorkGroupMemberClick
import com.linagora.android.linshare.domain.usecases.sharedspace.member.OnShowConfirmDeleteMemberClick
import com.linagora.android.linshare.domain.usecases.sharedspace.role.OnSelectRoleClick
import com.linagora.android.linshare.domain.usecases.sharedspace.role.OnSelectRoleClickForUpdate
import com.linagora.android.linshare.domain.usecases.sharedspace.role.OnSelectedRole
import com.linagora.android.linshare.domain.usecases.sharedspace.role.OnSelectedRoleForUpdate
import com.linagora.android.linshare.domain.usecases.utils.Failure
import com.linagora.android.linshare.domain.usecases.utils.Success
import com.linagora.android.linshare.model.parcelable.toSharedSpaceId
import com.linagora.android.linshare.util.binding.bindingDefaultSelectedRole
import com.linagora.android.linshare.util.binding.bindingRoles
import com.linagora.android.linshare.util.binding.clearFocus
import com.linagora.android.linshare.util.binding.initView
import com.linagora.android.linshare.util.binding.onSelectedMember
import com.linagora.android.linshare.util.binding.queryAfterTextChange
import com.linagora.android.linshare.util.binding.showKeyBoard
import com.linagora.android.linshare.util.dismissDialogFragmentByTag
import com.linagora.android.linshare.util.filterNetworkViewEvent
import com.linagora.android.linshare.util.getViewModel
import com.linagora.android.linshare.view.MainNavigationFragment
import com.linagora.android.linshare.view.dialog.ConfirmDeleteWorkGroupMemberDialog
import com.linagora.android.linshare.view.dialog.SelectRoleDialog
import com.linagora.android.linshare.view.dialog.SelectRoleForUpdateDialog
import com.linagora.android.linshare.view.widget.errorLayout
import kotlinx.coroutines.launch
import org.slf4j.LoggerFactory
import java.util.UUID

class SharedSpaceAddMemberFragment : MainNavigationFragment() {

    companion object {
        private val LOGGER = LoggerFactory.getLogger(SharedSpaceAddMemberFragment::class.java)
    }

    private lateinit var viewModel: SharedSpaceAddMemberViewModel

    private lateinit var binding: FragmentAddMemberBinding

    private val arguments: SharedSpaceAddMemberFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAddMemberBinding.inflate(inflater, container, false)
        initViewModel()
        initAddMembersAutoComplete()
        return binding.root
    }

    private fun initViewModel() {
        viewModel = getViewModel(viewModelFactory)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.sharedSpaceId = arguments.sharedSpaceId.toSharedSpaceId()
        binding.viewModel = viewModel
        binding.ownRoleName = arguments.ownRoleName
        binding.internetAvailable = viewModel.internetAvailable
        observeViewState()
    }

    private fun observeViewState() {
        viewModel.viewState.observe(viewLifecycleOwner, Observer { state ->
            state.fold(
                ifLeft = { failure -> reactToFailure(failure) },
                ifRight = { success ->
                    when (success) {
                        is Success.ViewState -> reactToViewState(success)
                        is Success.ViewEvent -> reactToViewEvent(success)
                    } }
            )
        })
    }

    private fun initAddMembersAutoComplete() {
        with(binding.addMembersContainer) {
            initView()
            queryAfterTextChange(this@SharedSpaceAddMemberFragment::searchMembers)
            onSelectedMember(this@SharedSpaceAddMemberFragment::onAddMember)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initData(arguments.sharedSpaceId.toSharedSpaceId())
    }

    private fun reactToViewState(viewState: Success.ViewState) {
        binding.addMembersContainer.apply {
            showKeyBoard(viewState)
            bindingRoles(viewState)
            bindingDefaultSelectedRole(viewState)
        }
        when (viewState) {
            is AddMemberSuccess, is EditWorkGroupMemberRoleSuccess, is DeleteMemberSuccess -> getAllMembers()
        }
    }

    private fun reactToFailure(failure: Failure) {
        when (failure) {
            is AddMemberFailed -> alertAddMemberFailed()
        }
    }

    private fun alertAddMemberFailed() {
        Snackbar.make(binding.root, requireContext().resources.getString(R.string.add_member_failed), Snackbar.LENGTH_SHORT)
            .setAnchorView(binding.anchorSnackbar)
            .errorLayout(requireContext())
            .show()
    }

    private fun reactToViewEvent(viewEvent: Success.ViewEvent) {
        when (val filteredViewEvent = viewEvent.filterNetworkViewEvent(viewModel.internetAvailable.value)) {
            is Success.CancelViewEvent -> handleCannotExecuteViewEvent(filteredViewEvent.operatorType)
            else -> handleViewEvent(filteredViewEvent)
        }
    }

    private fun handleViewEvent(viewEvent: Success.ViewEvent) {
        when (viewEvent) {
            is OnSelectRoleClick -> selectRoles(viewEvent.lastSelectedRole)
            is OnSelectRoleClickForUpdate -> showSelectRoleForUpdateDialog(viewEvent.lastSelectedRole, viewEvent.sharedSpaceMember)
            is OnSelectedRole -> onSelectedRole(viewEvent.selectedRole)
            is OnSelectedRoleForUpdate -> onSelectedRoleForUpdate(viewEvent.selectedRole, viewEvent.sharedSpaceMember)
            is OnShowConfirmDeleteMemberClick -> showConfirmDeleteMemberDialog(viewEvent.member, arguments.sharedSpaceName)
            is OnDeleteWorkGroupMemberClick -> deleteMember(arguments.sharedSpaceId.toSharedSpaceId(), viewEvent.member)
        }
        viewModel.dispatchResetState()
    }

    private fun deleteMember(sharedSpaceId: SharedSpaceId, sharedSpaceMember: SharedSpaceMember) {
        viewModel.deleteMember(sharedSpaceId, sharedSpaceMember.sharedSpaceMemberId)
    }

    private fun showConfirmDeleteMemberDialog(sharedSpaceMember: SharedSpaceMember, sharedSpaceName: String) {
        ConfirmDeleteWorkGroupMemberDialog(
            title = String.format(
                getString(R.string.confirm_remove_member_from_work_group),
                sharedSpaceMember.sharedSpaceAccount.name,
                sharedSpaceName),
            negativeText = getString(R.string.cancel),
            positiveText = getString(R.string.delete),
            onPositiveCallback = { viewModel.dispatchState(Either.right(OnDeleteWorkGroupMemberClick(sharedSpaceMember))) }
        ).show(childFragmentManager, "confirm_delete_member_dialog")
    }

    private fun showSelectRoleForUpdateDialog(lastSelectedRole: SharedSpaceRole, sharedSpaceMember: SharedSpaceMember) {
        binding.addMembersContainer.sharedSpaceRoles?.let {
            dismissSelectRoleForUpdateDialog()
            SelectRoleForUpdateDialog(it.toList(), lastSelectedRole, sharedSpaceMember, viewModel.onSelectRoleForUpdateBehavior)
                .show(childFragmentManager, SelectRoleForUpdateDialog.TAG)
        }
    }

    private fun handleCannotExecuteViewEvent(operatorType: OperatorType) {
        val messageId = when (operatorType) {
            is OperatorType.OnSelectedRoleForUpdate -> R.string.can_not_change_member_role_without_network
            is OperatorType.DeleteWorkGroupMember -> R.string.can_not_delete_member_in_workgroup_without_network
            else -> R.string.can_not_process_without_network
        }
        dismissSelectRoleForUpdateDialog()
        Snackbar.make(binding.root, getString(messageId), Snackbar.LENGTH_SHORT)
            .errorLayout(requireContext())
            .setAnchorView(binding.anchorSnackbar)
            .show()
        viewModel.dispatchResetState()
    }

    private fun dismissSelectRoleForUpdateDialog() {
        childFragmentManager.dismissDialogFragmentByTag(SelectRoleForUpdateDialog.TAG)
    }

    private fun onSelectedRoleForUpdate(selectedRole: SharedSpaceRole, sharedSpaceMember: SharedSpaceMember) {
        dismissSelectRoleForUpdateDialog()
        viewModel.editMemberInSharedSpace(selectedRole, sharedSpaceMember)
    }

    private fun initData(sharedSpaceId: SharedSpaceId) {
        viewModel.initData(sharedSpaceId)
    }

    private fun getAllMembers() {
        viewModel.getAllMembers(arguments.sharedSpaceId.toSharedSpaceId())
    }

    private fun selectRoles(lastSelectedRole: SharedSpaceRole) {
        binding.addMembersContainer.sharedSpaceRoles?.let {
            dismissKeyBoard()
            dismissSelectRoleDialog()
            SelectRoleDialog(it.toList(), lastSelectedRole, viewModel.onSelectRoleBehavior)
                .show(childFragmentManager, SelectRoleDialog.TAG)
        }
    }

    private fun onSelectedRole(selectedRole: SharedSpaceRole) {
        LOGGER.info("onSelectedRole(): $selectedRole")
        dismissSelectRoleDialog()
        binding.addMembersContainer.selectedRole = selectedRole
    }

    private fun dismissSelectRoleDialog() {
        childFragmentManager.dismissDialogFragmentByTag(SelectRoleDialog.TAG)
    }

    private fun searchMembers(autoCompletePattern: AutoCompletePattern, autoCompleteType: AutoCompleteType, sharedSpaceId: SharedSpaceId) {
        val threadMemberAutoCompleteRequest = ThreadMemberAutoCompleteRequest(autoCompletePattern, autoCompleteType, sharedSpaceId)
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.addMemberSuggestionManager.query(threadMemberAutoCompleteRequest)
        }
    }

    private fun onAddMember(sharedSpaceId: SharedSpaceId, autoCompleteResult: AutoCompleteResult, role: SharedSpaceRole) {
        val addMemberRequest = AddMemberRequest(
            SharedSpaceAccountId(UUID.fromString(autoCompleteResult.identifier)),
            sharedSpaceId,
            role.sharedSpaceRoleId
        )
        viewModel.addMemberToSharedSpace(addMemberRequest)
    }

    override fun configureToolbar(toolbar: Toolbar) {
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back)
    }

    private fun dismissKeyBoard() {
        binding.addMembersContainer.clearFocus()
    }

    override fun onPause() {
        super.onPause()
        binding.addMembersContainer.clearFocus()
    }
}
