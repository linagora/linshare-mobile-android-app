package com.linagora.android.linshare.view.receivedshares

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.linagora.android.linshare.R
import com.linagora.android.linshare.databinding.FragmentReceivedSharesBinding
import com.linagora.android.linshare.util.getViewModel
import com.linagora.android.linshare.view.MainNavigationFragment
import kotlinx.android.synthetic.main.fragment_received_shares.swipeLayoutReceivedList
import org.slf4j.LoggerFactory
import javax.inject.Inject

class ReceivedSharesFragment : MainNavigationFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var receivedSharesViewModel: ReceivedSharesViewModel

    companion object {
        private val LOGGER = LoggerFactory.getLogger(ReceivedSharesFragment::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentReceivedSharesBinding.inflate(inflater, container, false)
        initViewModel(binding)
        return binding.root
    }

    private fun initViewModel(binding: FragmentReceivedSharesBinding) {
        receivedSharesViewModel = getViewModel(viewModelFactory)
        binding.lifecycleOwner = this
        binding.viewModel = receivedSharesViewModel
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpSwipeRefreshLayout()
        getReceivedList()
    }

    private fun setUpSwipeRefreshLayout() {
        swipeLayoutReceivedList.setColorSchemeResources(R.color.colorPrimary)
    }

    private fun getReceivedList() {
        LOGGER.info("getReceivedList")
        receivedSharesViewModel.getReceivedList()
    }
}
