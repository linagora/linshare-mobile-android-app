package com.linagora.android.linshare.view.receivedshares

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
import com.linagora.android.linshare.databinding.FragmentReceivedSharesBinding
import com.linagora.android.linshare.domain.model.properties.PreviousUserPermissionAction.DENIED
import com.linagora.android.linshare.domain.model.share.Share
import com.linagora.android.linshare.domain.usecases.receivedshare.ContextMenuReceivedShareClick
import com.linagora.android.linshare.domain.usecases.receivedshare.DownloadReceivedShareClick
import com.linagora.android.linshare.domain.usecases.utils.Success
import com.linagora.android.linshare.model.permission.PermissionResult
import com.linagora.android.linshare.model.properties.RuntimePermissionRequest
import com.linagora.android.linshare.util.getViewModel
import com.linagora.android.linshare.view.MainActivityViewModel
import com.linagora.android.linshare.view.MainNavigationFragment
import com.linagora.android.linshare.view.WriteExternalPermissionRequestCode
import kotlinx.android.synthetic.main.fragment_received_shares.swipeLayoutReceivedList
import org.slf4j.LoggerFactory
import javax.inject.Inject

class ReceivedSharesFragment : MainNavigationFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val mainActivityViewModel: MainActivityViewModel
            by activityViewModels { viewModelFactory }

    private lateinit var receivedSharesViewModel: ReceivedSharesViewModel

    private lateinit var receivedShareContextMenuDialog: ReceivedShareContextMenuDialog

    companion object {
        private val LOGGER = LoggerFactory.getLogger(ReceivedSharesFragment::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentReceivedSharesBinding.inflate(inflater, container, false)
        initViewModel(binding)
        return binding.root
    }

    private fun initViewModel(binding: FragmentReceivedSharesBinding) {
        receivedSharesViewModel = getViewModel(viewModelFactory)
        binding.lifecycleOwner = this
        binding.viewModel = receivedSharesViewModel

        observeViewState()
        observeRequestPermission()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpSwipeRefreshLayout()
        getReceivedList()
    }

    private fun observeViewState() {
        receivedSharesViewModel.viewState.observe(viewLifecycleOwner, Observer {
            it.map { success -> when (success) {
                is Success.ViewEvent -> reactToViewEvent(success)
            } }
        })
    }

    private fun reactToViewEvent(viewEvent: Success.ViewEvent) {
        when (viewEvent) {
            is ContextMenuReceivedShareClick -> showContextMenuReceivedShare(viewEvent.share)
            is DownloadReceivedShareClick -> handleDownloadDocument(viewEvent.share)
        }
        receivedSharesViewModel.dispatchState(Either.right(Success.Idle))
    }

    private fun handleDownloadDocument(share: Share) {
        receivedShareContextMenuDialog.dismiss()
        when (mainActivityViewModel.checkWriteStoragePermission(requireContext())) {
            PermissionResult.PermissionGranted -> { download(share) }
            else -> { shouldRequestWriteStoragePermission() }
        }
    }

    private fun download(share: Share) {
        LOGGER.info("download() $share")
        mainActivityViewModel.currentAuthentication.value
            ?.let { authentication ->
                receivedSharesViewModel.downloadShare(authentication.credential, authentication.token, share)
            }
    }

    private fun shouldRequestWriteStoragePermission() {
        mainActivityViewModel.shouldShowWriteStoragePermissionRequest(requireActivity())
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

    private fun showContextMenuReceivedShare(share: Share) {
        receivedShareContextMenuDialog = ReceivedShareContextMenuDialog(share)
        receivedShareContextMenuDialog.show(childFragmentManager, receivedShareContextMenuDialog.tag)
    }

    private fun setUpSwipeRefreshLayout() {
        swipeLayoutReceivedList.setColorSchemeResources(R.color.colorPrimary)
    }

    private fun getReceivedList() {
        LOGGER.info("getReceivedList")
        receivedSharesViewModel.getReceivedList()
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
                    ifTrue = { receivedSharesViewModel.getDownloading()?.let { download(it) } },
                    ifFalse = { mainActivityViewModel.setActionForWriteStoragePermissionRequest(DENIED) }
                )
            }
        }
    }
}
