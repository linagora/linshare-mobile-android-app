package com.linagora.android.linshare.view.sharedspace.details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.linagora.android.linshare.R
import com.linagora.android.linshare.databinding.FragmentSharedSpaceMemberBinding
import com.linagora.android.linshare.domain.model.sharedspace.SharedSpaceId
import com.linagora.android.linshare.domain.usecases.sharedspace.OpenAddMembers
import com.linagora.android.linshare.domain.usecases.utils.Success
import com.linagora.android.linshare.model.parcelable.toParcelable
import com.linagora.android.linshare.util.getParentViewModel
import com.linagora.android.linshare.util.getViewModel
import dagger.android.support.DaggerFragment
import javax.inject.Inject

class SharedSpaceMembersFragment(private val sharedSpaceId: SharedSpaceId) : DaggerFragment() {

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

    private fun initViewModel(binding: FragmentSharedSpaceMemberBinding) {
        sharedSpaceDetailsViewModel = getParentViewModel(viewModelFactory)
        sharedSpaceMemberViewModel = getViewModel(viewModelFactory)
        binding.viewModel = sharedSpaceMemberViewModel
        observeViewState()
    }

    private fun observeViewState() {
        sharedSpaceDetailsViewModel.viewState.observe(viewLifecycleOwner, Observer { state ->
            state.map { success -> when (success) {
                is Success.ViewEvent -> reactToViewEvent(success)
            } }
        })
    }

    private fun reactToViewEvent(viewEvent: Success.ViewEvent) {
        when (viewEvent) {
            is OpenAddMembers -> navigateToAddMembersFragment(viewEvent.sharedSpaceId)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpSwipeRefreshLayout()
        sharedSpaceMemberViewModel.getAllMembers(sharedSpaceId)
    }

    private fun setUpSwipeRefreshLayout() {
        binding.swipeLayoutMember.setColorSchemeResources(R.color.colorPrimary)
    }

    private fun navigateToAddMembersFragment(sharedSpaceId: SharedSpaceId) {
        val action = SharedSpaceDetailsFragmentDirections
            .actionNavigationSharedSpaceToSharedSpaceAddMemberFragment(sharedSpaceId.toParcelable())
        findNavController().navigate(action)
    }
}
