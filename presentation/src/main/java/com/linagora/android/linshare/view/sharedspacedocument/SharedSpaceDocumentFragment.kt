package com.linagora.android.linshare.view.sharedspacedocument

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import arrow.core.Either
import com.linagora.android.linshare.R
import com.linagora.android.linshare.databinding.FragmentSharedSpaceDocumentBinding
import com.linagora.android.linshare.domain.model.properties.PreviousUserPermissionAction
import com.linagora.android.linshare.domain.model.sharedspace.SharedSpaceId
import com.linagora.android.linshare.domain.model.sharedspace.WorkGroupDocument
import com.linagora.android.linshare.domain.model.sharedspace.WorkGroupNode
import com.linagora.android.linshare.domain.model.sharedspace.WorkGroupNodeId
import com.linagora.android.linshare.domain.usecases.sharedspace.DownloadSharedSpaceNodeClick
import com.linagora.android.linshare.domain.usecases.sharedspace.GetSharedSpaceNodeSuccess
import com.linagora.android.linshare.domain.usecases.sharedspace.GetSharedSpaceSuccess
import com.linagora.android.linshare.domain.usecases.sharedspace.SharedSpaceDocumentContextMenuClick
import com.linagora.android.linshare.domain.usecases.sharedspace.SharedSpaceDocumentItemClick
import com.linagora.android.linshare.domain.usecases.sharedspace.SharedSpaceDocumentOnBackClick
import com.linagora.android.linshare.domain.usecases.utils.Success
import com.linagora.android.linshare.model.parcelable.SharedSpaceNavigationInfo
import com.linagora.android.linshare.model.parcelable.WorkGroupNodeIdParcelable
import com.linagora.android.linshare.model.parcelable.getParentNodeId
import com.linagora.android.linshare.model.parcelable.toParcelable
import com.linagora.android.linshare.model.parcelable.toSharedSpaceId
import com.linagora.android.linshare.model.parcelable.toWorkGroupNodeId
import com.linagora.android.linshare.model.permission.PermissionResult
import com.linagora.android.linshare.model.properties.RuntimePermissionRequest
import com.linagora.android.linshare.util.getViewModel
import com.linagora.android.linshare.view.MainActivityViewModel
import com.linagora.android.linshare.view.MainNavigationFragment
import com.linagora.android.linshare.view.Navigation
import com.linagora.android.linshare.view.WriteExternalPermissionRequestCode
import org.slf4j.LoggerFactory
import javax.inject.Inject

class SharedSpaceDocumentFragment : MainNavigationFragment() {

    companion object {
        const val NAVIGATION_INFO_KEY = "navigationInfo"

        private val LOGGER = LoggerFactory.getLogger(SharedSpaceDocumentFragment::class.java)
    }

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val mainActivityViewModel: MainActivityViewModel
            by activityViewModels { viewModelFactory }

    private lateinit var sharedSpaceDocumentContextMenuDialog: SharedSpaceDocumentContextMenuDialog

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
            state.map { success -> when (success) {
                is Success.ViewEvent -> reactToViewEvent(success)
                is Success.ViewState -> reactToViewState(success)
            } }
        })
    }

    private fun reactToViewState(viewState: Success.ViewState) {
        bindingTitleName(viewState)
        bindingFolderName(viewState)
    }

    private fun reactToViewEvent(viewEvent: Success.ViewEvent) {
        when (viewEvent) {
            is SharedSpaceDocumentItemClick -> navigateIntoSubFolder(viewEvent.workGroupNode)
            is SharedSpaceDocumentContextMenuClick -> showContextMenuSharedSpaceDocumentNode(viewEvent.workGroupDocument)
            is DownloadSharedSpaceNodeClick -> handleDownloadSharedSpaceNode(viewEvent.workGroupNode)
            SharedSpaceDocumentOnBackClick -> navigateBack()
        }
        sharedSpacesDocumentViewModel.dispatchState(Either.right(Success.Idle))
    }

    private fun handleDownloadSharedSpaceNode(workGroupNode: WorkGroupNode) {
        sharedSpaceDocumentContextMenuDialog.dismiss()
        when (mainActivityViewModel.checkWriteStoragePermission(requireContext())) {
            PermissionResult.PermissionGranted -> { download(workGroupNode) }
            else -> { shouldRequestWriteStoragePermission() }
        }
    }

    private fun download(workGroupNode: WorkGroupNode) {
        LOGGER.info("download() $workGroupNode")
        mainActivityViewModel.currentAuthentication.value
            ?.let { authentication ->
                workGroupNode.takeIf { it is WorkGroupDocument }
                    ?.let {
                        sharedSpacesDocumentViewModel.downloadSharedSpaceDocument(
                            authentication.credential,
                            authentication.token,
                            workGroupNode as WorkGroupDocument
                        )
                    }
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
        sharedSpaceDocumentContextMenuDialog = SharedSpaceDocumentContextMenuDialog(workGroupDocument)
        sharedSpaceDocumentContextMenuDialog.show(childFragmentManager, sharedSpaceDocumentContextMenuDialog.tag)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpSwipeRefreshLayout()
        getAllNodes(
            sharedSpaceId = arguments.navigationInfo.sharedSpaceIdParcelable.toSharedSpaceId(),
            parentNodeId = arguments.navigationInfo.getParentNodeId())

        sharedSpacesDocumentViewModel.getCurrentNode(
            sharedSpaceId = arguments.navigationInfo.sharedSpaceIdParcelable.toSharedSpaceId(),
            currentNodeId = arguments.navigationInfo.nodeIdParcelable.toWorkGroupNodeId())

        sharedSpacesDocumentViewModel.getCurrentSharedSpace(
            sharedSpaceId = arguments.navigationInfo.sharedSpaceIdParcelable.toSharedSpaceId())
    }

    private fun setUpSwipeRefreshLayout() {
        binding.swipeLayoutSharedSpace.setColorSchemeResources(R.color.colorPrimary)
    }

    private fun getAllNodes(sharedSpaceId: SharedSpaceId, parentNodeId: WorkGroupNodeId?) {
        sharedSpacesDocumentViewModel.getAllChildNodes(sharedSpaceId, parentNodeId)
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
            fileType = Navigation.FileType.NORMAL,
            nodeIdParcelable = WorkGroupNodeIdParcelable(workGroupNode.workGroupNodeId.uuid)
        )
    }

    private fun navigateBack() {
        findNavController().popBackStack()
    }
}
