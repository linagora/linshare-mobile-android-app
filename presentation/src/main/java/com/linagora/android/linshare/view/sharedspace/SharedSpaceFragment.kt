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

package com.linagora.android.linshare.view.sharedspace

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.core.os.bundleOf
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import arrow.core.Either
import com.google.android.material.snackbar.Snackbar
import com.linagora.android.linshare.R
import com.linagora.android.linshare.databinding.FragmentSharedSpaceBinding
import com.linagora.android.linshare.domain.model.OperatorType
import com.linagora.android.linshare.domain.model.order.OrderListConfigurationType
import com.linagora.android.linshare.domain.model.search.QueryString
import com.linagora.android.linshare.domain.model.sharedspace.SharedSpaceNodeNested
import com.linagora.android.linshare.domain.model.workgroup.NewNameRequest
import com.linagora.android.linshare.domain.usecases.order.GetOrderListConfigurationFailed
import com.linagora.android.linshare.domain.usecases.order.GetOrderListConfigurationSuccess
import com.linagora.android.linshare.domain.usecases.search.CloseSearchView
import com.linagora.android.linshare.domain.usecases.search.OpenSearchView
import com.linagora.android.linshare.domain.usecases.sharedspace.CreateWorkGroupButtonBottomBarClick
import com.linagora.android.linshare.domain.usecases.sharedspace.CreateWorkGroupSuccess
import com.linagora.android.linshare.domain.usecases.sharedspace.CreateWorkGroupViewState
import com.linagora.android.linshare.domain.usecases.sharedspace.DeleteSharedSpaceClick
import com.linagora.android.linshare.domain.usecases.sharedspace.DeletedSharedSpaceSuccess
import com.linagora.android.linshare.domain.usecases.sharedspace.DetailsSharedSpaceItem
import com.linagora.android.linshare.domain.usecases.sharedspace.GetSharedSpaceFailedInRename
import com.linagora.android.linshare.domain.usecases.sharedspace.OnOrderByRowItemClick
import com.linagora.android.linshare.domain.usecases.sharedspace.OnShowConfirmDeleteSharedSpaceClick
import com.linagora.android.linshare.domain.usecases.sharedspace.OpenOrderByDialog
import com.linagora.android.linshare.domain.usecases.sharedspace.RenameSharedSpaceFailure
import com.linagora.android.linshare.domain.usecases.sharedspace.RenameSharedSpaceItemClick
import com.linagora.android.linshare.domain.usecases.sharedspace.RenameSharedSpaceSuccess
import com.linagora.android.linshare.domain.usecases.sharedspace.SharedSpaceContextMenuClick
import com.linagora.android.linshare.domain.usecases.sharedspace.SharedSpaceItemClick
import com.linagora.android.linshare.domain.usecases.utils.Failure
import com.linagora.android.linshare.domain.usecases.utils.Success
import com.linagora.android.linshare.model.parcelable.SharedSpaceNavigationInfo
import com.linagora.android.linshare.model.parcelable.WorkGroupNodeIdParcelable
import com.linagora.android.linshare.model.parcelable.toParcelable
import com.linagora.android.linshare.util.Constant.CLEAR_QUERY_STRING
import com.linagora.android.linshare.util.Constant.NOT_SUBMIT_TEXT
import com.linagora.android.linshare.util.dismissDialogFragmentByTag
import com.linagora.android.linshare.util.dismissKeyboard
import com.linagora.android.linshare.util.filterNetworkViewEvent
import com.linagora.android.linshare.util.getViewModel
import com.linagora.android.linshare.util.showKeyboard
import com.linagora.android.linshare.view.MainNavigationFragment
import com.linagora.android.linshare.view.Navigation
import com.linagora.android.linshare.view.base.event.OnAddMemberContextMenuClick
import com.linagora.android.linshare.view.dialog.ConfirmDeleteSharedSpaceDialog
import com.linagora.android.linshare.view.sharedspacedocument.SharedSpaceDocumentFragment.Companion.NAVIGATION_INFO_KEY
import com.linagora.android.linshare.view.widget.errorLayout
import org.slf4j.LoggerFactory

class SharedSpaceFragment : MainNavigationFragment() {

