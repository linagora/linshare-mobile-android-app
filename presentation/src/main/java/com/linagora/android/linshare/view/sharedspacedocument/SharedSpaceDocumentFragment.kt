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
import com.linagora.android.linshare.domain.model.document.Document
import com.linagora.android.linshare.domain.model.order.OrderListConfigurationType
import com.linagora.android.linshare.domain.model.properties.PreviousUserPermissionAction
import com.linagora.android.linshare.domain.model.search.QueryString
import com.linagora.android.linshare.domain.model.sharedspace.WorkGroupDocument
import com.linagora.android.linshare.domain.model.sharedspace.WorkGroupFolder
import com.linagora.android.linshare.domain.model.sharedspace.WorkGroupNode
import com.linagora.android.linshare.domain.model.workgroup.NewNameRequest
import com.linagora.android.linshare.domain.usecases.auth.AuthenticationViewState
import com.linagora.android.linshare.domain.usecases.myspace.CopyFailedWithFileSizeExceed
import com.linagora.android.linshare.domain.usecases.myspace.CopyFailedWithQuotaReach
import com.linagora.android.linshare.domain.usecases.myspace.CopyInMySpaceFailure
import com.linagora.android.linshare.domain.usecases.myspace.CopyInMySpaceSuccess
import com.linagora.android.linshare.domain.usecases.order.GetOrderListConfigurationFailed
import com.linagora.android.linshare.domain.usecases.order.GetOrderListConfigurationSuccess
import com.linagora.android.linshare.domain.usecases.search.CloseSearchView
import com.linagora.android.linshare.domain.usecases.search.OpenSearchView
import com.linagora.android.linshare.domain.usecases.sharedspace.CopyToSharedSpaceFailure
import com.linagora.android.linshare.domain.usecases.sharedspace.CopyToSharedSpaceSuccess
import com.linagora.android.linshare.domain.usecases.sharedspace.CreateFolderViewEvent
import com.linagora.android.linshare.domain.usecases.sharedspace.CreateSharedSpaceFolderSuccessViewState
import com.linagora.android.linshare.domain.usecases.sharedspace.DownloadSharedSpaceNodeClick
import com.linagora.android.linshare.domain.usecases.sharedspace.DuplicateInSharedSpaceSuccess
import com.linagora.android.linshare.domain.usecases.sharedspace.DuplicateInSharedSpaceFailure
import com.linagora.android.linshare.domain.usecases.sharedspace.DuplicateWorkGroupNodeClick
import com.linagora.android.linshare.domain.usecases.sharedspace.GetSharedSpaceNodeSuccess
import com.linagora.android.linshare.domain.usecases.sharedspace.GetSharedSpaceSuccess
import com.linagora.android.linshare.domain.usecases.sharedspace.OnOrderByRowItemClick
import com.linagora.android.linshare.domain.usecases.sharedspace.OpenOrderByDialog
import com.linagora.android.linshare.domain.usecases.sharedspace.RemoveNodeNotFoundSharedSpaceState
import com.linagora.android.linshare.domain.usecases.sharedspace.RemoveSharedSpaceNodeClick
import com.linagora.android.linshare.domain.usecases.sharedspace.RemoveSharedSpaceNodeSuccessViewState
import com.linagora.android.linshare.domain.usecases.sharedspace.RenameSuccess
import com.linagora.android.linshare.domain.usecases.sharedspace.SharedSpaceDocumentContextMenuClick
import com.linagora.android.linshare.domain.usecases.sharedspace.SharedSpaceDocumentDetailsClick
import com.linagora.android.linshare.domain.usecases.sharedspace.SharedSpaceDocumentItemClick
import com.linagora.android.linshare.domain.usecases.sharedspace.SharedSpaceDocumentOnAddButtonClick
import com.linagora.android.linshare.domain.usecases.sharedspace.SharedSpaceDocumentOnBackClick
import com.linagora.android.linshare.domain.usecases.sharedspace.SharedSpaceDocumentOnCreateFolderClick
import com.linagora.android.linshare.domain.usecases.sharedspace.SharedSpaceDocumentOnUploadFileClick
import com.linagora.android.linshare.domain.usecases.sharedspace.SharedSpaceFolderContextMenuClick
import com.linagora.android.linshare.domain.usecases.sharedspace.SharedSpaceFolderItemClick
import com.linagora.android.linshare.domain.usecases.sharedspace.WorkGroupNodeRenameClick
import com.linagora.android.linshare.domain.usecases.utils.Failure
import com.linagora.android.linshare.domain.usecases.utils.Success
import com.linagora.android.linshare.model.parcelable.ParentDestinationInfo
import com.linagora.android.linshare.model.parcelable.SelectedDestinationInfo
import com.linagora.android.linshare.model.parcelable.SelectedDestinationInfoForOperate
import com.linagora.android.linshare.model.parcelable.SharedSpaceDestinationInfo
import com.linagora.android.linshare.model.parcelable.SharedSpaceIdParcelable
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
import com.linagora.android.linshare.view.Event
import com.linagora.android.linshare.view.MainActivityViewModel
import com.linagora.android.linshare.view.MainNavigationFragment
import com.linagora.android.linshare.view.Navigation.FileType
import com.linagora.android.linshare.view.Navigation.UploadType
import com.linagora.android.linshare.view.OpenFilePickerRequestCode
import com.linagora.android.linshare.view.WriteExternalPermissionRequestCode
import com.linagora.android.linshare.view.base.event.SharedSpaceSelectedDestinationMySpace
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
            is RemoveNodeNotFoundSharedSpaceState, is GetOrderListConfigurationFailed -> getAllNodes()
            is CopyToSharedSpaceFailure -> errorSnackBar(getString(R.string.copy_to_another_shared_space_fail)).show()
            is DuplicateInSharedSpaceFailure -> errorSnackBar(getString(R.string.duplicate_fail)).show()
            is CopyFailedWithFileSizeExceed -> errorSnackBar(getString(R.string.copy_to_my_space_error_file_size_exceed)).show()
            is CopyFailedWithQuotaReach -> errorSnackBar(getString(R.string.copy_to_my_space_error_quota_reach)).show()
            is CopyInMySpaceFailure -> errorSnackBar(getString(R.string.copy_to_my_space_error)).show()
        }
    }

    private fun reactToViewState(viewState: Success.ViewState) {
        when (viewState) {
            is RemoveSharedSpaceNodeSuccessViewState -> {
                alertRemoveSuccess(viewState.workGroupNode)
                getAllNodes()
            }
            is CopyToSharedSpaceSuccess -> successSnackBar(getString(R.string.copy_to_another_shared_space_success)).show()
            is DuplicateInSharedSpaceSuccess -> {
                successSnackBar(getString(R.string.duplicate_success)).show()
                getAllNodes()
            }
            is CopyInMySpaceSuccess -> alertCopyToMySpaceSuccess(viewState.documents)
            is CreateSharedSpaceFolderSuccessViewState -> getAllNodes()
            is GetOrderListConfigurationSuccess -> handleGetOrderListConfigSuccess(viewState.orderListConfigurationType)
            is RenameSuccess -> getAllNodes()
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

    private fun errorSnackBar(message: String): Snackbar {
        return Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG)
            .errorLayout(requireContext())
            .setAnchorView(binding.sharedSpaceDocumentAddButton)
    }

    private fun successSnackBar(message: String): Snackbar {
        return Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG)
            .withLinShare(requireContext())
            .setAnchorView(binding.sharedSpaceDocumentAddButton)
    }

    private fun alertCopyToMySpaceSuccess(documents: List<Document>) {
        successSnackBar(getString(R.string.copied_in_my_space, documents[0].name))
            .setAction(R.string.view) { navigateToMySpace() }
            .show()
        sharedSpacesDocumentViewModel.dispatchResetState()
    }

    private fun reactToViewEvent(viewEvent: Success.ViewEvent) {
        when (val filteredViewEvent = viewEvent.filterNetworkViewEvent(sharedSpacesDocumentViewModel.internetAvailable.value)) {
            is Success.CancelViewEvent -> handleCannotExecuteViewEvent(filteredViewEvent.operatorType)
            else -> handleViewEvent(filteredViewEvent)
        }
    }

    private fun handleViewEvent(viewEvent: Success.ViewEvent) {
        when (viewEvent) {
            is SharedSpaceFolderItemClick -> navigateIntoSubFolder(viewEvent.workGroupFolder)
            is SharedSpaceDocumentItemClick -> navigateToDetails(viewEvent.workGroupDocument)
            is SharedSpaceDocumentContextMenuClick -> showContextMenuSharedSpaceDocumentNode(viewEvent.workGroupDocument)
            is SharedSpaceFolderContextMenuClick -> showContextMenuSharedSpaceFolderNode(viewEvent.workGroupFolder)
            is DownloadSharedSpaceNodeClick -> handleDownloadSharedSpaceNode(viewEvent.workGroupNode)
            is RemoveSharedSpaceNodeClick -> confirmRemoveSharedSpaceNode(viewEvent.workGroupNode)
            is WorkGroupNodeCopyToViewEvent -> selectDestinationSpaceType(viewEvent.operatorType, viewEvent.node)
            is SharedSpaceSelectedDestinationSharedSpace -> handleSelectedDestinationToSharedSpace(viewEvent.workGroupNode, viewEvent.destinationForOperator)
            is SharedSpaceSelectedDestinationMySpace -> handleSelectedDestinationToMySpace(viewEvent.workGroupNode, viewEvent.destinationForOperator)
            is SharedSpaceDocumentOnUploadFileClick -> handleUploadFile()
            is SharedSpaceDocumentOnCreateFolderClick -> showCreateFolderDialog()
            is CreateFolderViewEvent -> handleCreateFolder(viewEvent.nameFolder)
            is OnOrderByRowItemClick -> handleOrderRowItemClick(viewEvent.orderListConfigurationType)
            is SharedSpaceDocumentDetailsClick -> navigateToDetails(viewEvent.node)
            is WorkGroupNodeRenameClick -> handleRenameClick(viewEvent.workGroupNode)
            is DuplicateWorkGroupNodeClick -> duplicateFile(viewEvent.workGroupNode)
            SharedSpaceDocumentOnBackClick -> navigateBack()
            SharedSpaceDocumentOnAddButtonClick -> showUploadFileOrCreateFolderDialog()
            OpenSearchView -> handleOpenSearch()
            CloseSearchView -> handleCloseSearch()
            OpenOrderByDialog -> showOrderByDialog()
        }
        sharedSpacesDocumentViewModel.dispatchResetState()
    }

    private fun showOrderByDialog() {
        dismissOrderByDialog()
        SharedSpaceDocumentOrderByDialog().show(childFragmentManager, SharedSpaceDocumentOrderByDialog.TAG)
    }

    private fun dismissOrderByDialog() {
        childFragmentManager.dismissDialogFragmentByTag(SharedSpaceDocumentOrderByDialog.TAG)
    }

    private fun handleOrderRowItemClick(orderListConfigurationType: OrderListConfigurationType) {
        dismissOrderByDialog()
        sharedSpacesDocumentViewModel.persistOrderListConfiguration(orderListConfigurationType)
    }

    private fun handleGetOrderListConfigSuccess(orderListConfigurationType: OrderListConfigurationType) {
        sharedSpacesDocumentViewModel.setCurrentOrderListConfigurationType(orderListConfigurationType)
        getAllNodes()
    }

    private fun handleCreateFolder(nameWorkGroup: NewNameRequest) {
        dismissCreateFolderDialog()
        sharedSpacesDocumentViewModel.createFolder(nameWorkGroup)
    }

    private fun dismissCreateFolderDialog() {
        childFragmentManager.dismissDialogFragmentByTag(CreateFolderDialog.TAG)
    }

    private fun showCreateFolderDialog() {
        dismissAddToSharedSpaceDialog()
        CreateFolderDialog(
                listWorkGroupNodes = sharedSpacesDocumentViewModel.listWorkGroupNode,
                onCreateSharedSpaceNode = { text -> sharedSpacesDocumentViewModel.createFolderBehavior.onCreate(NewNameRequest(text)) },
                onNewNameRequestChange = { name -> sharedSpacesDocumentViewModel.verifyNewName(name) },
                viewState = sharedSpacesDocumentViewModel.viewState)
            .show(childFragmentManager, CreateFolderDialog.TAG)
    }

    private fun handleUploadFile() {
        dismissAddToSharedSpaceDialog()
        openFilePicker()
    }

    private fun dismissAddToSharedSpaceDialog() {
        childFragmentManager.dismissDialogFragmentByTag(AddToSharedSpaceDialog.TAG)
    }

    private fun showUploadFileOrCreateFolderDialog() {
        sharedSpacesDocumentViewModel.currentSharedSpace.value
            ?.let { AddToSharedSpaceDialog(it)
                .show(childFragmentManager, AddToSharedSpaceDialog.TAG) }
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
            dismissDialogFragmentByTag(SharedSpacePickDestinationDialog.TAG)
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
        setUpMenu()
        setUpSwipeRefreshLayout()
        setUpSearchView()
        getOrderListConfiguration()

        sharedSpacesDocumentViewModel.getCurrentNode(
            sharedSpaceId = arguments.navigationInfo.sharedSpaceIdParcelable.toSharedSpaceId(),
            currentNodeId = arguments.navigationInfo.nodeIdParcelable.toWorkGroupNodeId())

        sharedSpacesDocumentViewModel.getCurrentSharedSpace(
            sharedSpaceId = arguments.navigationInfo.sharedSpaceIdParcelable.toSharedSpaceId())

        handleNewArguments()
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

    private fun setUpMenu() {
        binding.toolbar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.sharedSpaceDetailsMenu -> {
                    navigateToSharedSpaceDetails(arguments.navigationInfo.sharedSpaceIdParcelable)
                    true
                }
                else -> false
            }
        }
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

    private fun getOrderListConfiguration() {
        sharedSpacesDocumentViewModel.getOrderListConfiguration()
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

    private fun handleNewArguments() {
        arguments.selectedDestinationInfoFor
            ?.let(this@SharedSpaceDocumentFragment::operateSelectedDestinationInfo)
    }

    private fun operateSelectedDestinationInfo(selectedDestinationInfo: SelectedDestinationInfoForOperate) {
        LOGGER.info("operateSelectedDestinationInfo(): $selectedDestinationInfo")
        when (selectedDestinationInfo.operatorPickDestination) {
            Event.OperatorPickDestination.COPY -> copyToSharedSpace(selectedDestinationInfo)
        }
    }

    private fun copyToSharedSpace(selectedDestinationInfo: SelectedDestinationInfoForOperate) {
        sharedSpacesDocumentViewModel.copyNodeToSharedSpace(
            copyFromNodeId = selectedDestinationInfo.operateWorkGroupNode.toWorkGroupNodeId(),
            copyToSharedSpaceId = selectedDestinationInfo.selectedDestinationInfo
                .sharedSpaceDestinationInfo.sharedSpaceIdParcelable.toSharedSpaceId(),
            copyToParentNodeId = selectedDestinationInfo.selectedDestinationInfo
                .parentDestinationInfo.parentNodeId.toWorkGroupNodeId()
        )
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
            is OperatorType.CreateFolder -> R.string.not_create_folder_while_offline
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

    private fun handleSelectedDestinationToMySpace(workGroupNode: WorkGroupNode, operatorType: OperatorType) {
        when (operatorType) {
            OperatorType.CopyFile -> copyToMySpace(workGroupNode)
        }
    }

    private fun copyToMySpace(workGroupNode: WorkGroupNode) {
        dismissAllDialog()
        if (workGroupNode !is WorkGroupDocument) {
            return
        }
        sharedSpacesDocumentViewModel.copyNodeToMySpace(workGroupNode)
    }

    private fun handleRenameClick(workGroupNode: WorkGroupNode) {
        dismissAllDialog()
        RenameWorkgroupNodeDialog(
                currentWorkGroupNode = workGroupNode,
                listWorkGroupNodes = sharedSpacesDocumentViewModel.listWorkGroupNode,
                onRenameWorkgroupNode = sharedSpacesDocumentViewModel::renameWorkgroupNode,
                onNewNameRequestChange = sharedSpacesDocumentViewModel::verifyNewName,
                viewState = sharedSpacesDocumentViewModel.viewState)
            .show(childFragmentManager, RenameWorkgroupNodeDialog.TAG)
    }

    private fun navigateToSelectDestination(workGroupNode: WorkGroupNode) {
        if (workGroupNode !is WorkGroupDocument) {
            return
        }
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

    private fun navigateToSharedSpaceDetails(sharedSpaceId: SharedSpaceIdParcelable) {
        LOGGER.info("navigateToSharedSpaceDetails(): $sharedSpaceId")
        val actionToSharedSpaceDetails = SharedSpaceDocumentFragmentDirections
            .navigateToSharedSpaceDetails(sharedSpaceId)
        findNavController().navigate(actionToSharedSpaceDetails)
    }

    private fun navigateToDetails(workGroupNode: WorkGroupNode) {
        dismissAllDialog()
        LOGGER.info("navigateToDetails(): $workGroupNode")
        val actionToNodeDetails = SharedSpaceDocumentFragmentDirections
            .navigateToSharedSpaceDocumentDetailsFragment(
                workGroupNode.sharedSpaceId.toParcelable(),
                workGroupNode.workGroupNodeId.toParcelable())
        findNavController().navigate(actionToNodeDetails)
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

    private fun navigateToMySpace() = findNavController().navigate(R.id.navigation_my_space)

    private fun duplicateFile(workGroupNode: WorkGroupNode) {
        dismissAllDialog()
        if (workGroupNode !is WorkGroupDocument) {
            return
        }
        sharedSpacesDocumentViewModel.duplicateNodeInSharedSpace(workGroupNode.sharedSpaceId, workGroupNode)
    }
}
