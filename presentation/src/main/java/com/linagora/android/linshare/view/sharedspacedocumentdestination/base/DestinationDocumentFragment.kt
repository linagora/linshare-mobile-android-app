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

package com.linagora.android.linshare.view.sharedspacedocumentdestination.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.google.android.material.snackbar.Snackbar
import com.linagora.android.linshare.R
import com.linagora.android.linshare.databinding.FragmentSharedSpaceDocumentDestinationBinding
import com.linagora.android.linshare.domain.model.OperatorType
import com.linagora.android.linshare.domain.model.sharedspace.SharedSpaceId
import com.linagora.android.linshare.domain.model.sharedspace.WorkGroupNode
import com.linagora.android.linshare.domain.model.sharedspace.WorkGroupNodeId
import com.linagora.android.linshare.domain.usecases.sharedspace.GetSharedSpaceNodeSuccess
import com.linagora.android.linshare.domain.usecases.sharedspace.SharedSpaceDocumentItemClick
import com.linagora.android.linshare.domain.usecases.utils.Success
import com.linagora.android.linshare.model.parcelable.ParentDestinationInfo
import com.linagora.android.linshare.model.parcelable.SelectedDestinationInfo
import com.linagora.android.linshare.model.parcelable.SharedSpaceDestinationInfo
import com.linagora.android.linshare.model.parcelable.SharedSpaceNavigationInfo
import com.linagora.android.linshare.model.parcelable.toParcelable
import com.linagora.android.linshare.util.filterNetworkViewEvent
import com.linagora.android.linshare.view.MainNavigationFragment
import com.linagora.android.linshare.view.sharedspacedocumentdestination.CancelPickDestinationViewState
import com.linagora.android.linshare.view.sharedspacedocumentdestination.ChoosePickDestinationViewState
import com.linagora.android.linshare.view.widget.errorLayout

abstract class DestinationDocumentFragment : MainNavigationFragment() {

    abstract val destinationDocumentViewModel: DestinationDocumentViewModel

