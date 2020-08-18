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

package com.linagora.android.linshare.view.sharedspacedocument

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import arrow.core.Either
import com.google.android.material.snackbar.Snackbar
import com.linagora.android.linshare.R
import com.linagora.android.linshare.databinding.FragmentSharedSpaceDocumentBinding
import com.linagora.android.linshare.domain.model.OperatorType
import com.linagora.android.linshare.domain.model.properties.PreviousUserPermissionAction
import com.linagora.android.linshare.domain.model.search.QueryString
import com.linagora.android.linshare.domain.model.sharedspace.WorkGroupDocument
import com.linagora.android.linshare.domain.model.sharedspace.WorkGroupFolder
import com.linagora.android.linshare.domain.model.sharedspace.WorkGroupNode
import com.linagora.android.linshare.domain.usecases.auth.AuthenticationViewState
import com.linagora.android.linshare.domain.usecases.search.CloseSearchView
import com.linagora.android.linshare.domain.usecases.search.OpenSearchView
import com.linagora.android.linshare.domain.usecases.sharedspace.DownloadSharedSpaceNodeClick
import com.linagora.android.linshare.domain.usecases.sharedspace.GetSharedSpaceNodeSuccess
import com.linagora.android.linshare.domain.usecases.sharedspace.GetSharedSpaceSuccess
import com.linagora.android.linshare.domain.usecases.sharedspace.RemoveNodeNotFoundSharedSpaceState
import com.linagora.android.linshare.domain.usecases.sharedspace.RemoveSharedSpaceNodeClick
import com.linagora.android.linshare.domain.usecases.sharedspace.RemoveSharedSpaceNodeSuccessViewState
import com.linagora.android.linshare.domain.usecases.sharedspace.SharedSpaceDocumentContextMenuClick
import com.linagora.android.linshare.domain.usecases.sharedspace.SharedSpaceDocumentItemClick
import com.linagora.android.linshare.domain.usecases.sharedspace.SharedSpaceDocumentOnAddButtonClick
import com.linagora.android.linshare.domain.usecases.sharedspace.SharedSpaceDocumentOnBackClick
import com.linagora.android.linshare.domain.usecases.sharedspace.SharedSpaceFolderContextMenuClick
import com.linagora.android.linshare.domain.usecases.utils.Failure
import com.linagora.android.linshare.domain.usecases.utils.Success
import com.linagora.android.linshare.model.parcelable.ParentDestinationInfo
import com.linagora.android.linshare.model.parcelable.SelectedDestinationInfo
import com.linagora.android.linshare.model.parcelable.SharedSpaceDestinationInfo
import com.linagora.android.linshare.model.parcelable.SharedSpaceNavigationInfo
import com.linagora.android.linshare.model.parcelable.WorkGroupNodeIdParcelable
import com.linagora.android.linshare.model.parcelable.getCurrentNodeId
import com.linagora.android.linshare.model.parcelable.toParcelable
import com.linagora.android.linshare.model.parcelable.toSharedSpaceId
import com.linagora.android.linshare.model.parcelable.toWorkGroupNodeId
import com.linagora.android.linshare.model.permission.PermissionResult
import com.linagora.android.linshare.model.properties.RuntimePermissionRequest
import com.linagora.android.linshare.util.Constant
import com.linagora.android.linshare.util.dismissDialogFragmentByTag
import com.linagora.android.linshare.util.dismissKeyboard
import com.linagora.android.linshare.util.filterNetworkViewEvent
import com.linagora.android.linshare.util.getViewModel
import com.linagora.android.linshare.util.openFilePicker
import com.linagora.android.linshare.util.showKeyboard
import com.linagora.android.linshare.view.MainActivityViewModel
import com.linagora.android.linshare.view.MainNavigationFragment
import com.linagora.android.linshare.view.Navigation.FileType
import com.linagora.android.linshare.view.Navigation.UploadType
import com.linagora.android.linshare.view.OpenFilePickerRequestCode
import com.linagora.android.linshare.view.WriteExternalPermissionRequestCode
import com.linagora.android.linshare.view.base.event.SharedSpaceSelectedDestinationSharedSpace
import com.linagora.android.linshare.view.base.event.WorkGroupNodeCopyToViewEvent
import com.linagora.android.linshare.view.upload.UploadFragmentArgs
import com.linagora.android.linshare.view.widget.errorLayout
import com.linagora.android.linshare.view.widget.withLinShare
import org.slf4j.LoggerFactory

