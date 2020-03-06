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
import androidx.navigation.fragment.findNavController
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import arrow.core.Either
import com.linagora.android.linshare.R
import com.linagora.android.linshare.databinding.FragmentUploadBinding
import com.linagora.android.linshare.domain.model.document.DocumentRequest
import com.linagora.android.linshare.domain.usecases.quota.ExtractInfoFailed
import com.linagora.android.linshare.util.Constant
import com.linagora.android.linshare.util.CoroutinesDispatcherProvider
import com.linagora.android.linshare.util.getDocumentRequest
import com.linagora.android.linshare.util.getViewModel
import com.linagora.android.linshare.view.MainActivityViewModel
import com.linagora.android.linshare.view.MainActivityViewModel.AuthenticationState.AUTHENTICATED
import com.linagora.android.linshare.view.MainActivityViewModel.AuthenticationState.INVALID_AUTHENTICATION
import com.linagora.android.linshare.view.MainNavigationFragment
import com.linagora.android.linshare.view.upload.worker.UploadWorker
import com.linagora.android.linshare.view.upload.worker.UploadWorker.Companion.FILE_URI_INPUT_KEY
import com.linagora.android.linshare.view.upload.worker.UploadWorker.Companion.TAG_UPLOAD_WORKER
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

        private val EMPTY_DOCUMENT_REQUEST = null
    }

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    @Inject
    lateinit var dispatcherProvider: CoroutinesDispatcherProvider

    private lateinit var uploadScoped: CoroutineScope

    private val mainActivityViewModel: MainActivityViewModel
            by activityViewModels { viewModelFactory }

    private lateinit var uploadFragmentViewModel: UploadFragmentViewModel

    private lateinit var binding: FragmentUploadBinding

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
        initViewModel()
        return binding.root
    }

    override fun configureToolbar(toolbar: Toolbar) {
        toolbar.navigationIcon = null
    }

    private fun initViewModel() {
        mainActivityViewModel.authenticationState.observe(this, Observer { authenticateState ->
            when (authenticateState) {
                AUTHENTICATED -> receiveFile()
                INVALID_AUTHENTICATION -> navigateToWizardLogin()
            }
        })

        uploadFragmentViewModel = getViewModel(viewModelFactory)
        binding.viewModel = uploadFragmentViewModel
    }

    private fun receiveFile() {
        uploadScoped.launch(dispatcherProvider.io) {
            LOGGER.info("receiveFile()")
            val bundle = requireArguments()
            bundle.getParcelable<Uri>(Constant.UPLOAD_URI_BUNDLE_KEY)
                ?.let { uri ->
                    buildDocumentRequest(uri)
                        ?.let { documentRequest -> bindingData(uri, documentRequest) }
                        ?: handleBuildDocumentRequestFailed()
                }
                ?: handleBuildDocumentRequestFailed()
        }
    }

    private fun handleBuildDocumentRequestFailed() {
        uploadScoped.launch(dispatcherProvider.main) {
            uploadFragmentViewModel.dispatchState(Either.left(ExtractInfoFailed))
        }
    }

    private suspend fun buildDocumentRequest(uri: Uri): DocumentRequest? {
        return try {
            withContext(uploadScoped.coroutineContext + dispatcherProvider.io) {
                val projection = arrayOf(OpenableColumns.DISPLAY_NAME, OpenableColumns.SIZE, Media.MIME_TYPE)
                requireContext().contentResolver
                    .query(uri, projection, ALL_ROWS_SELECTION, EMPTY_SELECTION_ARGS, DEFAULT_SORT_ORDER)
                    ?.use { cursor -> moveCursorAndGetDocumentRequest(cursor, uri) }
            }
        } catch (exp: Exception) {
            LOGGER.error("$exp - ${exp.printStackTrace()}")
            EMPTY_DOCUMENT_REQUEST
        }
    }

    private fun moveCursorAndGetDocumentRequest(cursor: Cursor, uri: Uri): DocumentRequest? {
        return takeIf { cursor.moveToFirst() }
            ?.let { cursor.getDocumentRequest(uri) }
    }

    private fun bindingData(uri: Uri, documentRequest: DocumentRequest) {
        uploadScoped.launch(dispatcherProvider.main) {
            binding.document = documentRequest
            uploadFragmentViewModel.checkAccountQuota(documentRequest)
            setUpUploadButton(uri)
        }
    }

    private fun setUpUploadButton(uri: Uri) {
        btnUpload.setOnClickListener {

            val inputData = createInputDataForUploadFile(uri)

            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

            val uploadRequest = OneTimeWorkRequestBuilder<UploadWorker>()
                .setInputData(inputData)
                .setConstraints(constraints)
                .addTag(TAG_UPLOAD_WORKER)
                .build()

            WorkManager.getInstance(requireContext()).enqueue(uploadRequest)

            alertStartToUpload(1)
            navigateAfterUpload()
        }
    }

    private fun alertStartToUpload(uploadFiles: Int) {
        Toast.makeText(
            requireContext(),
            requireContext()
                .resources
                .getQuantityString(R.plurals.uploading_n_file, uploadFiles),
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun navigateAfterUpload() {
        requireActivity().onBackPressed()
        requireActivity().finish()
    }

    private fun createInputDataForUploadFile(uri: Uri): Data {
        return Data.Builder()
            .putString(FILE_URI_INPUT_KEY, uri.toString())
            .build()
    }

    private fun navigateToWizardLogin() {
        findNavController().navigate(R.id.wizardFragment)
    }
}
