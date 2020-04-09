package com.linagora.android.linshare.view.share

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.appcompat.widget.Toolbar
import androidx.core.widget.doAfterTextChanged
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipDrawable
import com.linagora.android.linshare.R
import com.linagora.android.linshare.databinding.FragmentShareBinding
import com.linagora.android.linshare.domain.model.GenericUser
import com.linagora.android.linshare.domain.model.autocomplete.AutoCompletePattern
import com.linagora.android.linshare.domain.model.autocomplete.UserAutoCompleteResult
import com.linagora.android.linshare.domain.model.document.Document
import com.linagora.android.linshare.model.parcelable.DocumentParcelable
import com.linagora.android.linshare.util.dismissKeyboard
import com.linagora.android.linshare.util.generateCircleLetterAvatar
import com.linagora.android.linshare.util.getViewModel
import com.linagora.android.linshare.view.MainNavigationFragment
import com.linagora.android.linshare.view.dialog.NoOpCallback
import com.linagora.android.linshare.view.dialog.OnRemoveRecipient
import kotlinx.android.synthetic.main.fragment_share.addRecipients
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.random.Random

class ShareFragment : MainNavigationFragment() {

    companion object {
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
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindingShareDocument()
        initAutoComplete()
        setUpAddButton()
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

    private fun initAutoComplete() {
        binding.addRecipients.apply {
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
                shareFragmentViewModel.onSelectedUserClick(selectedUser)
            }
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

    private fun search(autoCompletePattern: AutoCompletePattern) {
        viewLifecycleOwner.lifecycleScope.launch {
            shareFragmentViewModel.queryChannel.send(autoCompletePattern)
        }
    }

    private fun backToPreviousScreen() {
        findNavController().navigateUp()
    }

    private fun setUpAddButton() {
        binding.button.setOnClickListener {
            val name = Random.nextInt().takeIf { it % 3 == 0 }
                ?.let { "Dat ${it.toString().subSequence(IntRange(1, 5))}" }
                ?: "Dat Pham Dat Pham Dat Pham Dat Pham Dat Pham"
            val user = GenericUser("mail", name, name)
            val chip = createRecipientChip(user)
            binding.recipientContainer.addView(chip, 0)
        }
    }

    private fun createRecipientChip(genericUser: GenericUser, onRemoveRecipient: OnRemoveRecipient = NoOpCallback): Chip {
        return Chip(requireContext()).apply {

            setChipDrawable(ChipDrawable.createFromAttributes(
                requireContext(),
                RECIPIENT_ATTRIBUTES,
                NO_RECIPIENT_ATTRIBUTES_RESOURCE,
                R.style.RecipientChip)
            )

            val iconTint = ContextCompat.getColor(requireContext(), R.color.colorAccent)
            val icon = genericUser.generateCircleLetterAvatar(requireContext())
                .also { DrawableCompat.setTint(it, iconTint) }

            chipIcon = icon
            text = genericUser.firstName

            setOnCloseIconClickListener {
                binding.recipientContainer.removeView(it)
                onRemoveRecipient(it)
            }
        }
    }
}
