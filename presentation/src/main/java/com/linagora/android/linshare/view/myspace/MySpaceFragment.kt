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
import com.linagora.android.linshare.R
import com.linagora.android.linshare.databinding.FragmentMySpaceBinding
import com.linagora.android.linshare.domain.model.document.Document
import com.linagora.android.linshare.domain.model.properties.PreviousUserPermissionAction.DENIED
import com.linagora.android.linshare.domain.usecases.myspace.ContextMenuClick
import com.linagora.android.linshare.domain.usecases.myspace.DownloadClick
import com.linagora.android.linshare.domain.usecases.myspace.UploadButtonBottomBarClick
import com.linagora.android.linshare.domain.usecases.utils.Success
import com.linagora.android.linshare.domain.usecases.utils.Success.Idle
import com.linagora.android.linshare.domain.usecases.myspace.RemoveClick
import com.linagora.android.linshare.domain.usecases.myspace.RemoveDocumentSuccessViewState
import com.linagora.android.linshare.model.permission.PermissionResult
import com.linagora.android.linshare.model.properties.RuntimePermissionRequest.ShouldShowWriteStorage
import com.linagora.android.linshare.util.Constant
import com.linagora.android.linshare.util.MimeType
import com.linagora.android.linshare.util.getViewModel
import com.linagora.android.linshare.view.MainActivityViewModel
import com.linagora.android.linshare.view.MainNavigationFragment
import com.linagora.android.linshare.view.Navigation.UploadType.INSIDE_APP
import com.linagora.android.linshare.view.OpenFilePickerRequestCode
import com.linagora.android.linshare.view.WriteExternalPermissionRequestCode
import com.linagora.android.linshare.view.upload.UploadFragmentArgs
import kotlinx.android.synthetic.main.fragment_my_space.swipeLayoutMySpace
import org.slf4j.LoggerFactory
import java.util.UUID
import javax.inject.Inject

class MySpaceFragment : MainNavigationFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val mainActivityViewModel: MainActivityViewModel
            by activityViewModels { viewModelFactory }

    private lateinit var mySpaceViewModel: MySpaceViewModel

    private lateinit var mySpaceContextMenuDialog: MySpaceContextMenuDialog

    companion object {
        private val LOGGER = LoggerFactory.getLogger(MySpaceFragment::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentMySpaceBinding.inflate(inflater, container, false)
        initViewModel(binding)
        return binding.root
    }

    private fun initViewModel(binding: FragmentMySpaceBinding) {
        mySpaceViewModel = getViewModel(viewModelFactory)
        binding.lifecycleOwner = this
        binding.viewModel = mySpaceViewModel

        observeViewState()
        observeRequestPermission()
    }

    private fun observeViewState() {
        mySpaceViewModel.viewState.observe(this, Observer {
            it.map { success -> when (success) {
                is Success.ViewEvent -> reactToViewEvent(success)
                is RemoveDocumentSuccessViewState -> getAllDocuments()
            } }
        })
    }

    private fun reactToViewEvent(viewEvent: Success.ViewEvent) {
        when (viewEvent) {
            is ContextMenuClick -> showContextMenu(viewEvent.document)
            is DownloadClick -> handleDownloadDocument(viewEvent.document)
            is UploadButtonBottomBarClick -> openFilePicker()
            is RemoveClick -> handleRemoveDocument(viewEvent.uuid)
        }
        mySpaceViewModel.dispatchState(Either.right(Idle))
    }

    private fun showContextMenu(document: Document) {
        mySpaceContextMenuDialog = MySpaceContextMenuDialog(document)
        mySpaceContextMenuDialog.show(childFragmentManager, mySpaceContextMenuDialog.tag)
    }

    private fun handleRemoveDocument(uuid: UUID) {
        mySpaceContextMenuDialog.dismiss()
        mySpaceViewModel.removeDocument(uuid)
    }

    private fun observeRequestPermission() {
        mainActivityViewModel.shouldShowPermissionRequestState.observe(this, Observer {
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
        mySpaceContextMenuDialog.dismiss()
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

    private fun openFilePicker() {
        val getDocumentIntent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        getDocumentIntent.type = MimeType.ALL_TYPE
        startActivityForResult(getDocumentIntent, OpenFilePickerRequestCode.code)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        LOGGER.info("onActivityResult() $requestCode - $data")
        requestCode.takeIf { it == OpenFilePickerRequestCode.code }
            ?.let { data?.data }
            ?.let(this@MySpaceFragment::navigateToUpload)
    }

    private fun navigateToUpload(uri: Uri) {
        val bundle = UploadFragmentArgs(INSIDE_APP).toBundle()
        bundle.putParcelable(Constant.UPLOAD_URI_BUNDLE_KEY, uri)
        findNavController().navigate(R.id.uploadFragment, bundle)
    }
}
