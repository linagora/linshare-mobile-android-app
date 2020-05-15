package com.linagora.android.linshare.view.upload

import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore.Images.Media
import android.provider.OpenableColumns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.work.Data
import androidx.work.WorkManager
import arrow.core.Either
import com.linagora.android.linshare.R
import com.linagora.android.linshare.databinding.FragmentUploadBinding
import com.linagora.android.linshare.domain.model.autocomplete.AutoCompletePattern
import com.linagora.android.linshare.domain.model.autocomplete.toGenericUser
import com.linagora.android.linshare.domain.model.document.DocumentRequest
import com.linagora.android.linshare.domain.usecases.quota.ExtractInfoFailed
import com.linagora.android.linshare.domain.usecases.quota.PreUploadExecuting
import com.linagora.android.linshare.domain.usecases.share.AddRecipient
import com.linagora.android.linshare.domain.usecases.upload.EmptyDocumentException
import com.linagora.android.linshare.domain.usecases.upload.PreUploadError
import com.linagora.android.linshare.domain.usecases.utils.Failure
import com.linagora.android.linshare.domain.usecases.utils.Success
import com.linagora.android.linshare.model.parcelable.toQuotaId
import com.linagora.android.linshare.model.parcelable.toSharedSpaceId
import com.linagora.android.linshare.model.parcelable.toWorkGroupNodeId
import com.linagora.android.linshare.util.Constant
import com.linagora.android.linshare.util.CoroutinesDispatcherProvider
import com.linagora.android.linshare.util.NetworkConnectivity
import com.linagora.android.linshare.util.binding.addRecipientView
import com.linagora.android.linshare.util.binding.initView
import com.linagora.android.linshare.util.binding.onSelectedRecipient
import com.linagora.android.linshare.util.binding.queryAfterTextChange
import com.linagora.android.linshare.util.createTempFile
import com.linagora.android.linshare.util.dismissKeyboard
import com.linagora.android.linshare.util.getDocumentRequest
import com.linagora.android.linshare.util.getViewModel
import com.linagora.android.linshare.view.MainActivityViewModel
import com.linagora.android.linshare.view.MainActivityViewModel.AuthenticationState.AUTHENTICATED
import com.linagora.android.linshare.view.MainActivityViewModel.AuthenticationState.INVALID_AUTHENTICATION
import com.linagora.android.linshare.view.MainNavigationFragment
import com.linagora.android.linshare.view.Navigation
import com.linagora.android.linshare.view.upload.request.UploadAndShareRequest
import com.linagora.android.linshare.view.upload.request.UploadToMySpaceRequest
import com.linagora.android.linshare.view.upload.request.UploadToSharedSpaceRequest
import com.linagora.android.linshare.view.upload.request.UploadWorkerRequest
import com.linagora.android.linshare.view.upload.worker.UploadWorker.Companion.FILE_MIME_TYPE_INPUT_KEY
import com.linagora.android.linshare.view.upload.worker.UploadWorker.Companion.FILE_NAME_INPUT_KEY
import com.linagora.android.linshare.view.upload.worker.UploadWorker.Companion.FILE_PATH_INPUT_KEY
import com.linagora.android.linshare.view.widget.makeCustomToast
import kotlinx.android.synthetic.main.fragment_upload.btnUpload
import kotlinx.coroutines.CoroutineScope
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
    }

    @Inject
    lateinit var workManager: WorkManager

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    @Inject
    lateinit var dispatcherProvider: CoroutinesDispatcherProvider

    private lateinit var uploadScoped: CoroutineScope

    private val mainActivityViewModel: MainActivityViewModel
            by activityViewModels { viewModelFactory }

    private lateinit var uploadFragmentViewModel: UploadFragmentViewModel

    private lateinit var binding: FragmentUploadBinding

    private val args: UploadFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        uploadScoped = CoroutineScope(dispatcherProvider.main)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentUploadBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.uploadType = args.uploadType
        initViewModel()
        initAutoComplete()
        return binding.root
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
    }

    private fun initAutoComplete() {
        if (args.uploadType != Navigation.UploadType.INSIDE_APP_TO_WORKGROUP) {
            with(binding.addRecipientContainer) {
                initView()
                queryAfterTextChange { pattern -> queryRecipientAutoComplete(pattern) }
                onSelectedRecipient { userAutoCompleteResult ->
                    uploadFragmentViewModel.addRecipient(userAutoCompleteResult.toGenericUser())
                }
            }
        }
    }

    private fun observeViewState() {
        uploadFragmentViewModel.viewState.observe(viewLifecycleOwner, Observer { state ->
            state.map { success -> when (success) {
                is Success.ViewEvent -> reactToViewEvent(success)
            } }
        })
    }

    private fun receiveFile() {
        uploadFragmentViewModel.dispatchState(Either.right(PreUploadExecuting))

        uploadScoped.launch(dispatcherProvider.io) {
            runCatching { extractArgument() }
                .getOrElse(this@UploadFragment::handlePreUploadError)
        }
    }

    private suspend fun extractArgument() {
        LOGGER.info("extractArgument()")
        val bundle = requireArguments()
        bundle.getParcelable<Uri>(Constant.UPLOAD_URI_BUNDLE_KEY)
            ?.let { uri -> buildDocumentRequest(uri) }
            ?.let(this@UploadFragment::bindingData)
    }

    private fun handlePreUploadError(throwable: Throwable) {
        LOGGER.error("handlePreUploadError(): $throwable - ${throwable.printStackTrace()}")
        takeIf { throwable is EmptyDocumentException }
            ?.let { handleBuildDocumentRequestFailed(ExtractInfoFailed) }
            ?: handleBuildDocumentRequestFailed(PreUploadError)
    }

    private fun handleBuildDocumentRequestFailed(failure: Failure.FeatureFailure) {
        uploadScoped.launch(dispatcherProvider.main) {
            uploadFragmentViewModel.dispatchState(Either.left(failure))
        }
    }

    private suspend fun buildDocumentRequest(uri: Uri): DocumentRequest {
        return withContext(uploadScoped.coroutineContext + dispatcherProvider.io) {
            val projection = arrayOf(OpenableColumns.DISPLAY_NAME, Media.MIME_TYPE)
            requireContext().contentResolver
                .query(uri, projection, ALL_ROWS_SELECTION, EMPTY_SELECTION_ARGS, DEFAULT_SORT_ORDER)
                ?.use { cursor -> extractCursor(uri, cursor) }
                ?: throw EmptyDocumentException
        }
    }

    private fun extractCursor(uri: Uri, cursor: Cursor): DocumentRequest {
        return takeIf { cursor.moveToFirst() }
            ?.let { cursor.getDocumentRequest(uri.createTempFile(requireContext())) }
            ?: throw EmptyDocumentException
    }

    private fun bindingData(documentRequest: DocumentRequest) {
        uploadScoped.launch(dispatcherProvider.main) {
            binding.document = documentRequest
            if (mainActivityViewModel.internetAvailable.value == NetworkConnectivity.CONNECTED)
                uploadFragmentViewModel.checkAccountQuota(documentRequest)
            setUpUploadButton(documentRequest)
        }
    }

    private fun queryRecipientAutoComplete(autoCompletePattern: AutoCompletePattern) {
        viewLifecycleOwner.lifecycleScope.launch {
            uploadFragmentViewModel.shareRecipientsManager.query(autoCompletePattern)
        }
    }

    private fun reactToViewEvent(viewEvent: Success.ViewEvent) {
        when (viewEvent) {
            is AddRecipient -> binding.addRecipientContainer
                .addRecipientView(
                    context = requireContext(),
                    user = viewEvent.user,
                    onRemoveRecipient = uploadFragmentViewModel::removeRecipient)
        }
        uploadFragmentViewModel.dispatchState(Either.right(Success.Idle))
    }

    private fun setUpUploadButton(documentRequest: DocumentRequest) {
        btnUpload.setOnClickListener {

            val inputData = createInputDataForUploadFile(documentRequest)
            createUploadRequest().execute(inputData)

            alertStartToUpload(1)
            navigateAfterUpload()
        }
    }

    private fun createUploadRequest(): UploadWorkerRequest {
        return when (args.uploadType) {
            Navigation.UploadType.INSIDE_APP_TO_WORKGROUP -> createSharedSpaceUploadRequest(args)
            else -> createMySpaceUploadRequest()
        }
    }

    private fun createMySpaceUploadRequest(): UploadWorkerRequest {
        return uploadFragmentViewModel.shareRecipientsManager.recipients.value
            ?.takeIf { it.isNotEmpty() }
            ?.let { recipients -> UploadAndShareRequest(workManager, recipients) }
            ?: UploadToMySpaceRequest(workManager)
    }

    private fun createSharedSpaceUploadRequest(
        args: UploadFragmentArgs
    ): UploadWorkerRequest {
        LOGGER.info("createSharedSpaceUploadRequest()")

        val sharedSpaceId = args.uploadDestinationInfo
            ?.sharedSpaceDestinationInfo
            ?.sharedSpaceIdParcelable
            ?.toSharedSpaceId()

        val sharedSpaceQuotaId = args.uploadDestinationInfo
            ?.sharedSpaceDestinationInfo
            ?.sharedSpaceQuotaId
            ?.toQuotaId()

        val parentNodeId = args.uploadDestinationInfo
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
                uploadFiles), Toast.LENGTH_LONG)
            .show()
    }

    private fun navigateAfterUpload() {
        when (args.uploadType) {
            Navigation.UploadType.OUTSIDE_APP -> {
                requireActivity().onBackPressed()
                requireActivity().finish()
            }
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

    override fun onPause() {
        clearAutoCompleteFocus()
        super.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        uploadFragmentViewModel.resetRecipients()
    }
}
