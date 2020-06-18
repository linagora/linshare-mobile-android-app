package com.linagora.android.linshare.view.sharedspace

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModelProvider
import arrow.core.Either
import com.linagora.android.linshare.databinding.DialogCreateWorkgroupBinding
import com.linagora.android.linshare.domain.model.sharedspace.SharedSpaceNodeNested
import com.linagora.android.linshare.domain.model.workgroup.NewNameRequest
import com.linagora.android.linshare.domain.usecases.utils.Failure
import com.linagora.android.linshare.domain.usecases.utils.Success
import com.linagora.android.linshare.view.dialog.DaggerBottomSheetDialogFragment
import com.linagora.android.linshare.view.dialog.NoOpCallback
import com.linagora.android.linshare.view.dialog.OnNegativeCallback
import com.linagora.android.linshare.view.dialog.OnNewNameRequestChange
import com.linagora.android.linshare.view.dialog.OnPositiveWithEnteredCharactersCallback
import javax.inject.Inject

class CreateWorkGroupDialog(
    private val listSharedSpaceNodeNestedData: LiveData<List<SharedSpaceNodeNested>>,
    private val onNegativeCallback: OnNegativeCallback = NoOpCallback,
    private val onCreateWorkGroup: OnPositiveWithEnteredCharactersCallback,
    private val onNewNameRequestChange: OnNewNameRequestChange,
    private val viewState: LiveData<Either<Failure, Success>>
) : DaggerBottomSheetDialogFragment() {

    companion object {
        const val TAG = "createWorkGroupDialog"
    }

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = DialogCreateWorkgroupBinding.inflate(inflater, container, false)
        initView(binding)
        return binding.root
    }

    private fun initView(binding: DialogCreateWorkgroupBinding) {
        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            listSharedSpaceNodeNested = listSharedSpaceNodeNestedData

            nameWorkGroup.apply {
                doAfterTextChanged { text ->
                    binding.newNameRequest = NewNameRequest(text.toString())
                    onNewNameRequest(text.toString()) }
            }
            cancelButton.setOnClickListener {
                onNegativeCallback.invoke(it)
                dismiss()
            }
            createButton.setOnClickListener {
                onCreateWorkGroup.invoke(binding.nameWorkGroup.text.toString())
                dismiss()
            }
            state = viewState
            executePendingBindings()
        }
    }

    private fun onNewNameRequest(enterText: String) {
        onNewNameRequestChange(enterText.let(::NewNameRequest))
    }
}
