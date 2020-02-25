package com.linagora.android.linshare.view.myspace

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.linagora.android.linshare.databinding.DialogMySpaceContextMenuBinding
import com.linagora.android.linshare.domain.model.document.Document
import com.linagora.android.linshare.util.getParentViewModel
import com.linagora.android.linshare.view.dialog.DaggerBottomSheetDialogFragment
import javax.inject.Inject

class MySpaceContextMenuDialog(private val document: Document) : DaggerBottomSheetDialogFragment() {
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    @Inject
    lateinit var mySpaceViewModel: MySpaceViewModel

    private fun initViewModel(binding: DialogMySpaceContextMenuBinding) {
        mySpaceViewModel = getParentViewModel(viewModelFactory)

        binding.document = document
        binding.viewModel = mySpaceViewModel
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = DialogMySpaceContextMenuBinding.inflate(inflater, container, false)
        initViewModel(binding)
        return binding.root
    }
}
