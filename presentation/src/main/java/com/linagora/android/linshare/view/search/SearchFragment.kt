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

package com.linagora.android.linshare.view.search

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView.OnQueryTextListener
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import arrow.core.Either
import com.linagora.android.linshare.R
import com.linagora.android.linshare.databinding.FragmentSearchBinding
import com.linagora.android.linshare.domain.model.document.Document
import com.linagora.android.linshare.domain.model.properties.PreviousUserPermissionAction.DENIED
import com.linagora.android.linshare.domain.model.search.QueryString
import com.linagora.android.linshare.domain.usecases.myspace.ContextMenuClick
import com.linagora.android.linshare.domain.usecases.myspace.DownloadClick
import com.linagora.android.linshare.domain.usecases.myspace.RemoveClick
import com.linagora.android.linshare.domain.usecases.myspace.RemoveDocumentSuccessViewState
import com.linagora.android.linshare.domain.usecases.myspace.ShareItemClick
import com.linagora.android.linshare.domain.usecases.utils.Success
import com.linagora.android.linshare.model.parcelable.toParcelable
import com.linagora.android.linshare.model.permission.PermissionResult
import com.linagora.android.linshare.model.properties.RuntimePermissionRequest
import com.linagora.android.linshare.util.dismissKeyboard
import com.linagora.android.linshare.util.getViewModel
import com.linagora.android.linshare.util.showKeyboard
import com.linagora.android.linshare.view.MainActivityViewModel
import com.linagora.android.linshare.view.MainNavigationFragment
import com.linagora.android.linshare.view.WriteExternalPermissionRequestCode
import com.linagora.android.linshare.view.myspace.ConfirmRemoveDocumentDialog
import com.linagora.android.linshare.view.share.ShareFragment
import kotlinx.android.synthetic.main.fragment_search.searchView
import kotlinx.android.synthetic.main.fragment_search.view.searchView
import kotlinx.coroutines.launch
import org.slf4j.LoggerFactory
import javax.inject.Inject

class SearchFragment : MainNavigationFragment() {

    companion object {
        private val LOGGER = LoggerFactory.getLogger(SearchFragment::class.java)
    }

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val mainActivityViewModel: MainActivityViewModel
            by activityViewModels { viewModelFactory }

    private lateinit var binding: FragmentSearchBinding

    private lateinit var searchViewModel: SearchViewModel

