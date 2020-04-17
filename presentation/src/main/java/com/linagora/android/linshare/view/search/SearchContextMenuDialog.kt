package com.linagora.android.linshare.view.search

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

class SearchContextMenuDialog(private val document: Document) : DaggerBottomSheetDialogFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var searchViewModel: SearchViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = DialogDocumentContextMenuBinding.inflate(inflater, container, false)
        initViewModel(binding)
        return binding.root
    }

    private fun initViewModel(documentContextMenuBinding: DialogDocumentContextMenuBinding) {
        searchViewModel = getParentViewModel(viewModelFactory)
        documentContextMenuBinding.document = document
        documentContextMenuBinding.contextMenu = searchViewModel.itemContextMenu
        documentContextMenuBinding.downloadContextMenu = searchViewModel.downloadContextMenu
        documentContextMenuBinding.mySpaceItemAction = searchViewModel.mySpaceItemAction
    }
}
