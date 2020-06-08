package com.linagora.android.linshare.view.sharedspace

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.linagora.android.linshare.databinding.DialogSharedSpaceContextMenuBinding
import com.linagora.android.linshare.domain.model.sharedspace.SharedSpaceNodeNested
import com.linagora.android.linshare.util.getParentViewModel
import com.linagora.android.linshare.view.dialog.DaggerBottomSheetDialogFragment
import javax.inject.Inject

class SharedSpaceContextMenuDialog(
    private val sharedSpaceNodeNested: SharedSpaceNodeNested
) : DaggerBottomSheetDialogFragment() {

    companion object {
        const val TAG = "sharedSpaceContextMenuDialog"
    }

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    @Inject
    lateinit var sharedSpaceViewModel: SharedSpaceViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = DialogSharedSpaceContextMenuBinding
            .inflate(inflater, container, false)
        initViewModel(binding)
        return binding.root
    }

    private fun initViewModel(binding: DialogSharedSpaceContextMenuBinding) {
        sharedSpaceViewModel = getParentViewModel(viewModelFactory)
        binding.sharedSpaceNodeNested = sharedSpaceNodeNested
        binding.itemContextMenu = sharedSpaceViewModel.sharedSpaceItemContextMenu
    }
}
