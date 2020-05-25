package com.linagora.android.linshare.view.myspace

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.linagora.android.linshare.databinding.DialogDocumentContextMenuBinding
import com.linagora.android.linshare.domain.model.document.Document
import com.linagora.android.linshare.util.getParentViewModel
import com.linagora.android.linshare.view.dialog.DaggerBottomSheetDialogFragment
import javax.inject.Inject

class MySpaceContextMenuDialog(private val document: Document) : DaggerBottomSheetDialogFragment() {

    companion object {
        const val TAG = "mySpaceContextMenu"
    }

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    @Inject
    lateinit var mySpaceViewModel: MySpaceViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = DialogDocumentContextMenuBinding.inflate(inflater, container, false)
        initViewModel(binding)
        return binding.root
    }

    private fun initViewModel(binding: DialogDocumentContextMenuBinding) {
        mySpaceViewModel = getParentViewModel(viewModelFactory)

        binding.document = document
        binding.mySpaceItemAction = mySpaceViewModel.mySpaceItemAction
        binding.contextMenu = mySpaceViewModel.itemContextMenu
        binding.downloadContextMenu = mySpaceViewModel.downloadContextMenu
    }
}