class SharedSpaceDocumentFragment : MainNavigationFragment() {

    companion object {
        private val LOGGER = LoggerFactory.getLogger(SharedSpaceDocumentFragment::class.java)

        const val NAVIGATION_INFO_KEY = "navigationInfo"
    }

    private val mainActivityViewModel: MainActivityViewModel
            by activityViewModels { viewModelFactory }

    private lateinit var sharedSpacesDocumentViewModel: SharedSpaceDocumentViewModel

    private lateinit var binding: FragmentSharedSpaceDocumentBinding

    private val arguments: SharedSpaceDocumentFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSharedSpaceDocumentBinding.inflate(inflater, container, false)
        initViewModel(binding)
        return binding.root
    }

    private fun initViewModel(binding: FragmentSharedSpaceDocumentBinding) {
        sharedSpacesDocumentViewModel = getViewModel(viewModelFactory)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = sharedSpacesDocumentViewModel
        binding.navigationInfo = arguments.navigationInfo
        observeViewState()
        observeRequestPermission()
    }

    private fun observeViewState() {
        sharedSpacesDocumentViewModel.viewState.observe(viewLifecycleOwner, Observer { state ->
            state.fold(
                ifLeft = { failure -> reactToFailureState(failure) },
                ifRight = { success ->
                    when (success) {
                        is Success.ViewEvent -> reactToViewEvent(success)
                        is Success.ViewState -> reactToViewState(success)
                    }
                })
        })
    }

    private fun reactToFailureState(failure: Failure) {
        when (failure) {
            is Failure.CannotExecuteWithoutNetwork -> handleCannotExecuteViewEvent(failure.operatorType)
            is RemoveNodeNotFoundSharedSpaceState -> getAllNodes()
        }
    }

    private fun reactToViewState(viewState: Success.ViewState) {
        when (viewState) {
            is RemoveSharedSpaceNodeSuccessViewState -> {
                alertRemoveSuccess(viewState.workGroupNode)
                getAllNodes()
            }
        }
        bindingTitleName(viewState)
        bindingFolderName(viewState)
    }

    private fun alertRemoveSuccess(workGroupNode: WorkGroupNode) {
        val fileType = workGroupNode.takeIf { it is WorkGroupFolder }
            ?.let { getString(R.string.folder) }
            ?: getString(R.string.file)
        Snackbar.make(binding.root, requireContext().resources.getString(R.string.success_deleted, fileType), Snackbar.LENGTH_SHORT)
            .withLinShare(requireContext())
            .setAnchorView(binding.sharedSpaceDocumentAddButton)
            .show()
    }

    private fun reactToViewEvent(viewEvent: Success.ViewEvent) {
        when (val filteredViewEvent = viewEvent.filterNetworkViewEvent(sharedSpacesDocumentViewModel.internetAvailable.value)) {
            is Success.CancelViewEvent -> handleCannotExecuteViewEvent(filteredViewEvent.operatorType)
            else -> handleViewEvent(filteredViewEvent)
        }
    }

    private fun handleViewEvent(viewEvent: Success.ViewEvent) {
        when (viewEvent) {
            is SharedSpaceDocumentItemClick -> navigateIntoSubFolder(viewEvent.workGroupNode)
            is SharedSpaceDocumentContextMenuClick -> showContextMenuSharedSpaceDocumentNode(viewEvent.workGroupDocument)
            is SharedSpaceFolderContextMenuClick -> showContextMenuSharedSpaceFolderNode(viewEvent.workGroupFolder)
            is DownloadSharedSpaceNodeClick -> handleDownloadSharedSpaceNode(viewEvent.workGroupNode)
            is RemoveSharedSpaceNodeClick -> confirmRemoveSharedSpaceNode(viewEvent.workGroupNode)
            is WorkGroupNodeCopyToViewEvent -> selectDestinationSpaceType(viewEvent.operatorType, viewEvent.node)
            is SharedSpaceSelectedDestinationSharedSpace -> handleSelectedDestinationToSharedSpace(viewEvent.workGroupNode, viewEvent.destinationForOperator)
            SharedSpaceDocumentOnBackClick -> navigateBack()
            SharedSpaceDocumentOnAddButtonClick -> openFilePicker()
            OpenSearchView -> handleOpenSearch()
            CloseSearchView -> handleCloseSearch()
        }
        sharedSpacesDocumentViewModel.dispatchResetState()
    }

    private fun confirmRemoveSharedSpaceNode(workGroupNode: WorkGroupNode) {
        dismissContextMenuDialog(workGroupNode)

        ConfirmRemoveSharedSpaceNodeDialog(
            title = getString(R.string.confirm_delete_file, workGroupNode.name),
            negativeText = getString(R.string.cancel),
            positiveText = getString(R.string.delete),
            onPositiveCallback = { handleRemoveSharedSpaceNode(workGroupNode) }
        ).show(childFragmentManager, "confirm_remove_shared_space_node_dialog")
    }

    private fun dismissContextMenuDialog(workGroupNode: WorkGroupNode) {
        workGroupNode.takeIf { it is WorkGroupDocument }
            ?.let { childFragmentManager.dismissDialogFragmentByTag(SharedSpaceDocumentContextMenuDialog.TAG) }
            ?: childFragmentManager.dismissDialogFragmentByTag(SharedSpaceFolderContextMenuDialog.TAG)
    }

    private fun dismissAllDialog() {
        with(childFragmentManager) {
            dismissDialogFragmentByTag(SharedSpaceDocumentContextMenuDialog.TAG)
            dismissDialogFragmentByTag(SharedSpaceFolderContextMenuDialog.TAG)
        }
    }

    private fun handleRemoveSharedSpaceNode(workGroupNode: WorkGroupNode) {
        sharedSpacesDocumentViewModel.removeSharedSpaceNode(
            arguments.navigationInfo.sharedSpaceIdParcelable.toSharedSpaceId(), workGroupNode)
    }

    private fun handleDownloadSharedSpaceNode(workGroupNode: WorkGroupNode) {
        dismissContextMenuDialog(workGroupNode)

        when (mainActivityViewModel.checkWriteStoragePermission(requireContext())) {
            PermissionResult.PermissionGranted -> { download(workGroupNode) }
            else -> { shouldRequestWriteStoragePermission() }
        }
    }

    private fun handleOpenSearch() {
        binding.apply {
            includeSearchContainer.searchContainer.visibility = View.VISIBLE
            includeSearchContainer.searchView.requestFocus()
            sharedSpaceDocumentBottomBar.visibility = View.GONE
        }
    }

    private fun handleCloseSearch() {
        binding.apply {
            includeSearchContainer.searchContainer.visibility = View.GONE
            sharedSpaceDocumentBottomBar.visibility = View.VISIBLE
            includeSearchContainer.searchView.apply {
                setQuery(Constant.CLEAR_QUERY_STRING, Constant.NOT_SUBMIT_TEXT)
                clearFocus()
            }
        }
    }

    private fun download(workGroupNode: WorkGroupNode) {
        LOGGER.info("download() $workGroupNode")
        mainActivityViewModel.currentAuthentication.value
            ?.let { authentication -> downloadDocument(authentication, workGroupNode) }
    }

    private fun downloadDocument(
        authentication: AuthenticationViewState,
        workGroupNode: WorkGroupNode
    ) {
        workGroupNode.takeIf { it is WorkGroupDocument }
            ?.let {
                sharedSpacesDocumentViewModel.downloadSharedSpaceDocument(
                    authentication.credential,
                    authentication.token,
                    workGroupNode as WorkGroupDocument
                )
            }
    }

    private fun shouldRequestWriteStoragePermission() {
        mainActivityViewModel.shouldShowWriteStoragePermissionRequest(requireActivity())
    }

    private fun observeRequestPermission() {
        mainActivityViewModel.shouldShowPermissionRequestState.observe(viewLifecycleOwner, Observer {
            if (it is RuntimePermissionRequest.ShouldShowWriteStorage) {
                requestPermissions(
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    WriteExternalPermissionRequestCode.code
                )
            }
        })
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        LOGGER.info("onRequestPermissionsResult() $requestCode")
        when (requestCode) {
            WriteExternalPermissionRequestCode.code -> {
                Either.cond(
                    test = grantResults.all { grantResult -> grantResult == PackageManager.PERMISSION_GRANTED },
                    ifTrue = { sharedSpacesDocumentViewModel.getDownloading()?.let { download(it) } },
                    ifFalse = { mainActivityViewModel.setActionForWriteStoragePermissionRequest(PreviousUserPermissionAction.DENIED) }
                )
            }
        }
    }

    private fun showContextMenuSharedSpaceDocumentNode(workGroupDocument: WorkGroupDocument) {
        SharedSpaceDocumentContextMenuDialog(workGroupDocument)
            .show(childFragmentManager, SharedSpaceDocumentContextMenuDialog.TAG)
    }

    private fun showContextMenuSharedSpaceFolderNode(workGroupFolder: WorkGroupFolder) {
        SharedSpaceFolderContextMenuDialog(workGroupFolder)
            .show(childFragmentManager, SharedSpaceFolderContextMenuDialog.TAG)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpSwipeRefreshLayout()
        setUpSearchView()
        getAllNodes()

        sharedSpacesDocumentViewModel.getCurrentNode(
            sharedSpaceId = arguments.navigationInfo.sharedSpaceIdParcelable.toSharedSpaceId(),
            currentNodeId = arguments.navigationInfo.nodeIdParcelable.toWorkGroupNodeId())

        sharedSpacesDocumentViewModel.getCurrentSharedSpace(
            sharedSpaceId = arguments.navigationInfo.sharedSpaceIdParcelable.toSharedSpaceId())
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
                    searchSharedSpaceDocument(newText)
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

    private fun searchSharedSpaceDocument(query: String) {
        query.trim()
            .let(::QueryString)
            .let { sendQueryString(it) }
    }

    private fun sendQueryString(query: QueryString) {
        sharedSpacesDocumentViewModel.searchDocument(
            sharedSpaceId = arguments.navigationInfo.sharedSpaceIdParcelable.toSharedSpaceId(),
            parentNodeId = arguments.navigationInfo.getCurrentNodeId(),
            query = query
        )
    }

    private fun setUpSwipeRefreshLayout() {
        binding.swipeLayoutSharedSpace.setColorSchemeResources(R.color.colorPrimary)
    }

    private fun getAllNodes() {
        sharedSpacesDocumentViewModel.getAllChildNodes(
            arguments.navigationInfo.sharedSpaceIdParcelable.toSharedSpaceId(),
            arguments.navigationInfo.getCurrentNodeId()
        )
    }

    private fun bindingTitleName(viewState: Success.ViewState) {
        if (viewState is GetSharedSpaceSuccess) {
            binding.txtTitle.text = viewState.sharedSpace.name
        }
    }

    private fun bindingFolderName(viewState: Success.ViewState) {
        if (viewState is GetSharedSpaceNodeSuccess) {
            binding.navigationPath.navigationCurrentFolder.text = viewState.node.name
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        LOGGER.info("onActivityResult() $requestCode - $data")
        requestCode.takeIf { it == OpenFilePickerRequestCode.code }
            ?.let { data?.data }
            ?.let(this@SharedSpaceDocumentFragment::navigateToUpload)
    }

    private fun navigateToUpload(uri: Uri) {
        val bundle = UploadFragmentArgs(
                uploadType = UploadType.INSIDE_APP_TO_WORKGROUP,
                uri = uri,
                selectedDestinationInfo = createUploadDestination())
            .toBundle()
        findNavController().navigate(R.id.uploadFragment, bundle)
    }

    private fun createUploadDestination(): SelectedDestinationInfo {
        val currentSharedSpace = sharedSpacesDocumentViewModel.currentSharedSpace.value
        val currentNode = sharedSpacesDocumentViewModel.currentNode.value

        require(currentSharedSpace != null) { "sharedSpace is not available" }
        require(currentNode != null) { "workgroup node is not available" }

        return SelectedDestinationInfo(
            sharedSpaceDestinationInfo = SharedSpaceDestinationInfo(
                currentSharedSpace.sharedSpaceId.toParcelable(),
                currentSharedSpace.name,
                currentSharedSpace.quotaId.toParcelable()
            ),
            parentDestinationInfo = ParentDestinationInfo(
                currentNode.workGroupNodeId.toParcelable(),
                currentNode.name
            )
        )
    }

    private fun handleCannotExecuteViewEvent(operatorType: OperatorType) {
        dismissAllDialog()
        val messageId = when (operatorType) {
            is OperatorType.OnItemClick -> R.string.not_access_folder_while_offline
            else -> R.string.can_not_process_without_network
        }
        Snackbar.make(binding.root, getString(messageId), Snackbar.LENGTH_SHORT)
            .errorLayout(requireContext())
            .setAnchorView(binding.sharedSpaceDocumentAddButton)
            .show()
        sharedSpacesDocumentViewModel.dispatchResetState()
    }

    private fun selectDestinationSpaceType(operatorType: OperatorType, workGroupNode: WorkGroupNode) {
        dismissAllDialog()
        SharedSpacePickDestinationDialog(workGroupNode, operatorType, sharedSpacesDocumentViewModel.selectDestinationSpaceTypeAction)
            .show(childFragmentManager, SharedSpacePickDestinationDialog.TAG)
    }

    private fun handleSelectedDestinationToSharedSpace(workGroupNode: WorkGroupNode, operatorType: OperatorType) {
        when (operatorType) {
            OperatorType.CopyFile -> navigateToSelectDestination(workGroupNode)
        }
    }

    private fun navigateToSelectDestination(workGroupNode: WorkGroupNode) {
        val action = SharedSpaceDocumentFragmentDirections
            .navigateToCopySharedSpaceDestinationFragment(
                workGroupNode.sharedSpaceId.toParcelable(),
                arguments.navigationInfo.nodeIdParcelable,
                workGroupNode.workGroupNodeId.toParcelable())
        findNavController().navigate(action)
    }

    private fun navigateIntoSubFolder(workGroupNode: WorkGroupNode) {
        if (workGroupNode is WorkGroupDocument) {
            return
        }

        val navigationBundle = bundleOf(
            NAVIGATION_INFO_KEY to generateNavigationInfoForSubFolder(workGroupNode)
        )
        findNavController().navigate(R.id.navigation_shared_spaced_document, navigationBundle)
    }

    private fun generateNavigationInfoForSubFolder(workGroupNode: WorkGroupNode): SharedSpaceNavigationInfo {
        return SharedSpaceNavigationInfo(
            sharedSpaceIdParcelable = workGroupNode.sharedSpaceId.toParcelable(),
            fileType = FileType.NORMAL,
            nodeIdParcelable = WorkGroupNodeIdParcelable(workGroupNode.workGroupNodeId.uuid)
        )
    }

    private fun navigateBack() {
        findNavController().popBackStack()
    }
}
