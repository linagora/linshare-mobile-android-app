package com.linagora.android.linshare.view.sharedspace.details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.linagora.android.linshare.R
import com.linagora.android.linshare.databinding.FragmentSharedSpaceMemberBinding
import com.linagora.android.linshare.domain.model.sharedspace.SharedSpaceId
import com.linagora.android.linshare.util.getParentViewModel
import dagger.android.support.DaggerFragment
import javax.inject.Inject

class SharedSpaceMembersFragment(private val sharedSpaceId: SharedSpaceId) : DaggerFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var sharedSpaceDetailsViewModel: SharedSpaceDetailsViewModel

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
        binding.viewModel = sharedSpaceDetailsViewModel
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpSwipeRefreshLayout()
        sharedSpaceDetailsViewModel.getAllMembers(sharedSpaceId)
    }

    private fun setUpSwipeRefreshLayout() {
        binding.swipeLayoutMember.setColorSchemeResources(R.color.colorPrimary)
    }
}
