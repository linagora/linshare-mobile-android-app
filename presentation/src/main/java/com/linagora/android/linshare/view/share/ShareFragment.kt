/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 *
 * Copyright (C) 2020 LINAGORA
 *
 * This program is free software: you can redistribute it and/or modify it under the
 * terms of the GNU Affero General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later version,
 * provided you comply with the Additional Terms applicable for LinShare software by
 * Linagora pursuant to Section 7 of the GNU Affero General Public License,
 * subsections (b), (c), and (e), pursuant to which you must notably (i) retain the
 * display in the interface of the “LinShare™” trademark/logo, the "Libre & Free" mention,
 * the words “You are using the Free and Open Source version of LinShare™, powered by
 * Linagora © 2009–2020. Contribute to Linshare R&D by subscribing to an Enterprise
 * offer!”. You must also retain the latter notice in all asynchronous messages such as
 * e-mails sent with the Program, (ii) retain all hypertext links between LinShare and
 * http://www.linshare.org, between linagora.com and Linagora, and (iii) refrain from
 * infringing Linagora intellectual property rights over its trademarks and commercial
 * brands. Other Additional Terms apply, see
 * <http://www.linshare.org/licenses/LinShare-License_AfferoGPL-v3.pdf>
 * for more details.
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for
 * more details.
 * You should have received a copy of the GNU Affero General Public License and its
 * applicable Additional Terms for LinShare along with this program. If not, see
 * <http://www.gnu.org/licenses/> for the GNU Affero General Public License version
 *  3 and <http://www.linshare.org/licenses/LinShare-License_AfferoGPL-v3.pdf> for
 *  the Additional Terms applicable to LinShare software.
 */

package com.linagora.android.linshare.view.share

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
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
import com.linagora.android.linshare.domain.model.properties.PreviousUserPermissionAction.DENIED
import com.linagora.android.linshare.domain.usecases.share.AddMailingList
import com.linagora.android.linshare.domain.usecases.share.AddRecipient
import com.linagora.android.linshare.domain.usecases.share.ShareButtonClick
import com.linagora.android.linshare.domain.usecases.utils.Success
import com.linagora.android.linshare.domain.utils.NoOp
import com.linagora.android.linshare.model.parcelable.DocumentParcelable
import com.linagora.android.linshare.model.parcelable.toDocument
import com.linagora.android.linshare.model.permission.PermissionResult
import com.linagora.android.linshare.model.properties.RuntimePermissionRequest.ShouldShowReadContact
import com.linagora.android.linshare.util.binding.addMailingListView
import com.linagora.android.linshare.util.binding.addRecipientView
import com.linagora.android.linshare.util.binding.initView
import com.linagora.android.linshare.util.binding.onSelectedRecipient
import com.linagora.android.linshare.util.binding.queryAfterTextChange
import com.linagora.android.linshare.util.dismissKeyboard
import com.linagora.android.linshare.util.getViewModel
import com.linagora.android.linshare.view.MainActivityViewModel
import com.linagora.android.linshare.view.MainNavigationFragment
import com.linagora.android.linshare.view.ReadContactPermissionRequestCode
import kotlinx.coroutines.launch
import org.slf4j.LoggerFactory

class ShareFragment : MainNavigationFragment() {

    companion object {
        private val LOGGER = LoggerFactory.getLogger(ShareFragment::class.java)

        const val SHARE_DOCUMENT_BUNDLE_KEY = "shareDocument"

        val RECIPIENT_ATTRIBUTES = null

        const val NO_RECIPIENT_ATTRIBUTES_RESOURCE = 0
    }

    private val mainActivityViewModel: MainActivityViewModel
            by activityViewModels { viewModelFactory }

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
        binding.lifecycleOwner = viewLifecycleOwner
        binding.shareViewModel = shareFragmentViewModel

        observeViewState()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindingShareDocument()
        initAutoComplete()
        observeRecipients()
        observeRequestPermission()
    }

    override fun configureToolbar(toolbar: Toolbar) {
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back)
    }

    override fun onResume() {
        super.onResume()
        showReadContactPermissionRequest()
    }

    private fun bindingShareDocument() {
        val bundle = requireArguments()
        bundle.getParcelable<DocumentParcelable>(SHARE_DOCUMENT_BUNDLE_KEY)
            ?.toDocument()
            ?.let(this@ShareFragment::bindingData)
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

    private fun observeRequestPermission() {
        mainActivityViewModel.shouldShowPermissionRequestState.observe(viewLifecycleOwner, Observer {
            if (it is ShouldShowReadContact) {
                requestPermissions(
                    arrayOf(Manifest.permission.READ_CONTACTS),
                    ReadContactPermissionRequestCode.code
                )
            }
        })
    }

    private fun showReadContactPermissionRequest() {
        if (needToShowReadContactPermissionRequest()) {
            mainActivityViewModel.shouldShowReadContactPermissionRequest(requireActivity())
        }
    }

    private fun needToShowReadContactPermissionRequest(): Boolean {
        return mainActivityViewModel.checkReadContactPermission(requireContext()) == PermissionResult.PermissionDenied
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        LOGGER.info("onRequestPermissionsResult(): $requestCode")
        when (requestCode) {
            ReadContactPermissionRequestCode.code -> Either.cond(
                test = grantResults.all { grantResults -> grantResults == PackageManager.PERMISSION_GRANTED },
                ifTrue = { NoOp },
                ifFalse = { mainActivityViewModel.setActionForReadContactPermissionRequest(DENIED) }
            )
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
