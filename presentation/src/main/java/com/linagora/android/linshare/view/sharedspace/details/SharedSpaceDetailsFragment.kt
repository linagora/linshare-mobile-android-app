package com.linagora.android.linshare.view.sharedspace.details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import com.linagora.android.linshare.R
import com.linagora.android.linshare.databinding.FragmentSharedSpaceDetailsBinding
import com.linagora.android.linshare.model.parcelable.SharedSpaceIdParcelable
import com.linagora.android.linshare.model.parcelable.toSharedSpaceId
import com.linagora.android.linshare.util.getViewModel
import com.linagora.android.linshare.view.MainNavigationFragment
import javax.inject.Inject

class SharedSpaceDetailsFragment : MainNavigationFragment() {

    companion object {
        private val DETAILS_TITLES = arrayOf(
            R.string.members
        )
    }

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var viewModel: SharedSpaceDetailsViewModel

    private lateinit var binding: FragmentSharedSpaceDetailsBinding

    private val sharedSpaceDetailsArgs: SharedSpaceDetailsFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSharedSpaceDetailsBinding.inflate(inflater, container, false)
            .apply { lifecycleOwner = viewLifecycleOwner }
        initViewModel(binding)
        return binding.root
    }

    private fun initViewModel(binding: FragmentSharedSpaceDetailsBinding) {
        viewModel = getViewModel(viewModelFactory)
        binding.viewModel = viewModel
        binding.sharedSpaceId = sharedSpaceDetailsArgs.sharedSpaceId.toSharedSpaceId()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.run {
            viewpager.adapter = DetailsPageAdapter()
            TabLayoutMediator(tabsDetails, viewpager) { tab, position ->
                tab.text = getString(DETAILS_TITLES[position])
            }.attach()
        }
        getCurrentSharedSpace(sharedSpaceDetailsArgs.sharedSpaceId)
    }

    override fun configureToolbar(toolbar: Toolbar) {
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back)
    }

    private fun getCurrentSharedSpace(sharedSpaceIdParcelable: SharedSpaceIdParcelable) {
        viewModel.getCurrentSharedSpace(sharedSpaceIdParcelable.toSharedSpaceId())
    }

    inner class DetailsPageAdapter() : FragmentStateAdapter(this) {

        override fun getItemCount() = DETAILS_TITLES.size

        override fun createFragment(position: Int): Fragment {
            require(position < itemCount) { "page number is not supported" }
            return SharedSpaceMembersFragment(sharedSpaceDetailsArgs.sharedSpaceId.toSharedSpaceId())
        }
    }
}
