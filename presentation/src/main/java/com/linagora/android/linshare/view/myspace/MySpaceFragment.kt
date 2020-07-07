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

package com.linagora.android.linshare.view.myspace

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import arrow.core.Either
import com.google.android.material.snackbar.Snackbar
import com.linagora.android.linshare.R
import com.linagora.android.linshare.databinding.FragmentMySpaceBinding
import com.linagora.android.linshare.domain.model.OperatorType
import com.linagora.android.linshare.domain.model.document.Document
import com.linagora.android.linshare.domain.model.properties.PreviousUserPermissionAction.DENIED
import com.linagora.android.linshare.domain.usecases.myspace.ContextMenuClick
import com.linagora.android.linshare.domain.usecases.myspace.DownloadClick
import com.linagora.android.linshare.domain.usecases.myspace.RemoveClick
import com.linagora.android.linshare.domain.usecases.myspace.RemoveDocumentSuccessViewState
import com.linagora.android.linshare.domain.usecases.myspace.SearchButtonClick
import com.linagora.android.linshare.domain.usecases.myspace.ShareItemClick
import com.linagora.android.linshare.domain.usecases.myspace.UploadButtonBottomBarClick
import com.linagora.android.linshare.domain.usecases.utils.Failure
import com.linagora.android.linshare.domain.usecases.utils.Failure.CannotExecuteWithoutNetwork
import com.linagora.android.linshare.domain.usecases.utils.Success
import com.linagora.android.linshare.domain.usecases.utils.Success.Idle
import com.linagora.android.linshare.model.parcelable.toParcelable
import com.linagora.android.linshare.model.permission.PermissionResult
import com.linagora.android.linshare.model.properties.RuntimePermissionRequest.ShouldShowWriteStorage
import com.linagora.android.linshare.util.dismissDialogFragmentByTag
import com.linagora.android.linshare.util.getViewModel
import com.linagora.android.linshare.util.openFilePicker
import com.linagora.android.linshare.view.MainActivityViewModel
import com.linagora.android.linshare.view.MainNavigationFragment
import com.linagora.android.linshare.view.Navigation.UploadType.INSIDE_APP
import com.linagora.android.linshare.view.OpenFilePickerRequestCode
import com.linagora.android.linshare.view.WriteExternalPermissionRequestCode
import com.linagora.android.linshare.view.share.ShareFragment.Companion.SHARE_DOCUMENT_BUNDLE_KEY
import com.linagora.android.linshare.view.upload.UploadFragmentArgs
import com.linagora.android.linshare.view.widget.errorLayout
import kotlinx.android.synthetic.main.fragment_my_space.swipeLayoutMySpace
import org.slf4j.LoggerFactory
import javax.inject.Inject

