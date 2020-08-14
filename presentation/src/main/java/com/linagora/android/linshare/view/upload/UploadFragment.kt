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

package com.linagora.android.linshare.view.upload

import android.Manifest
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore.Images.Media
import android.provider.OpenableColumns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.work.Data
import androidx.work.WorkManager
import arrow.core.Either
import com.linagora.android.linshare.R
import com.linagora.android.linshare.databinding.FragmentUploadBinding
import com.linagora.android.linshare.domain.model.GenericUser
import com.linagora.android.linshare.domain.model.OperatorType
import com.linagora.android.linshare.domain.model.autocomplete.AutoCompletePattern
import com.linagora.android.linshare.domain.model.autocomplete.AutoCompleteResult
import com.linagora.android.linshare.domain.model.autocomplete.MailingList
import com.linagora.android.linshare.domain.model.autocomplete.MailingListAutoCompleteResult
import com.linagora.android.linshare.domain.model.autocomplete.SimpleAutoCompleteResult
import com.linagora.android.linshare.domain.model.autocomplete.UserAutoCompleteResult
import com.linagora.android.linshare.domain.model.autocomplete.toGenericUser
import com.linagora.android.linshare.domain.model.autocomplete.toMailingList
import com.linagora.android.linshare.domain.model.document.DocumentRequest
import com.linagora.android.linshare.domain.model.properties.PreviousUserPermissionAction.DENIED
import com.linagora.android.linshare.domain.usecases.quota.ExtractInfoFailed
import com.linagora.android.linshare.domain.usecases.quota.PreUploadExecuting
import com.linagora.android.linshare.domain.usecases.share.AddMailingList
import com.linagora.android.linshare.domain.usecases.share.AddRecipient
import com.linagora.android.linshare.domain.usecases.share.SelectDestinationClick
import com.linagora.android.linshare.domain.usecases.upload.EmptyDocumentException
import com.linagora.android.linshare.domain.usecases.upload.NotEnoughDeviceStorageException
import com.linagora.android.linshare.domain.usecases.upload.PreUploadError
import com.linagora.android.linshare.domain.usecases.utils.Failure
import com.linagora.android.linshare.domain.usecases.utils.Success
import com.linagora.android.linshare.domain.utils.NoOp
import com.linagora.android.linshare.model.parcelable.toQuotaId
import com.linagora.android.linshare.model.parcelable.toSharedSpaceId
import com.linagora.android.linshare.model.parcelable.toWorkGroupNodeId
import com.linagora.android.linshare.model.permission.PermissionResult
import com.linagora.android.linshare.model.properties.RuntimePermissionRequest.ShouldShowReadContact
import com.linagora.android.linshare.model.upload.UploadDocumentRequest
import com.linagora.android.linshare.model.upload.toDocumentRequest
import com.linagora.android.linshare.util.CoroutinesDispatcherProvider
import com.linagora.android.linshare.util.NetworkConnectivity
import com.linagora.android.linshare.util.binding.addMailingListView
import com.linagora.android.linshare.util.binding.addRecipientView
import com.linagora.android.linshare.util.binding.initView
import com.linagora.android.linshare.util.binding.onSelectedRecipient
import com.linagora.android.linshare.util.binding.queryAfterTextChange
import com.linagora.android.linshare.util.dismissDialogFragmentByTag
import com.linagora.android.linshare.util.dismissKeyboard
import com.linagora.android.linshare.util.getUploadDocumentRequest
import com.linagora.android.linshare.util.getViewModel
import com.linagora.android.linshare.view.Event
import com.linagora.android.linshare.view.MainActivityViewModel
import com.linagora.android.linshare.view.MainActivityViewModel.AuthenticationState.AUTHENTICATED
import com.linagora.android.linshare.view.MainActivityViewModel.AuthenticationState.INVALID_AUTHENTICATION
import com.linagora.android.linshare.view.MainNavigationFragment
import com.linagora.android.linshare.view.Navigation
import com.linagora.android.linshare.view.ReadContactPermissionRequestCode
import com.linagora.android.linshare.view.base.event.SelectedDestinationMySpace
import com.linagora.android.linshare.view.base.event.SelectedDestinationSharedSpace
import com.linagora.android.linshare.view.dialog.UploadProgressDialog
import com.linagora.android.linshare.view.upload.request.UploadAndShareRequest
import com.linagora.android.linshare.view.upload.request.UploadToMySpaceRequest
import com.linagora.android.linshare.view.upload.request.UploadToSharedSpaceRequest
import com.linagora.android.linshare.view.upload.request.UploadWorkerRequest
import com.linagora.android.linshare.view.upload.worker.UploadWorker.Companion.FILE_MIME_TYPE_INPUT_KEY
import com.linagora.android.linshare.view.upload.worker.UploadWorker.Companion.FILE_NAME_INPUT_KEY
import com.linagora.android.linshare.view.upload.worker.UploadWorker.Companion.FILE_PATH_INPUT_KEY
import com.linagora.android.linshare.view.widget.makeCustomToast
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.slf4j.LoggerFactory
import javax.inject.Inject

