package com.linagora.android.linshare.view.upload

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
import com.linagora.android.linshare.R
import com.linagora.android.linshare.databinding.FragmentUploadBinding
import com.linagora.android.linshare.domain.model.document.DocumentRequest
import com.linagora.android.linshare.util.Constant
import com.linagora.android.linshare.util.CoroutinesDispatcherProvider
import com.linagora.android.linshare.util.MimeType.APPLICATION_DEFAULT
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
import okhttp3.MediaType.Companion.toMediaType
import org.slf4j.LoggerFactory
import javax.inject.Inject

class UploadFragment : MainNavigationFragment() {

    companion object {
        private val LOGGER = LoggerFactory.getLogger(UploadFragment::class.java)
    }

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    @Inject
    lateinit var dispatcherProvider: CoroutinesDispatcherProvider

    private lateinit var uploadScoped: CoroutineScope

    private val mainActivityViewModel: MainActivityViewModel
            by activityViewModels { viewModelFactory }

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
        initViewModel()
        binding = FragmentUploadBinding.inflate(inflater, container, false)
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
    }

    private fun receiveFile() {
        LOGGER.info("receiveFile()")
        val bundle = requireArguments()
        val uriFile = bundle.getParcelable<Uri>(Constant.UPLOAD_URI_BUNDLE_KEY)
        extractFileInfo(uriFile!!)
    }

    private fun extractFileInfo(uri: Uri) {
        uploadScoped.launch(dispatcherProvider.io) {
            requireContext().contentResolver.query(uri, null, null, null, null)
                ?.use { cursor ->
                    with(cursor) {
                        moveToFirst()
                        val fileName = getString(getColumnIndex(OpenableColumns.DISPLAY_NAME))
                        val size = getLong(getColumnIndex(OpenableColumns.SIZE))
                        val mimeType = runCatching {
                            getString(getColumnIndex(Media.MIME_TYPE))
                        }.getOrElse { APPLICATION_DEFAULT }

                        LOGGER.info("name: $fileName - size: $size - mimeType: $mimeType")

                        withContext(dispatcherProvider.main) {
                            bindingData(DocumentRequest(uri, fileName, size, mimeType.toMediaType()))
                            setUpUploadButton(uri)
                        }
                    }
                }
        }
    }

    private fun bindingData(documentRequest: DocumentRequest) {
        binding.document = documentRequest
    }

    private fun setUpUploadButton(uri: Uri) {
        btnUpload.isEnabled = true
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
