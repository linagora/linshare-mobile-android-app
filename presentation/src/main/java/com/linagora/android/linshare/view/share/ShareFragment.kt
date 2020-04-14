package com.linagora.android.linshare.view.share

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import arrow.core.Either
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipDrawable
import com.linagora.android.linshare.R
import com.linagora.android.linshare.databinding.FragmentShareBinding
import com.linagora.android.linshare.domain.model.GenericUser
import com.linagora.android.linshare.domain.model.autocomplete.AutoCompletePattern
import com.linagora.android.linshare.domain.model.autocomplete.UserAutoCompleteResult
import com.linagora.android.linshare.domain.model.autocomplete.toGenericUser
import com.linagora.android.linshare.domain.model.document.Document
import com.linagora.android.linshare.domain.model.fullName
import com.linagora.android.linshare.domain.usecases.share.AddRecipient
import com.linagora.android.linshare.domain.usecases.share.ShareButtonClick
import com.linagora.android.linshare.domain.usecases.utils.Success
import com.linagora.android.linshare.model.parcelable.DocumentParcelable
import com.linagora.android.linshare.model.parcelable.toDocument
import com.linagora.android.linshare.util.dismissKeyboard
import com.linagora.android.linshare.util.generateCircleLetterAvatar
import com.linagora.android.linshare.util.getViewModel
import com.linagora.android.linshare.util.showKeyboard
import com.linagora.android.linshare.view.MainNavigationFragment
import com.linagora.android.linshare.view.dialog.OnRemoveRecipient
import kotlinx.android.synthetic.main.fragment_share.addRecipients
import kotlinx.coroutines.launch
import org.slf4j.LoggerFactory
import javax.inject.Inject

class ShareFragment : MainNavigationFragment() {

    companion object {
        private val LOGGER = LoggerFactory.getLogger(ShareFragment::class.java)

        const val SHARE_DOCUMENT_BUNDLE_KEY = "shareDocument"

        const val AUTO_COMPLETE_THRESHOLD = 3

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
        binding.addRecipients.apply {
            findFocus().showKeyboard()
            threshold = AUTO_COMPLETE_THRESHOLD

            doAfterTextChanged { pattern ->
                pattern?.toString()
                    ?.takeIf { it.isNotBlank() && it.length >= AUTO_COMPLETE_THRESHOLD }
                    ?.let { AutoCompletePattern(it) }
                    ?.let { search(it) }
            }

            onItemClickListener = AdapterView.OnItemClickListener { parent, _, position, _ ->
                text.clear()
                val selectedUser = parent.getItemAtPosition(position) as UserAutoCompleteResult
                shareFragmentViewModel.addRecipient(selectedUser.toGenericUser())
            }
        }
    }

    private fun bindingData(document: Document) {
        binding.document = document
    }

    private fun search(autoCompletePattern: AutoCompletePattern) {
        viewLifecycleOwner.lifecycleScope.launch {
            shareFragmentViewModel.queryChannel.send(autoCompletePattern)
        }
    }

    private fun afterShareDocument() {
        binding.addRecipients.dismissKeyboard()
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
        shareFragmentViewModel.recipients.observe(viewLifecycleOwner, Observer { recipients ->
            val hasRecipients = recipients.size.takeIf { it > 0 }
                ?.let { true }
                ?: false

            val hasDocument = binding.document != null

            binding.shareButton.isEnabled = hasRecipients && hasDocument
        })
    }

    private fun reactToViewEvent(viewEvent: Success.ViewEvent) {
        when (viewEvent) {
            is AddRecipient -> addRecipientView(viewEvent.user)
            is ShareButtonClick -> afterShareDocument()
        }
        shareFragmentViewModel.dispatchState(Either.right(Success.Idle))
    }

    private fun addRecipientView(user: GenericUser) {
        val recipientChip = createRecipientChip(user, shareFragmentViewModel::removeRecipient)
        binding.recipientContainer.addView(recipientChip, 0)
    }

    private fun createRecipientChip(genericUser: GenericUser, onRemoveRecipient: OnRemoveRecipient): Chip {
        return Chip(requireContext()).apply {

            setChipDrawable(ChipDrawable.createFromAttributes(
                requireContext(),
                RECIPIENT_ATTRIBUTES,
                NO_RECIPIENT_ATTRIBUTES_RESOURCE,
                R.style.RecipientChip
            ))

            val iconTint = ContextCompat.getColor(requireContext(), R.color.colorAccent)
            val icon = genericUser.generateCircleLetterAvatar(requireContext())
                .also { DrawableCompat.setTint(it, iconTint) }

            chipIcon = icon
            text = genericUser.fullName() ?: genericUser.mail

            tag = genericUser

            setOnCloseIconClickListener {
                binding.recipientContainer.removeView(it)
                onRemoveRecipient(it.tag as GenericUser)
            }
        }
    }

    private fun clearAutoCompleteFocus() {
        addRecipients.dismissKeyboard()
        addRecipients.clearFocus()
    }

    override fun onPause() {
        clearAutoCompleteFocus()
        super.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        shareFragmentViewModel.resetRecipients()
    }
}
