package com.linagora.android.linshare.view.sharedspacedocumentdestination

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import arrow.core.Either
import com.linagora.android.linshare.R
import com.linagora.android.linshare.databinding.FragmentSharedSpaceDocumentDestinationBinding
import com.linagora.android.linshare.domain.model.sharedspace.WorkGroupDocument
import com.linagora.android.linshare.domain.model.sharedspace.WorkGroupNode
import com.linagora.android.linshare.domain.usecases.sharedspace.GetSharedSpaceNodeSuccess
import com.linagora.android.linshare.domain.usecases.sharedspace.SharedSpaceDocumentItemClick
import com.linagora.android.linshare.domain.usecases.sharedspace.SharedSpaceDocumentOnBackClick
import com.linagora.android.linshare.domain.usecases.utils.Success
import com.linagora.android.linshare.model.parcelable.SharedSpaceNavigationInfo
import com.linagora.android.linshare.model.parcelable.WorkGroupNodeIdParcelable
import com.linagora.android.linshare.model.parcelable.getParentNodeId
import com.linagora.android.linshare.model.parcelable.toParcelable
import com.linagora.android.linshare.model.parcelable.toSharedSpaceId
import com.linagora.android.linshare.model.parcelable.toWorkGroupNodeId
import com.linagora.android.linshare.util.getViewModel
import com.linagora.android.linshare.view.MainNavigationFragment
import com.linagora.android.linshare.view.Navigation.FileType
import javax.inject.Inject

class SharedSpaceDocumentDestinationFragment : MainNavigationFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var viewModel: SharedSpaceDocumentDestinationViewModel

    private lateinit var binding: FragmentSharedSpaceDocumentDestinationBinding

    private val arguments: SharedSpaceDocumentDestinationFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSharedSpaceDocumentDestinationBinding.inflate(inflater, container, false)
        initViewModel(binding)
        return binding.root
    }

    override fun configureToolbar(toolbar: Toolbar) {
        toolbar.setNavigationIcon(R.drawable.ic_navigation_back)
        toolbar.navigationIcon?.setTint(ContextCompat.getColor(context!!, R.color.toolbar_primary_color))
    }

    private fun initViewModel(binding: FragmentSharedSpaceDocumentDestinationBinding) {
        viewModel = getViewModel(viewModelFactory)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
        binding.navigationInfo = arguments.navigationInfo
        observeViewState()
    }

    private fun observeViewState() {
        viewModel.viewState.observe(viewLifecycleOwner, Observer { state ->
            state.map { success ->
                when (success) {
                    is Success.ViewEvent -> reactToViewEvent(success)
                    is Success.ViewState -> reactToViewState(success)
                }
            }
        })
    }

    private fun reactToViewState(viewState: Success.ViewState) {
        bindingFolderName(viewState)
    }

    private fun reactToViewEvent(viewEvent: Success.ViewEvent) {
        when (viewEvent) {
            is SharedSpaceDocumentItemClick -> navigateIntoSubFolder(viewEvent.workGroupNode)
            SharedSpaceDocumentOnBackClick -> navigateBack()
        }

        viewModel.dispatchState(Either.right(Success.Idle))
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpSwipeRefreshLayout()
        getAllNodes()
        getCurrentNode()
        getCurrentSharedSpace()
    }

    private fun getCurrentNode() {
        viewModel.getCurrentNode(
            sharedSpaceId = arguments.navigationInfo.sharedSpaceIdParcelable.toSharedSpaceId(),
            currentNodeId = arguments.navigationInfo.nodeIdParcelable.toWorkGroupNodeId())
    }

    private fun getCurrentSharedSpace() {
        viewModel.getCurrentSharedSpace(
            sharedSpaceId = arguments.navigationInfo.sharedSpaceIdParcelable.toSharedSpaceId())
    }

    private fun setUpSwipeRefreshLayout() {
        binding.swipeLayoutSharedSpace.setColorSchemeResources(R.color.colorPrimary)
    }

    private fun getAllNodes() {
        viewModel.getAllChildNodes(
            arguments.navigationInfo.sharedSpaceIdParcelable.toSharedSpaceId(),
            arguments.navigationInfo.getParentNodeId()
        )
    }

    private fun bindingFolderName(viewState: Success.ViewState) {
        if (viewState is GetSharedSpaceNodeSuccess) {
            binding.navigationCurrentFolder.text = viewState.node.name
        }
    }

    private fun navigateIntoSubFolder(workGroupNode: WorkGroupNode) {
        if (workGroupNode is WorkGroupDocument) {
            return
        }

        val action = SharedSpaceDocumentDestinationFragmentDirections.actionNavigationPickDestinationToPickDestination(
            arguments.uploadType,
            arguments.uri,
            arguments.uploadDestinationInfo,
            generateNavigationInfoForSubFolder(workGroupNode))
        findNavController().navigate(action)
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
