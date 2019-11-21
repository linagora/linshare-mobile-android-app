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
import com.linagora.android.linshare.R
import com.linagora.android.linshare.util.Constant
import com.linagora.android.linshare.view.MainActivityViewModel
import com.linagora.android.linshare.view.MainActivityViewModel.AuthenticationState.AUTHENTICATED
import com.linagora.android.linshare.view.MainActivityViewModel.AuthenticationState.INVALID_AUTHENTICATION
import com.linagora.android.linshare.view.MainNavigationFragment
import org.slf4j.LoggerFactory
import javax.inject.Inject

class UploadFragment : MainNavigationFragment() {

    companion object {
        private val LOGGER = LoggerFactory.getLogger(UploadFragment::class.java)
    }

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val mainActivityViewModel: MainActivityViewModel
            by activityViewModels { viewModelFactory }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        initViewModel()
        return inflater.inflate(R.layout.fragment_upload, container, false)
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
        requireContext().contentResolver.query(uriFile!!, null, null, null, null)
            ?.use { cursor ->
                with(cursor) {
                    moveToFirst()
                    val fileName = getString(getColumnIndex(OpenableColumns.DISPLAY_NAME))
                    val size = getString(getColumnIndex(OpenableColumns.SIZE))
                    val filePath = getString(getColumnIndex(MediaStore.Images.Media.DATA))
                    LOGGER.info("name: $fileName - size: $size - filePath: $filePath")
                }
            }
    }

    private fun navigateToWizardLogin() {
        findNavController().navigate(R.id.wizardFragment)
    }
}
