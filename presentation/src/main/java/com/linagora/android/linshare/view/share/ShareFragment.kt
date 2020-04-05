package com.linagora.android.linshare.view.share

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.linagora.android.linshare.R
import com.linagora.android.linshare.databinding.FragmentShareBinding
import com.linagora.android.linshare.domain.model.GenericUser
import com.linagora.android.linshare.domain.model.document.Document
import com.linagora.android.linshare.model.parcelable.DocumentParcelable
import com.linagora.android.linshare.model.parcelable.toDocument
import com.linagora.android.linshare.util.dismissKeyboard
import com.linagora.android.linshare.util.getViewModel
import com.linagora.android.linshare.view.MainNavigationFragment
import kotlinx.android.synthetic.main.fragment_share.addRecipients
import javax.inject.Inject

class ShareFragment : MainNavigationFragment() {

    companion object {
        const val SHARE_DOCUMENT_BUNDLE_KEY = "shareDocument"
    }

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var shareFragmentViewModel: ShareFragmentViewModel

    private lateinit var binding: FragmentShareBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentShareBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        initViewModel()
        return binding.root
    }

    private fun initViewModel() {
        shareFragmentViewModel = getViewModel(viewModelFactory)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindingShareDocument()
    }

    override fun configureToolbar(toolbar: Toolbar) {
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back)
    }

    private fun bindingShareDocument() {
        val bundle = requireArguments()
        bundle.getParcelable<DocumentParcelable>(SHARE_DOCUMENT_BUNDLE_KEY)
            ?.toDocument()
            ?.let {
                bindingData(it)
                setUpShareButton(it)
            }
    }

    private fun bindingData(document: Document) {
        binding.document = document
    }

    private fun setUpShareButton(document: Document) {
        binding.shareButton.isEnabled = true
        binding.shareButton.setOnClickListener {
            if (binding.addRecipients.text.isNotEmpty()) {
                val genericUser = GenericUser(addRecipients.text.toString())
                shareFragmentViewModel.share(listOf(genericUser), document)
                backToPreviousScreen()
            }
            it.dismissKeyboard()
        }
    }

    private fun backToPreviousScreen() {
        findNavController().navigateUp()
    }
}
