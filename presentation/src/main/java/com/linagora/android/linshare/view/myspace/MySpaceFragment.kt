package com.linagora.android.linshare.view.myspace

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import arrow.core.Either
import com.linagora.android.linshare.R
import com.linagora.android.linshare.databinding.FragmentMySpaceBinding
import com.linagora.android.linshare.domain.model.document.Document
import com.linagora.android.linshare.domain.model.properties.PreviousUserPermissionAction.DENIED
import com.linagora.android.linshare.domain.usecases.myspace.ContextMenuClick
import com.linagora.android.linshare.model.permission.PermissionResult
import com.linagora.android.linshare.model.properties.RuntimePermissionRequest.ShouldShowWriteStorage
import com.linagora.android.linshare.util.getViewModel
import com.linagora.android.linshare.view.MainActivityViewModel
import com.linagora.android.linshare.view.MainNavigationFragment
import com.linagora.android.linshare.view.WriteExternalPermissionRequestCode
import kotlinx.android.synthetic.main.fragment_my_space.swipeLayoutMySpace
import org.slf4j.LoggerFactory
import javax.inject.Inject

class MySpaceFragment : MainNavigationFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val mainActivityViewModel: MainActivityViewModel
            by activityViewModels { viewModelFactory }

    private lateinit var mySpaceViewModel: MySpaceViewModel

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
            it.map { success ->
                if (success is ContextMenuClick) {
                    handleDownloadDocument(success.document)
                }
            }
        })
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
        when (mainActivityViewModel.checkWriteStoragePermission(requireContext())) {
            PermissionResult.PermissionGranted -> { download(document) }
            else -> { shouldRequestWriteStoragePermission() }
        }
    }

    private fun download(document: Document) {
        LOGGER.info("download $document")
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
}
