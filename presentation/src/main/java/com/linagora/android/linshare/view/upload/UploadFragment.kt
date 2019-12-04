package com.linagora.android.linshare.view.upload

import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import com.linagora.android.linshare.util.Constant
import com.linagora.android.linshare.util.CoroutinesDispatcherProvider
import com.linagora.android.linshare.view.MainActivityViewModel
import com.linagora.android.linshare.view.MainActivityViewModel.AuthenticationState.AUTHENTICATED
import com.linagora.android.linshare.view.MainActivityViewModel.AuthenticationState.INVALID_AUTHENTICATION
import com.linagora.android.linshare.view.MainNavigationFragment
import com.linagora.android.linshare.view.upload.worker.UploadWorker
import com.linagora.android.linshare.view.upload.worker.UploadWorker.Companion.FILE_URI_INPUT_KEY
import kotlinx.android.synthetic.main.fragment_upload.btnUpload
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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
                        val mimeType = getString(getColumnIndex(MediaStore.Images.Media.MIME_TYPE))
                        LOGGER.info("name: $fileName - size: $size - mimeType: $mimeType")
                        withContext(dispatcherProvider.main) {
                            bindingData(fileName, size)
                            setUpUploadButton(uri)
                        }
                    }
                }
        }
    }

    private fun bindingData(fileName: String, size: Long) {
        binding.fileName = fileName
        binding.fileSize = size
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
                .build()

            WorkManager.getInstance(requireContext()).enqueue(uploadRequest)
        }
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