class MySpaceFragment : MainNavigationFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val mainActivityViewModel: MainActivityViewModel
            by activityViewModels { viewModelFactory }

    private lateinit var mySpaceViewModel: MySpaceViewModel

    private lateinit var binding: FragmentMySpaceBinding

    companion object {
        private val LOGGER = LoggerFactory.getLogger(MySpaceFragment::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMySpaceBinding.inflate(inflater, container, false)
        initViewModel(binding)
        return binding.root
    }

    private fun initViewModel(binding: FragmentMySpaceBinding) {
        mySpaceViewModel = getViewModel(viewModelFactory)
        binding.lifecycleOwner = this
        binding.internetAvailable = mainActivityViewModel.internetAvailable
        binding.viewModel = mySpaceViewModel

        observeViewState()
        observeRequestPermission()
    }

    private fun observeViewState() {
        mySpaceViewModel.viewState.observe(viewLifecycleOwner, Observer { it.fold(
            ifLeft = this@MySpaceFragment::reactToFailure,
            ifRight = this@MySpaceFragment::reactToSuccess
        ) })
    }

    private fun reactToFailure(failure: Failure) {
        when (failure) {
            is CannotExecuteWithoutNetwork -> handleCannotExecuteWithoutNetwork(failure.operatorType)
        }
    }

    private fun reactToSuccess(success: Success) {
        when (success) {
            is Success.ViewEvent -> reactToViewEvent(success)
            is RemoveDocumentSuccessViewState -> getAllDocuments()
        }
    }

    private fun reactToViewEvent(viewEvent: Success.ViewEvent) {
        when (viewEvent) {
            is ContextMenuClick -> showContextMenu(viewEvent.document)
            is DownloadClick -> handleDownloadDocument(viewEvent.document)
            is UploadButtonBottomBarClick -> openFilePicker()
            is RemoveClick -> confirmRemoveDocument(viewEvent.document)
            is SearchButtonClick -> openSearch()
            is ShareItemClick -> navigateToShare(viewEvent.document)
        }
        mySpaceViewModel.dispatchState(Either.right(Idle))
    }

    private fun confirmRemoveDocument(document: Document) {
        dismissContextMenu()
        ConfirmRemoveDocumentDialog(
            document = document,
            title = getString(R.string.confirm_delete_file, document.name),
            negativeText = getString(R.string.cancel),
            positiveText = getString(R.string.delete),
            onPositiveCallback = { handleRemoveDocument(document) }
        ).show(childFragmentManager, "confirm_remove_document_dialog")
    }

    private fun showContextMenu(document: Document) {
        dismissContextMenu()
        MySpaceContextMenuDialog(document)
            .show(childFragmentManager, MySpaceContextMenuDialog.TAG)
    }

    private fun handleRemoveDocument(document: Document) {
        mySpaceViewModel.removeDocument(document)
    }

    private fun observeRequestPermission() {
        mainActivityViewModel.shouldShowPermissionRequestState.observe(viewLifecycleOwner, Observer {
            if (it is ShouldShowWriteStorage) {
                requestPermissions(
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    WriteExternalPermissionRequestCode.code
                )
            }
        })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        LOGGER.info("onViewCreated")
        setUpSwipeRefreshLayout()
        getAllDocuments()
    }

    private fun setUpSwipeRefreshLayout() {
        swipeLayoutMySpace.setColorSchemeResources(R.color.colorPrimary)
    }

    private fun getAllDocuments() {
        LOGGER.info("getAllDocuments")
        mySpaceViewModel.getAllDocuments()
    }

    private fun handleDownloadDocument(document: Document) {
        dismissContextMenu()
        when (mainActivityViewModel.checkWriteStoragePermission(requireContext())) {
            PermissionResult.PermissionGranted -> { download(document) }
            else -> { shouldRequestWriteStoragePermission() }
        }
    }

    private fun download(document: Document) {
        LOGGER.info("download() $document")
        mainActivityViewModel.currentAuthentication.value
            ?.let { authentication ->
                mySpaceViewModel.downloadDocument(authentication.credential, authentication.token, document)
            }
    }

    private fun shouldRequestWriteStoragePermission() {
        mainActivityViewModel.shouldShowWriteStoragePermissionRequest(requireActivity())
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        LOGGER.info("onRequestPermissionsResult() $requestCode")
        when (requestCode) {
            WriteExternalPermissionRequestCode.code -> {
                Either.cond(
                    test = grantResults.all { grantResult -> grantResult == PackageManager.PERMISSION_GRANTED },
                    ifTrue = { mySpaceViewModel.getDownloadingDocument()?.let { download(it) } },
                    ifFalse = { mainActivityViewModel.setActionForWriteStoragePermissionRequest(DENIED) }
                )
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        LOGGER.info("onActivityResult() $requestCode - $data")
        requestCode.takeIf { it == OpenFilePickerRequestCode.code }
            ?.let { data?.data }
            ?.let(this@MySpaceFragment::navigateToUpload)
    }

    private fun dismissContextMenu() {
        childFragmentManager.dismissDialogFragmentByTag(MySpaceContextMenuDialog.TAG)
    }

    private fun handleCannotExecuteWithoutNetwork(operatorType: OperatorType) {
        val messageId = when (operatorType) {
            is OperatorType.SwiftRefresh -> R.string.can_not_refresh_without_network
            else -> R.string.can_not_process_without_network
        }
        Snackbar.make(binding.root, getString(messageId), Snackbar.LENGTH_SHORT)
            .errorLayout(requireContext())
            .setAnchorView(binding.mySpaceUploadButton)
            .show()
        mySpaceViewModel.dispatchResetState()
    }

    private fun navigateToUpload(uri: Uri) {
        val bundle = UploadFragmentArgs(INSIDE_APP, uri).toBundle()
        findNavController().navigate(R.id.uploadFragment, bundle)
    }

    private fun openSearch() {
        findNavController().navigate(R.id.navigationSearch)
    }

    private fun navigateToShare(document: Document) {
        dismissContextMenu()
        val bundle = Bundle()
        bundle.putParcelable(SHARE_DOCUMENT_BUNDLE_KEY, document.toParcelable())
        findNavController().navigate(R.id.navigationShare, bundle)
    }
}