class UploadFragment : MainNavigationFragment() {

    companion object {
        private val LOGGER = LoggerFactory.getLogger(UploadFragment::class.java)

        private val ALL_ROWS_SELECTION = null

        private val EMPTY_SELECTION_ARGS = null

        private val DEFAULT_SORT_ORDER = null

        val UPLOAD_TO_MY_SPACE_DESTINATION_INFO = null
    }

    @Inject
    lateinit var workManager: WorkManager

    @Inject
    lateinit var dispatcherProvider: CoroutinesDispatcherProvider

    private val mainActivityViewModel: MainActivityViewModel
            by activityViewModels { viewModelFactory }

    private lateinit var uploadFragmentViewModel: UploadFragmentViewModel

    private lateinit var binding: FragmentUploadBinding

    private val args: UploadFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        LOGGER.info("onCreateView()")
        binding = FragmentUploadBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.uploadType = args.uploadType
        binding.selectedDestinationInfo = args.selectedDestinationInfo
        initViewModel()
        initAutoComplete()
        checkPickerEventToShowPickDestinationDialog()
        return binding.root
    }

    private fun checkPickerEventToShowPickDestinationDialog() {
        args.destinationPickerEvent.takeIf { args.destinationPickerEvent == Event.DestinationPickerEvent.BACK }
            ?.let { showPickDestinationDialog() }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.window?.setSoftInputMode(SOFT_INPUT_ADJUST_NOTHING)
    }

    override fun configureToolbar(toolbar: Toolbar) {
        toolbar.navigationIcon = null
    }

    private fun initViewModel() {
        mainActivityViewModel.authenticationState.observe(viewLifecycleOwner, Observer { authenticateState ->
            when (authenticateState) {
                AUTHENTICATED -> receiveFile()
                INVALID_AUTHENTICATION -> navigateToWizardLogin()
            }
        })

        uploadFragmentViewModel = getViewModel(viewModelFactory)
        binding.viewModel = uploadFragmentViewModel

        observeViewState()
        observeRequestPermission()
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

    private fun initAutoComplete() {
        if (isUploadToMySpace()) {
            with(binding.addRecipientContainer) {
                initView()
                queryAfterTextChange(this@UploadFragment::queryRecipientAutoComplete)
                onSelectedRecipient(this@UploadFragment::reactOnSelectedSuggestion)
            }
        }
    }

    private fun isUploadToMySpace(): Boolean {
        return binding.uploadType != Navigation.UploadType.INSIDE_APP_TO_WORKGROUP || binding.uploadType != Navigation.UploadType.OUTSIDE_APP_TO_WORKGROUP
    }

    private fun reactOnSelectedSuggestion(autoCompleteResult: AutoCompleteResult) {
        when (autoCompleteResult) {
            is UserAutoCompleteResult -> uploadFragmentViewModel.addRecipient(autoCompleteResult.toGenericUser())
            is SimpleAutoCompleteResult -> uploadFragmentViewModel.addRecipient(autoCompleteResult.toGenericUser())
            is MailingListAutoCompleteResult -> uploadFragmentViewModel.addMailingList(autoCompleteResult.toMailingList())
        }
    }

    private fun observeViewState() {
        uploadFragmentViewModel.viewState.observe(viewLifecycleOwner, Observer { state ->
            state.fold(
                ifLeft = { dismissUploadProgressDialog() },
                ifRight = { success ->
                    when (success) {
                        is Success.ViewEvent -> reactToViewEvent(success)
                        is Success.ViewState -> reactToViewState(success)
                    }
                })
        })
    }

    private fun receiveFile() {
        uploadFragmentViewModel.dispatchState(Either.right(PreUploadExecuting))

        viewLifecycleOwner.lifecycleScope.launchWhenResumed {
            runCatching { extractArgument() }
                .getOrElse(this@UploadFragment::handlePreUploadError)
        }
    }

    private suspend fun extractArgument() {
        LOGGER.info("extractArgument()")
        buildUploadDocumentRequest(args.uri)
            .let { uploadFragmentViewModel.dispatchState(Either.right(ExtractInfoSuccess(it))) }
    }

    private fun handlePreUploadError(throwable: Throwable) {
        LOGGER.error("handlePreUploadError(): $throwable - ${throwable.printStackTrace()}")
        takeIf { throwable is EmptyDocumentException }
            ?.let { handleBuildDocumentRequestFailed(ExtractInfoFailed) }
            ?: handleBuildDocumentRequestFailed(PreUploadError)
    }

    private fun handleBuildDocumentRequestFailed(failure: Failure.FeatureFailure) {
        viewLifecycleOwner.lifecycleScope.launchWhenResumed {
            uploadFragmentViewModel.dispatchState(Either.left(failure))
        }
    }

    private suspend fun buildUploadDocumentRequest(uri: Uri): UploadDocumentRequest {
        return withContext(viewLifecycleOwner.lifecycleScope.coroutineContext + dispatcherProvider.io) {
            val projection = arrayOf(OpenableColumns.DISPLAY_NAME, OpenableColumns.SIZE, Media.MIME_TYPE)
            requireContext().contentResolver
                .query(uri, projection, ALL_ROWS_SELECTION, EMPTY_SELECTION_ARGS, DEFAULT_SORT_ORDER)
                ?.use { cursor -> extractCursor(uri, cursor) }
                ?: throw EmptyDocumentException
        }
    }

    private fun extractCursor(uri: Uri, cursor: Cursor): UploadDocumentRequest {
        return takeIf { cursor.moveToFirst() }
            ?.let { cursor.getUploadDocumentRequest(uri) }
            ?: throw EmptyDocumentException
    }

    private fun handleExtractSuccess(uploadDocumentRequest: UploadDocumentRequest) {
        bindingData(uploadDocumentRequest)
        viewLifecycleOwner.lifecycleScope.launchWhenResumed {
            uploadFragmentViewModel.dispatchState(
                Either.Right(CheckLocalDeviceStorage(uploadDocumentRequest)))
        }
    }

    private fun handleCheckDeviceStorageSuccess(uploadDocumentRequest: UploadDocumentRequest) {
        checkAccountQuota(uploadDocumentRequest)
        showReadContactPermissionRequest()
    }

    private fun showReadContactPermissionRequest() {
        if (needToShowReadContactPermissionRequest()) {
            mainActivityViewModel.shouldShowReadContactPermissionRequest(requireActivity())
        }
    }

    private fun needToShowReadContactPermissionRequest(): Boolean {
        return isUploadToMySpace() &&
            mainActivityViewModel.checkReadContactPermission(requireContext()) == PermissionResult.PermissionDenied
    }

    private fun bindingData(uploadDocumentRequest: UploadDocumentRequest) {
        viewLifecycleOwner.lifecycleScope.launchWhenResumed {
            binding.uploadDocument = uploadDocumentRequest
        }
    }

    private fun checkAccountQuota(uploadDocumentRequest: UploadDocumentRequest) {
        if (mainActivityViewModel.internetAvailable.value == NetworkConnectivity.CONNECTED)
            uploadFragmentViewModel.checkAccountQuota(uploadDocumentRequest)
    }

    private fun checkLocalDeviceStorage(uploadDocumentRequest: UploadDocumentRequest) {
        uploadFragmentViewModel.checkLocalDeviceStorage(uploadDocumentRequest)
    }

    private fun queryRecipientAutoComplete(autoCompletePattern: AutoCompletePattern) {
        viewLifecycleOwner.lifecycleScope.launch {
            uploadFragmentViewModel.shareRecipientsManager.query(autoCompletePattern)
        }
    }

    private fun reactToViewState(viewState: Success.ViewState) {
        LOGGER.info("reactToViewState(): $viewState")
        when (viewState) {
            is ExtractInfoSuccess -> handleExtractSuccess(viewState.uploadDocumentRequest)
            is CheckLocalDeviceStorage -> checkLocalDeviceStorage(viewState.documentRequest)
            is EnoughDeviceStorage -> handleCheckDeviceStorageSuccess(viewState.documentRequest)
            is BuildDocumentRequestSuccess -> executeUpload(viewState.documentRequest)
        }
    }

    private fun reactToViewEvent(viewEvent: Success.ViewEvent) {
        when (viewEvent) {
            is AddRecipient -> addRecipientView(viewEvent.user)
            is AddMailingList -> addMailingListView(viewEvent.mailingList)
            is OnUploadButtonClick -> preUpload(viewEvent.uploadDocumentRequest)
            is SelectDestinationClick -> selectDestination()
            is SelectedDestinationMySpace -> updateOutsideToMySpaceDestination()
            is SelectedDestinationSharedSpace -> navigateToDestinationPicker()
        }
        uploadFragmentViewModel.dispatchState(Either.right(Success.Idle))
    }

    private fun selectDestination() {
        binding.selectedDestinationInfo
            ?.let { reSelectDestination() }
            ?: showPickDestinationDialog()
    }

    private fun reSelectDestination() {
        val action = UploadFragmentDirections.actionUploadFragmentToNavigationPickDestination(args.uploadType, args.uri, args.selectedDestinationInfo)
        findNavController().navigate(action)
    }

    private fun updateOutsideToMySpaceDestination() {
        childFragmentManager.dismissDialogFragmentByTag(PickDestinationDialog.TAG)
        binding.uploadType = Navigation.UploadType.OUTSIDE_APP
        binding.selectedDestinationInfo = UPLOAD_TO_MY_SPACE_DESTINATION_INFO
    }

    private fun navigateToDestinationPicker() {
        val action = UploadFragmentDirections.actionUploadFragmentToNavigationDestination(
            args.uploadType,
            args.uri,
            args.selectedDestinationInfo)
        findNavController().navigate(action)
    }

    private fun showPickDestinationDialog() {
        PickDestinationDialog(OperatorType.UploadFile, uploadFragmentViewModel.selectDestinationSpaceTypeAction)
            .show(childFragmentManager, PickDestinationDialog.TAG)
    }

    private fun addRecipientView(user: GenericUser) {
        binding.addRecipientContainer
            .addRecipientView(requireContext(), user, uploadFragmentViewModel::removeRecipient)
    }

    private fun addMailingListView(mailingList: MailingList) {
        binding.addRecipientContainer
            .addMailingListView(requireContext(), mailingList, uploadFragmentViewModel::removeMailingList)
    }

    private fun preUpload(uploadDocumentRequest: UploadDocumentRequest) {
        LOGGER.info("executeUpload(): ${uploadDocumentRequest.uploadFileName} - ${uploadDocumentRequest.uploadFileSize}")
        dismissUploadProgressDialog()
        UploadProgressDialog()
            .show(childFragmentManager, UploadProgressDialog.TAG)
        viewLifecycleOwner.lifecycleScope.launch(dispatcherProvider.io) {
            val documentRequestState = Either
                .catch { uploadDocumentRequest.toDocumentRequest(requireContext()) }
                .bimap(
                    leftOperation = { throwable -> generatePreUploadFailureState(throwable) },
                    rightOperation = { BuildDocumentRequestSuccess(it) })

            uploadFragmentViewModel.dispatchUIState(documentRequestState)
        }
    }

    private fun generatePreUploadFailureState(throwable: Throwable): Failure {
        LOGGER.error("handleConvertToDocumentRequest(): $throwable - ${throwable.printStackTrace()}")
        return takeIf { throwable is NotEnoughDeviceStorageException }
            ?.let { NotEnoughDeviceStorageViewState }
            ?: CanNotCreateFileViewState
    }

    private fun executeUpload(documentRequest: DocumentRequest) {
        dismissUploadProgressDialog()

        val inputData = createInputDataForUploadFile(documentRequest)
        createUploadRequest().execute(inputData)

        alertStartToUpload(1)
        navigateAfterUpload()
    }

    private fun dismissUploadProgressDialog() {
        childFragmentManager.dismissDialogFragmentByTag(UploadProgressDialog.TAG)
    }

    private fun createUploadRequest(): UploadWorkerRequest {
        return when (binding.uploadType) {
            Navigation.UploadType.INSIDE_APP_TO_WORKGROUP, Navigation.UploadType.OUTSIDE_APP_TO_WORKGROUP -> createSharedSpaceUploadRequest(args)
            else -> createMySpaceUploadRequest()
        }
    }

    private fun createMySpaceUploadRequest(): UploadWorkerRequest {
        return uploadFragmentViewModel.shareRecipientsManager.shareReceiverCount.value
            ?.takeIf { it > 0 }
            ?.let { createUploadAndShareRequest() }
            ?: UploadToMySpaceRequest(workManager)
    }

    private fun createUploadAndShareRequest(): UploadAndShareRequest {
        return UploadAndShareRequest(
            workManager = workManager,
            recipients = uploadFragmentViewModel.shareRecipientsManager.recipients.value ?: emptySet(),
            mailingLists = uploadFragmentViewModel.shareRecipientsManager.mailingLists.value ?: emptySet()
        )
    }

    private fun createSharedSpaceUploadRequest(
        args: UploadFragmentArgs
    ): UploadWorkerRequest {
        LOGGER.info("createSharedSpaceUploadRequest()")

        val sharedSpaceId = args.selectedDestinationInfo
            ?.sharedSpaceDestinationInfo
            ?.sharedSpaceIdParcelable
            ?.toSharedSpaceId()

        val sharedSpaceQuotaId = args.selectedDestinationInfo
            ?.sharedSpaceDestinationInfo
            ?.sharedSpaceQuotaId
            ?.toQuotaId()

        val parentNodeId = args.selectedDestinationInfo
            ?.parentDestinationInfo
            ?.parentNodeId
            ?.toWorkGroupNodeId()

        require(sharedSpaceId != null)
        require(sharedSpaceQuotaId != null)
        require(parentNodeId != null)

        LOGGER.info("createSharedSpaceUploadRequest(): $sharedSpaceId - $sharedSpaceQuotaId - $parentNodeId")

        return UploadToSharedSpaceRequest(workManager, sharedSpaceId, sharedSpaceQuotaId, parentNodeId)
    }

    private fun alertStartToUpload(uploadFiles: Int) {
        Toast(context).makeCustomToast(
                requireContext(),
                requireContext().resources
                    .getQuantityString(R.plurals.file_in_waiting_list,
                uploadFiles), Toast.LENGTH_SHORT)
            .show()
    }

    private fun navigateAfterUpload() {
        when (binding.uploadType) {
            Navigation.UploadType.OUTSIDE_APP, Navigation.UploadType.OUTSIDE_APP_TO_WORKGROUP -> requireActivity().onBackPressed()
            Navigation.UploadType.INSIDE_APP, Navigation.UploadType.INSIDE_APP_TO_WORKGROUP -> findNavController().popBackStack()
        }
    }

    private fun createInputDataForUploadFile(documentRequest: DocumentRequest): Data {
        return Data.Builder()
            .putString(FILE_PATH_INPUT_KEY, documentRequest.file.absolutePath)
            .putString(FILE_NAME_INPUT_KEY, documentRequest.uploadFileName)
            .putString(FILE_MIME_TYPE_INPUT_KEY, documentRequest.mediaType.toString())
            .build()
    }

    private fun navigateToWizardLogin() {
        findNavController().navigate(R.id.wizardFragment)
    }

    private fun clearAutoCompleteFocus() {
        with(binding.addRecipientContainer) {
            addRecipients.dismissKeyboard()
            addRecipients.clearFocus()
        }
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
        uploadFragmentViewModel.resetRecipientManager()
    }
}
