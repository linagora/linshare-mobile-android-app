package com.linagora.android.linshare.view.receivedshares

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.linagora.android.linshare.databinding.FragmentReceivedSharesBinding
import com.linagora.android.linshare.util.getViewModel
import com.linagora.android.linshare.view.MainNavigationFragment
import javax.inject.Inject

class ReceivedSharesFragment : MainNavigationFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var receivedSharesViewModel: ReceivedSharesViewModel

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
}
