package com.linagora.android.linshare.view.myspace

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.linagora.android.linshare.databinding.DialogInfoDocumentBinding
import com.linagora.android.linshare.domain.model.document.Document
import com.linagora.android.linshare.view.dialog.DaggerBottomSheetDialogFragment
import javax.inject.Inject

class InfoDocumentDialog(private val document: Document) : DaggerBottomSheetDialogFragment() {
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    @Inject
    lateinit var mySpaceViewModel: MySpaceViewModel

    private fun initViewModel(binding: DialogInfoDocumentBinding) {
        mySpaceViewModel= ViewModelProviders.of(this.parentFragment!!, viewModelFactory).get(MySpaceViewModel::class.java)

        binding.document = document
        binding.viewModel = mySpaceViewModel
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = DialogInfoDocumentBinding.inflate(inflater, container, false)
        initViewModel(binding)
        return binding.root
    }
}
