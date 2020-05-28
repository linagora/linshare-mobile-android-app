package com.linagora.android.linshare.view.share

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import arrow.core.Either
import com.linagora.android.linshare.R
import com.linagora.android.linshare.databinding.FragmentShareBinding
import com.linagora.android.linshare.domain.model.GenericUser
import com.linagora.android.linshare.domain.model.autocomplete.AutoCompletePattern
import com.linagora.android.linshare.domain.model.autocomplete.AutoCompleteResult
import com.linagora.android.linshare.domain.model.autocomplete.MailingList
import com.linagora.android.linshare.domain.model.autocomplete.MailingListAutoCompleteResult
import com.linagora.android.linshare.domain.model.autocomplete.SimpleAutoCompleteResult
import com.linagora.android.linshare.domain.model.autocomplete.UserAutoCompleteResult
import com.linagora.android.linshare.domain.model.autocomplete.toGenericUser
import com.linagora.android.linshare.domain.model.autocomplete.toMailingList
import com.linagora.android.linshare.domain.model.document.Document
import com.linagora.android.linshare.domain.usecases.share.AddMailingList
import com.linagora.android.linshare.domain.usecases.share.AddRecipient
import com.linagora.android.linshare.domain.usecases.share.ShareButtonClick
import com.linagora.android.linshare.domain.usecases.utils.Success
import com.linagora.android.linshare.model.parcelable.DocumentParcelable
import com.linagora.android.linshare.model.parcelable.toDocument
import com.linagora.android.linshare.util.binding.addMailingListView
import com.linagora.android.linshare.util.binding.addRecipientView
import com.linagora.android.linshare.util.binding.initView
import com.linagora.android.linshare.util.binding.onSelectedRecipient
import com.linagora.android.linshare.util.binding.queryAfterTextChange
import com.linagora.android.linshare.util.dismissKeyboard
import com.linagora.android.linshare.util.getViewModel
import com.linagora.android.linshare.view.MainNavigationFragment
import kotlinx.coroutines.launch
import org.slf4j.LoggerFactory
import javax.inject.Inject

class ShareFragment : MainNavigationFragment() {

    companion object {
        private val LOGGER = LoggerFactory.getLogger(ShareFragment::class.java)

        const val SHARE_DOCUMENT_BUNDLE_KEY = "shareDocument"

        val RECIPIENT_ATTRIBUTES = null

        const val NO_RECIPIENT_ATTRIBUTES_RESOURCE = 0
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
        initViewModel()
        return binding.root
    }

    private fun initViewModel() {
        shareFragmentViewModel = getViewModel(viewModelFactory)
        binding.lifecycleOwner = this
        binding.shareViewModel = shareFragmentViewModel

        observeViewState()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindingShareDocument()
        initAutoComplete()
        observeRecipients()
    }

    override fun configureToolbar(toolbar: Toolbar) {
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back)
    }

    private fun bindingShareDocument() {
        val bundle = requireArguments()
        bundle.getParcelable<DocumentParcelable>(SHARE_DOCUMENT_BUNDLE_KEY)
            ?.toDocument()
            ?.let { bindingData(it) }
    }

    private fun initAutoComplete() {
        with(binding.addRecipientContainer) {
            initView()
            queryAfterTextChange(this@ShareFragment::search)
            onSelectedRecipient(this@ShareFragment::reactOnSelectedSuggestion)
        }
    }

    private fun reactOnSelectedSuggestion(autoCompleteResult: AutoCompleteResult) {
        when (autoCompleteResult) {
            is UserAutoCompleteResult -> shareFragmentViewModel.addRecipient(autoCompleteResult.toGenericUser())
            is SimpleAutoCompleteResult -> shareFragmentViewModel.addRecipient(autoCompleteResult.toGenericUser())
            is MailingListAutoCompleteResult -> shareFragmentViewModel.addMailingList(autoCompleteResult.toMailingList())
        }
    }

    private fun bindingData(document: Document) {
        binding.document = document
    }

    private fun search(autoCompletePattern: AutoCompletePattern) {
        viewLifecycleOwner.lifecycleScope.launch {
            shareFragmentViewModel.recipientsManager.query(autoCompletePattern)
        }
    }

    private fun afterShareDocument() {
        binding.addRecipientContainer.addRecipients.dismissKeyboard()
        backToPreviousScreen()
    }

    private fun backToPreviousScreen() {
        findNavController().navigateUp()
    }

    private fun observeViewState() {
        shareFragmentViewModel.viewState.observe(viewLifecycleOwner, Observer { state ->
            state.map { success -> when (success) {
                is Success.ViewEvent -> reactToViewEvent(success)
            } }
        })
    }

    private fun observeRecipients() {
        shareFragmentViewModel.recipientsManager.shareReceiverCount.observe(viewLifecycleOwner, Observer { recipientsCount ->
            val hasRecipients = recipientsCount.takeIf { it > 0 }
                ?.let { true }
                ?: false

            val hasDocument = binding.document != null

            binding.shareButton.isEnabled = hasRecipients && hasDocument
        })
    }

    private fun reactToViewEvent(viewEvent: Success.ViewEvent) {
        when (viewEvent) {
            is AddRecipient -> addRecipientView(viewEvent.user)
            is AddMailingList -> addMailingListView(viewEvent.mailingList)
            is ShareButtonClick -> afterShareDocument()
        }
        shareFragmentViewModel.dispatchState(Either.right(Success.Idle))
    }

    private fun addRecipientView(user: GenericUser) {
        binding.addRecipientContainer
            .addRecipientView(requireContext(), user, shareFragmentViewModel::removeRecipient)
    }

    private fun addMailingListView(mailingList: MailingList) {
        binding.addRecipientContainer
            .addMailingListView(requireContext(), mailingList, shareFragmentViewModel::removeMailingList
        )
    }

    private fun clearAutoCompleteFocus() {
        with(binding.addRecipientContainer) {
            addRecipients.dismissKeyboard()
            addRecipients.clearFocus()
        }
    }

    override fun onPause() {
        clearAutoCompleteFocus()
        super.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        shareFragmentViewModel.resetRecipientManager()
    }
}
