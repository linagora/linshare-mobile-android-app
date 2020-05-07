package com.linagora.android.linshare.view.sharedspacedocument

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.linagora.android.linshare.R
import com.linagora.android.linshare.databinding.FragmentSharedSpaceDocumentBinding
import com.linagora.android.linshare.domain.model.sharedspace.SharedSpaceId
import com.linagora.android.linshare.domain.model.sharedspace.WorkGroupNodeId
import com.linagora.android.linshare.model.parcelable.getParentNodeId
import com.linagora.android.linshare.model.parcelable.toSharedSpaceId
import com.linagora.android.linshare.util.getViewModel
import com.linagora.android.linshare.view.MainNavigationFragment
import javax.inject.Inject

class SharedSpaceDocumentFragment : MainNavigationFragment() {

    companion object {
        const val NAVIGATION_INFO_KEY = "navigationInfo"
    }

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

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
        binding.lifecycleOwner = this
        binding.viewModel = sharedSpacesDocumentViewModel
        binding.navigationInfo = arguments.navigationInfo
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpSwipeRefreshLayout()
        getAllNodes(
            sharedSpaceId = arguments.navigationInfo.sharedSpaceIdParcelable.toSharedSpaceId(),
            parentNodeId = arguments.navigationInfo.getParentNodeId()
        )
    }

    private fun setUpSwipeRefreshLayout() {
        binding.swipeLayoutSharedSpace.setColorSchemeResources(R.color.colorPrimary)
    }

    private fun getAllNodes(sharedSpaceId: SharedSpaceId, parentNodeId: WorkGroupNodeId?) {
        sharedSpacesDocumentViewModel.getAllChildNodes(sharedSpaceId, parentNodeId)
    }
}
