package com.linagora.android.linshare.view.sharedspace

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModelProvider
import com.linagora.android.linshare.databinding.DialogCreateWorkgroupBinding
import com.linagora.android.linshare.domain.model.sharedspace.SharedSpaceNodeNested
import com.linagora.android.linshare.domain.model.workgroup.NameString
import com.linagora.android.linshare.util.getParentViewModel
import com.linagora.android.linshare.view.dialog.DaggerBottomSheetDialogFragment
import javax.inject.Inject

class CreateWorkGroupDialog(
    private val listSharedSpaceNodeNested: LiveData<List<SharedSpaceNodeNested>>
) : DaggerBottomSheetDialogFragment() {

    companion object {
        const val TAG = "createWorkGroupDialog"
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
        val binding = DialogCreateWorkgroupBinding.inflate(inflater, container, false)
        initViewModel(binding)
        return binding.root
    }

    private fun initViewModel(binding: DialogCreateWorkgroupBinding) {
        binding.lifecycleOwner = this
        sharedSpaceViewModel = getParentViewModel(viewModelFactory)
        binding.viewModel = sharedSpaceViewModel
        binding.listSharedSpaceNodeNested = listSharedSpaceNodeNested.value
        binding.nameWorkGroup.apply {
            doAfterTextChanged { text -> validEnterTextNameWorkGroup(text.toString()) }
        }
    }

    private fun validEnterTextNameWorkGroup(enterText: String) {
        enterText.let(::NameString).let(::sendEnterText)
    }

    private fun sendEnterText(name: NameString) {
        sharedSpaceViewModel.validName(name)
    }
}