    private lateinit var sharedSpaceViewModel: SharedSpaceViewModel

    private lateinit var binding: FragmentSharedSpaceBinding

    companion object {
        private val LOGGER = LoggerFactory.getLogger(SharedSpaceFragment::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSharedSpaceBinding.inflate(inflater, container, false)
        initViewModel(binding)
        return binding.root
    }

    private fun initViewModel(binding: FragmentSharedSpaceBinding) {
        sharedSpaceViewModel = getViewModel(viewModelFactory)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = sharedSpaceViewModel
        observeViewState()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpSwipeRefreshLayout()
        getOrderListConfiguration()
        setUpSearchView()
    }

    private fun observeViewState() {
        sharedSpaceViewModel.viewState.observe(viewLifecycleOwner, Observer {
            it.fold(this::reactToFailure, this::reactToSuccess)
        })
    }

    private fun reactToFailure(failure: Failure) {
        when (failure) {
            is Failure.CannotExecuteWithoutNetwork -> handleCannotExecuteViewEvent(failure.operatorType)
            is GetOrderListConfigurationFailed -> getSharedSpace()
            is GetSharedSpaceFailedInRename, is RenameSharedSpaceFailure -> handleRenameFailure()
        }
    }

    private fun reactToSuccess(success: Success) {
        when (success) {
            is Success.ViewEvent -> reactToViewEvent(success)
            is Success.ViewState -> reactToViewState(success)
        }
    }

    private fun reactToViewState(viewState: Success.ViewState) {
        when (viewState) {
            is CreateWorkGroupSuccess, is DeletedSharedSpaceSuccess, is RenameSharedSpaceSuccess -> getSharedSpace()
            is GetOrderListConfigurationSuccess -> handleGetOrderListConfigSuccess(viewState.orderListConfigurationType)
        }
    }

    private fun reactToViewEvent(viewEvent: Success.ViewEvent) {
        when (val filteredViewEvent = viewEvent.filterNetworkViewEvent(sharedSpaceViewModel.internetAvailable.value)) {
            is Success.CancelViewEvent -> handleCannotExecuteViewEvent(filteredViewEvent.operatorType)
            is RenameSharedSpaceItemClick -> handleRenameItemContextClick(filteredViewEvent.sharedSpaceNodeNested)
            else -> handleViewEvent(filteredViewEvent)
        }
    }

    private fun handleViewEvent(viewEvent: Success.ViewEvent) {
        when (viewEvent) {
            is SharedSpaceItemClick -> navigateIntoSharedSpace(viewEvent.sharedSpaceNodeNested)
            is OpenSearchView -> handleOpenSearch()
            is CloseSearchView -> handleCloseSearch()
            is SharedSpaceContextMenuClick -> showContextMenu(viewEvent.sharedSpaceNodeNested)
            is DetailsSharedSpaceItem -> navigateToDetails(viewEvent.sharedSpaceNodeNested)
            is CreateWorkGroupButtonBottomBarClick -> showCreateWorkGroupDialog()
            is CreateWorkGroupViewState -> handleCreateWorkGroup(viewEvent.nameWorkGroup)
            is OnShowConfirmDeleteSharedSpaceClick -> showConfirmDeleteSharedSpaceDialog(viewEvent.sharedSpaceNodeNested)
            is DeleteSharedSpaceClick -> deleteSharedSpace(viewEvent.sharedSpaceNodeNested)
            is OnAddMemberContextMenuClick -> navigateIntoAddMemberFragment(viewEvent.sharedSpaceNodeNested)
            is OpenOrderByDialog -> showOrderByDialog()
            is OnOrderByRowItemClick -> handleOrderRowItemClick(viewEvent.orderListConfigurationType)
        }
        sharedSpaceViewModel.dispatchResetState()
    }

    private fun deleteSharedSpace(sharedSpaceNodeNested: SharedSpaceNodeNested) {
        sharedSpaceViewModel.deleteSharedSpace(sharedSpaceNodeNested.sharedSpaceId)
    }

    private fun showConfirmDeleteSharedSpaceDialog(sharedSpaceNodeNested: SharedSpaceNodeNested) {
        dismissContextMenu()
        ConfirmDeleteSharedSpaceDialog(
            title = getString(R.string.confirm_delete_shared_space, sharedSpaceNodeNested.name),
            negativeText = getString(R.string.cancel),
            positiveText = getString(R.string.delete),
            onPositiveCallback = { sharedSpaceViewModel.dispatchState(Either.right(DeleteSharedSpaceClick(sharedSpaceNodeNested))) }
        ).show(childFragmentManager, "ConfirmDeleteSharedSpaceDialog")
    }

    private fun handleCreateWorkGroup(nameWorkGroup: NewNameRequest) {
        dismissCreateWorkGroupDialog()
        sharedSpaceViewModel.createWorkGroup(nameWorkGroup)
    }

    private fun showCreateWorkGroupDialog() {
        dismissCreateWorkGroupDialog()
        CreateWorkGroupDialog(
                listSharedSpaceNodeNestedData = sharedSpaceViewModel.listSharedSpaceNodeNested,
                onCreateWorkGroup = { text -> sharedSpaceViewModel.createWorkGroupBehavior.onCreate(NewNameRequest(text)) },
                onNewNameRequestChange = { name -> sharedSpaceViewModel.validName(name) },
                viewState = sharedSpaceViewModel.viewState)
            .show(childFragmentManager, CreateWorkGroupDialog.TAG)
    }

    private fun dismissCreateWorkGroupDialog() {
        childFragmentManager.dismissDialogFragmentByTag(CreateWorkGroupDialog.TAG)
    }

    private fun showOrderByDialog() {
        dismissListOrderByDialog()
        SharedSpaceListOrderByDialog().show(childFragmentManager, SharedSpaceListOrderByDialog.TAG)
    }

    private fun dismissListOrderByDialog() {
        childFragmentManager.dismissDialogFragmentByTag(SharedSpaceListOrderByDialog.TAG)
    }

    private fun handleOpenSearch() {
        binding.apply {
            includeSearchContainer.searchContainer.visibility = View.VISIBLE
            includeSearchContainer.searchView.requestFocus()
            sharedSpaceBottomBar.visibility = View.GONE
        }
    }

    private fun handleCloseSearch() {
        binding.apply {
            includeSearchContainer.searchContainer.visibility = View.GONE
            sharedSpaceBottomBar.visibility = View.VISIBLE
            includeSearchContainer.searchView.apply {
                setQuery(CLEAR_QUERY_STRING, NOT_SUBMIT_TEXT)
                clearFocus()
            }
        }
        getSharedSpace()
    }

    private fun handleGetOrderListConfigSuccess(orderListConfigurationType: OrderListConfigurationType) {
        sharedSpaceViewModel.setCurrentOrderListConfigurationType(orderListConfigurationType)
        getSharedSpace()
    }

    private fun handleOrderRowItemClick(orderListConfigurationType: OrderListConfigurationType) {
        dismissListOrderByDialog()
        sharedSpaceViewModel.persistOrderListConfiguration(orderListConfigurationType)
    }

    private fun setUpSearchView() {
        binding.includeSearchContainer.searchView.apply {

            setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String): Boolean {
                    this@apply.dismissKeyboard()
                    return true
                }

                override fun onQueryTextChange(newText: String): Boolean {
                    LOGGER.info("onQueryTextChange() $newText")
                    search(newText)
                    return true
                }
            })

