package com.linagora.android.linshare.view.sharedspacedocument

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.linagora.android.linshare.databinding.DialogSharedSpaceFolderContextMenuBinding
import com.linagora.android.linshare.domain.model.sharedspace.WorkGroupFolder
import com.linagora.android.linshare.util.getParentViewModel
import com.linagora.android.linshare.view.dialog.DaggerBottomSheetDialogFragment
import javax.inject.Inject

class SharedSpaceFolderContextMenuDialog(private val workGroupFolder: WorkGroupFolder) :
    DaggerBottomSheetDialogFragment() {
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    @Inject
    lateinit var sharedSpaceDocumentViewModel: SharedSpaceDocumentViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = DialogSharedSpaceFolderContextMenuBinding
            .inflate(inflater, container, false)
        initViewModel(binding)
        return binding.root
    }

    private fun initViewModel(binding: DialogSharedSpaceFolderContextMenuBinding) {
        sharedSpaceDocumentViewModel = getParentViewModel(viewModelFactory)
        binding.workGroupNode = workGroupFolder
        binding.itemContextMenu = sharedSpaceDocumentViewModel.itemContextMenu
        binding.sharedSpace = sharedSpaceDocumentViewModel.currentSharedSpace.value
    }
}