    private lateinit var searchContextMenuDialog: SearchContextMenuDialog

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSearchBinding.inflate(inflater, container, false)
        initViewModel(binding)
        return binding.root
    }

    private fun initViewModel(searchBinding: FragmentSearchBinding) {
        searchViewModel = getViewModel(viewModelFactory)
        searchBinding.lifecycleOwner = this
        searchBinding.searchViewModel = searchViewModel

        observeViewState()
        observeRequestPermission()
    }

    private fun observeViewState() {
        searchViewModel.viewState.observe(viewLifecycleOwner, Observer {
            it.map { success -> when (success) {
                is Success.ViewEvent -> reactToViewEvent(success)
                is Success.ViewState -> reactToViewState(success)
            } }
        })
    }

    private fun reactToViewState(viewState: Success.ViewState) {
        when (viewState) {
            is RemoveDocumentSuccessViewState -> search(searchView.query.toString())
        }
    }

    private fun reactToViewEvent(viewEvent: Success.ViewEvent) {
        when (viewEvent) {
            is ContextMenuClick -> {
                clearSearchFocus()
                showContextMenu(viewEvent.document)
            }
            is DownloadClick -> handleDownloadDocument(viewEvent.document)
            is RemoveClick -> confirmRemoveDocument(viewEvent.document)
            is ShareItemClick -> navigateToShare(viewEvent.document)
        }
        searchViewModel.dispatchState(Either.right(Success.Idle))
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        LOGGER.info("onViewCreated()")
        super.onViewCreated(view, savedInstanceState)

        setUpSearchView()
    }

    private fun setUpSearchView() {
        binding.toolbar.searchView.apply {

            setOnQueryTextListener(object : OnQueryTextListener {
                override fun onQueryTextSubmit(query: String): Boolean {
                    this@apply.dismissKeyboard()
                    return true
                }

                override fun onQueryTextChange(newText: String): Boolean {
                    LOGGER.info("onQueryTextChange() $newText")
                    search(newText)
                    return true
                }
            })

            setOnQueryTextFocusChangeListener { view, hasFocus ->
                if (hasFocus) {
                    view.findFocus().showKeyboard()
                }
            }
            requestFocus()
        }
    }

    private fun search(query: String) {
        query.takeIf { it.isNotBlank() }
            ?.let(::QueryString)
            ?.let(this@SearchFragment::sendQueryString)
    }

    private fun sendQueryString(query: QueryString) {
        viewLifecycleOwner.lifecycleScope.launch {
            searchViewModel.queryChannel.send(query)
        }
    }

    override fun configureToolbar(toolbar: Toolbar) {
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_in_white_bg)
    }

    override fun onPause() {
        searchView.dismissKeyboard()
        super.onPause()
    }

    private fun showContextMenu(document: Document) {
        searchContextMenuDialog = SearchContextMenuDialog(document)
        searchContextMenuDialog.show(childFragmentManager, "search context menu dialog")
    }

    private fun clearSearchFocus() {
        searchView.dismissKeyboard()
        searchView.clearFocus()
    }

    private fun observeRequestPermission() {
        mainActivityViewModel.shouldShowPermissionRequestState.observe(viewLifecycleOwner, Observer {
            if (it is RuntimePermissionRequest.ShouldShowWriteStorage) {
                requestPermissions(
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    WriteExternalPermissionRequestCode.code
                )
            }
        })
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        LOGGER.info("onRequestPermissionsResult() $requestCode")
        when (requestCode) {
            WriteExternalPermissionRequestCode.code -> { Either.cond(
                test = grantResults.all { grantResult -> grantResult == PackageManager.PERMISSION_GRANTED },
                ifTrue = { searchViewModel.getDownloadingDocument()?.let { download(it) } },
                ifFalse = { mainActivityViewModel.setActionForWriteStoragePermissionRequest(DENIED) })
            }
        }
    }

    private fun handleDownloadDocument(document: Document) {
        searchContextMenuDialog.dismiss()
        when (mainActivityViewModel.checkWriteStoragePermission(requireContext())) {
            PermissionResult.PermissionGranted -> { download(document) }
            else -> { shouldRequestWriteStoragePermission() }
        }
    }

    private fun download(document: Document) {
        LOGGER.info("download() $document")
        mainActivityViewModel.currentAuthentication.value
            ?.let { authentication ->
                searchViewModel.downloadDocument(authentication.credential, authentication.token, document)
            }
    }

    private fun shouldRequestWriteStoragePermission() {
        mainActivityViewModel.shouldShowWriteStoragePermissionRequest(requireActivity())
    }

    private fun confirmRemoveDocument(document: Document) {
        searchContextMenuDialog.dismiss()
        ConfirmRemoveDocumentDialog(
            document = document,
            title = getString(R.string.confirm_delete_file, document.name),
            negativeText = getString(R.string.cancel),
            positiveText = getString(R.string.remove),
            onPositiveCallback = { handleRemoveDocument(document) }
        ).show(childFragmentManager, "confirm_remove_document_dialog")
    }

    private fun handleRemoveDocument(document: Document) {
        searchViewModel.removeDocument(document)
    }

    private fun navigateToShare(document: Document) {
        searchContextMenuDialog.dismiss()
        val bundle = Bundle()
        bundle.putParcelable(ShareFragment.SHARE_DOCUMENT_BUNDLE_KEY, document.toParcelable())
        findNavController().navigate(R.id.navigationShare, bundle)
    }
}