    private lateinit var binding: FragmentSharedSpaceDocumentDestinationBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSharedSpaceDocumentDestinationBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.navigationInfo = bindingNavigationInfo()
        observeViewState(binding)
        return binding.root
    }

    override fun configureToolbar(toolbar: Toolbar) {
        toolbar.setNavigationIcon(R.drawable.ic_navigation_back)
        toolbar.navigationIcon?.setTint(ContextCompat.getColor(toolbar.context, R.color.toolbar_primary_color))
        toolbar.setNavigationOnClickListener { navigateBack() }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpSwipeRefreshLayout()
        initData()
        setUpOnBackPressed()
    }

    private fun setUpSwipeRefreshLayout() {
        binding.swipeLayoutSharedSpace.setColorSchemeResources(R.color.colorPrimary)
    }

    private fun setUpOnBackPressed() {
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) { navigateBack() }
    }

    private fun initData() {
        val currentSharedSpaceId = extractSharedSpaceId()
        val currentNodeId = extractCurrentNodeId()
        currentSharedSpaceId?.let { sharedSpaceId ->
            getAllNodes(sharedSpaceId)
            getCurrentSharedSpace(sharedSpaceId)
            currentNodeId?.let { workGroupNodeId -> getCurrentNode(sharedSpaceId, workGroupNodeId) }
        }
    }

    abstract fun bindingNavigationInfo(): SharedSpaceNavigationInfo?

    abstract fun extractSharedSpaceId(): SharedSpaceId?

    abstract fun extractCurrentNodeId(): WorkGroupNodeId?

    abstract fun getRealCurrentNodeId(): WorkGroupNodeId?

    private fun getAllNodes(sharedSpaceId: SharedSpaceId) {
        destinationDocumentViewModel.getAllChildNodes(sharedSpaceId, getRealCurrentNodeId())
    }

    private fun getCurrentNode(sharedSpaceId: SharedSpaceId, currentNodeId: WorkGroupNodeId) {
        destinationDocumentViewModel.getCurrentNode(sharedSpaceId, currentNodeId)
    }

    private fun getCurrentSharedSpace(sharedSpaceId: SharedSpaceId) {
        destinationDocumentViewModel.getCurrentSharedSpace(sharedSpaceId)
    }

    private fun observeViewState(binding: FragmentSharedSpaceDocumentDestinationBinding) {
        binding.viewModel = destinationDocumentViewModel
        destinationDocumentViewModel.viewState.observe(viewLifecycleOwner, Observer {
            it.map { success ->
                when (success) {
                    is Success.ViewEvent -> reactToViewEvent(success)
                    is Success.ViewState -> reactToViewState(success)
                }
            }
        })
        binding.executePendingBindings()
    }

    private fun reactToViewEvent(viewEvent: Success.ViewEvent) {
        when (val filteredViewEvent = viewEvent.filterNetworkViewEvent(destinationDocumentViewModel.internetAvailable.value)) {
            is Success.CancelViewEvent -> handleCannotExecuteViewEvent(filteredViewEvent.operatorType)
            else -> handleViewEvent(filteredViewEvent)
        }
    }

    open fun reactToViewState(viewState: Success.ViewState) {
        bindingFolderName(viewState)
    }

    private fun bindingFolderName(viewState: Success.ViewState) {
        if (viewState is GetSharedSpaceNodeSuccess) {
            binding.navigationCurrentFolder.text = viewState.node.name
        }
    }

    private fun handleCannotExecuteViewEvent(operatorType: OperatorType) {
        val messageId = when (operatorType) {
            is OperatorType.OnItemClick -> R.string.not_access_folder_while_offline
            else -> R.string.can_not_process_without_network
        }
        Snackbar.make(binding.root, getString(messageId), Snackbar.LENGTH_SHORT)
            .errorLayout(requireContext())
            .show()
        destinationDocumentViewModel.dispatchResetState()
    }

    protected fun handleViewEvent(viewEvent: Success.ViewEvent) {
        when (viewEvent) {
            is SharedSpaceDocumentItemClick -> navigateIntoSubFolder(viewEvent.workGroupNode)
            is CancelPickDestinationViewState -> navigateInCancelDestination()
            is ChoosePickDestinationViewState -> navigateInChooseDestination()
        }
        destinationDocumentViewModel.dispatchResetState()
    }

    protected fun selectCurrentDestination(): SelectedDestinationInfo {
        val currentSharedSpace = destinationDocumentViewModel.currentSharedSpace.value
        val currentNode = destinationDocumentViewModel.currentNode.value

        require(currentSharedSpace != null) { "sharedSpace is not available" }
        require(currentNode != null) { "workgroup node is not available" }

        return SelectedDestinationInfo(
            sharedSpaceDestinationInfo = SharedSpaceDestinationInfo(
                currentSharedSpace.sharedSpaceId.toParcelable(),
                currentSharedSpace.name,
                currentSharedSpace.quotaId.toParcelable()
            ),
            parentDestinationInfo = ParentDestinationInfo(
                generateSelectNodeId(currentNode).toParcelable(),
                currentNode.name
            )
        )
    }

    abstract fun generateSelectNodeId(currentNode: WorkGroupNode): WorkGroupNodeId

    abstract fun navigateIntoSubFolder(subFolder: WorkGroupNode)
    abstract fun navigateInCancelDestination()
    abstract fun navigateInChooseDestination()

    protected open fun navigateBack() {
        destinationDocumentViewModel.currentNode.value?.let { node ->
            node.treePath.takeIf { it.isNullOrEmpty() }
                ?.let { navigateBackToSharedSpaceDestination() }
                ?: navigateBackToPreviousFolder(node)
        }
    }

    abstract fun navigateBackToSharedSpaceDestination()
    abstract fun navigateBackToPreviousFolder(workGroupNode: WorkGroupNode)
}