            setOnQueryTextFocusChangeListener { view, hasFocus ->
                if (hasFocus) {
                    view.findFocus().showKeyboard()
                }
            }
        }
    }

    private fun search(query: String) {
        query.trim()
            .let(::QueryString)
            .let(this@SharedSpaceFragment::sendQueryString)
    }

    private fun sendQueryString(query: QueryString) {
        sharedSpaceViewModel.searchWithQuery(query)
    }

    private fun setUpSwipeRefreshLayout() {
        binding.swipeLayoutSharedSpace.setColorSchemeResources(R.color.colorPrimary)
    }

    private fun showContextMenu(sharedSpaceNodeNested: SharedSpaceNodeNested) {
        dismissContextMenu()
        SharedSpaceContextMenuDialog(sharedSpaceNodeNested)
            .show(childFragmentManager, SharedSpaceContextMenuDialog.TAG)
    }

    private fun dismissContextMenu() {
        childFragmentManager.dismissDialogFragmentByTag(SharedSpaceContextMenuDialog.TAG)
    }

    private fun getOrderListConfiguration() {
        sharedSpaceViewModel.getOrderListConfiguration()
    }

    private fun getSharedSpace() {
        LOGGER.info("getSharedSpaces")
        sharedSpaceViewModel.getSharedSpace()
    }

    private fun handleCannotExecuteViewEvent(operatorType: OperatorType) {
        val messageId = when (operatorType) {
            is OperatorType.CreateWorkGroup -> R.string.can_not_create_shared_space_without_network
            is OperatorType.OnItemClick -> R.string.not_access_workgroup_while_offline
            is OperatorType.ViewDetails -> R.string.not_view_workgroup_details_while_offline
            is OperatorType.DeleteSharedSpace -> R.string.can_not_delete_shared_space_without_network
            else -> R.string.can_not_process_without_network
        }
        dismissContextMenu()
        Snackbar.make(binding.root, getString(messageId), Snackbar.LENGTH_SHORT)
            .errorLayout(requireContext())
            .setAnchorView(binding.sharedSpaceUploadButton)
            .show()
        sharedSpaceViewModel.dispatchResetState()
    }

    private fun handleRenameFailure() {
        Snackbar.make(binding.root, getString(R.string.common_error_occurred_message), Snackbar.LENGTH_SHORT)
            .errorLayout(requireContext())
            .setAnchorView(binding.sharedSpaceUploadButton)
            .show()
        sharedSpaceViewModel.dispatchResetState()
    }

    private fun handleRenameItemContextClick(sharedSpaceNodeNested: SharedSpaceNodeNested) {
        dismissContextMenu()
        RenameSharedSpaceDialog(
            currentSharedSpaceNodeNested = sharedSpaceNodeNested,
            listSharedSpaceNodeNested = sharedSpaceViewModel.listSharedSpaceNodeNested,
            onRenameSharedSpace = sharedSpaceViewModel::renameSharedSpace,
            onNewNameRequestChange = sharedSpaceViewModel::validName,
            viewState = sharedSpaceViewModel.viewState
        ).show(childFragmentManager, RenameSharedSpaceDialog.TAG)
    }

    private fun navigateToDetails(sharedSpaceNodeNested: SharedSpaceNodeNested) {
        dismissContextMenu()
        val actionToDetails = SharedSpaceFragmentDirections
            .actionToNavigationSharedSpaceDetails(sharedSpaceNodeNested.sharedSpaceId.toParcelable())
        findNavController().navigate(actionToDetails)
    }

    private fun navigateIntoSharedSpace(sharedSpaceNodeNested: SharedSpaceNodeNested) {
        val navigationBundle = bundleOf(
            NAVIGATION_INFO_KEY to generateNavigationInfoForSharedSpaceRoot(sharedSpaceNodeNested)
        )
        findNavController().navigate(R.id.navigation_shared_spaced_document, navigationBundle)
    }

    private fun generateNavigationInfoForSharedSpaceRoot(sharedSpaceNodeNested: SharedSpaceNodeNested): SharedSpaceNavigationInfo {
        return SharedSpaceNavigationInfo(
            sharedSpaceIdParcelable = sharedSpaceNodeNested.sharedSpaceId.toParcelable(),
            fileType = Navigation.FileType.ROOT,
            nodeIdParcelable = WorkGroupNodeIdParcelable(sharedSpaceNodeNested.sharedSpaceId.uuid)
        )
    }

    private fun navigateIntoAddMemberFragment(sharedSpaceNodeNested: SharedSpaceNodeNested) {
        dismissContextMenu()
        val actionToAddMember = SharedSpaceFragmentDirections
            .navigateToAddMembersFragment(
                sharedSpaceNodeNested.sharedSpaceId.toParcelable(),
                sharedSpaceNodeNested.role.name,
                sharedSpaceNodeNested.name
            )
        findNavController().navigate(actionToAddMember)
    }
}
