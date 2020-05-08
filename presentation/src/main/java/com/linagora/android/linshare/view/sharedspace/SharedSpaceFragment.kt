package com.linagora.android.linshare.view.sharedspace

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import arrow.core.Either
import com.linagora.android.linshare.R
import com.linagora.android.linshare.databinding.FragmentSharedSpaceBinding
import com.linagora.android.linshare.domain.usecases.sharedspace.SharedSpaceContextMenuClick
import com.linagora.android.linshare.domain.usecases.sharedspace.SharedSpaceItemClick
import com.linagora.android.linshare.domain.usecases.utils.Success
import com.linagora.android.linshare.util.getViewModel
import com.linagora.android.linshare.view.MainNavigationFragment
import kotlinx.android.synthetic.main.fragment_shared_space.swipeLayoutSharedSpace
import org.slf4j.LoggerFactory
import javax.inject.Inject

class SharedSpaceFragment : MainNavigationFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

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
        binding.lifecycleOwner = this
        binding.viewModel = sharedSpaceViewModel
        observeViewState()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpSwipeRefreshLayout()
        getSharedSpace()
    }

    private fun observeViewState() {
        sharedSpaceViewModel.viewState.observe(viewLifecycleOwner, Observer {
            it.map { success -> when (success) {
                is Success.ViewEvent -> reactToViewEvent(success)
            } }
        })
    }

    private fun reactToViewEvent(viewEvent: Success.ViewEvent) {
        when (viewEvent) {
            is SharedSpaceContextMenuClick -> {}
            is SharedSpaceItemClick -> {}
        }
        sharedSpaceViewModel.dispatchState(Either.right(Success.Idle))
    }

    private fun setUpSwipeRefreshLayout() {
        swipeLayoutSharedSpace.setColorSchemeResources(R.color.colorPrimary)
    }

    private fun getSharedSpace() {
        LOGGER.info("getSharedSpaces")
        sharedSpaceViewModel.getSharedSpace()
    }
}
