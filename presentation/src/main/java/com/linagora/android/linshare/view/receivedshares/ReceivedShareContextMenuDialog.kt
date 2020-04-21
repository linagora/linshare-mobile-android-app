package com.linagora.android.linshare.view.receivedshares

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.linagora.android.linshare.databinding.DialogReceivedShareContextMenuBinding
import com.linagora.android.linshare.domain.model.share.Share
import com.linagora.android.linshare.util.getParentViewModel
import com.linagora.android.linshare.view.dialog.DaggerBottomSheetDialogFragment
import javax.inject.Inject

class ReceivedShareContextMenuDialog(private val share: Share) : DaggerBottomSheetDialogFragment() {
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    @Inject
    lateinit var receivedSharesViewModel: ReceivedSharesViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = DialogReceivedShareContextMenuBinding.inflate(inflater, container, false)
        initViewModel(binding)
        return binding.root
    }

    private fun initViewModel(binding: DialogReceivedShareContextMenuBinding) {
        receivedSharesViewModel = getParentViewModel(viewModelFactory)

        binding.share = share
        binding.downloadContextMenu = receivedSharesViewModel.downloadContextMenu
        binding.copyInMySpaceContextMenu = receivedSharesViewModel.copyInMySpaceContextMenu
    }
}
