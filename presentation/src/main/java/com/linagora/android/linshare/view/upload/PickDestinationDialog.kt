package com.linagora.android.linshare.view.upload

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.linagora.android.linshare.databinding.DialogPickDestinationBinding
import com.linagora.android.linshare.util.getParentViewModel
import com.linagora.android.linshare.view.dialog.DaggerBottomSheetDialogFragment
import javax.inject.Inject

class PickDestinationDialog : DaggerBottomSheetDialogFragment() {

    companion object {
        const val TAG = "PickDestinationDialog"
    }

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    @Inject
    lateinit var uploadFragmentViewModel: UploadFragmentViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = DialogPickDestinationBinding.inflate(inflater, container, false)
        initViewModel(binding)
        return binding.root
    }

    private fun initViewModel(binding: DialogPickDestinationBinding) {
        uploadFragmentViewModel = getParentViewModel(viewModelFactory)
        binding.viewModel = uploadFragmentViewModel
    }
}
