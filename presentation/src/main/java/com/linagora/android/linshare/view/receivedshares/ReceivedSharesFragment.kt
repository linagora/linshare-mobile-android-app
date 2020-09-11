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

package com.linagora.android.linshare.view.receivedshares

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import arrow.core.Either
import com.google.android.material.snackbar.Snackbar
import com.linagora.android.linshare.R
import com.linagora.android.linshare.databinding.FragmentReceivedSharesBinding
import com.linagora.android.linshare.domain.model.document.Document
import com.linagora.android.linshare.domain.model.order.OrderListConfigurationType
import com.linagora.android.linshare.domain.model.properties.PreviousUserPermissionAction.DENIED
import com.linagora.android.linshare.domain.model.share.Share
import com.linagora.android.linshare.domain.usecases.myspace.CopyFailedWithFileSizeExceed
import com.linagora.android.linshare.domain.usecases.myspace.CopyFailedWithQuotaReach
import com.linagora.android.linshare.domain.usecases.myspace.CopyInMySpaceSuccess
import com.linagora.android.linshare.domain.usecases.order.GetOrderListConfigurationSuccess
import com.linagora.android.linshare.domain.usecases.receivedshare.ContextMenuReceivedShareClick
import com.linagora.android.linshare.domain.usecases.receivedshare.DownloadReceivedShareClick
import com.linagora.android.linshare.domain.usecases.receivedshare.ReceivedSharesCopyInMySpace
import com.linagora.android.linshare.domain.usecases.utils.Failure
import com.linagora.android.linshare.domain.usecases.utils.Success
import com.linagora.android.linshare.model.permission.PermissionResult
import com.linagora.android.linshare.model.properties.RuntimePermissionRequest
import com.linagora.android.linshare.model.resources.StringId
import com.linagora.android.linshare.util.getViewModel
import com.linagora.android.linshare.view.MainActivityViewModel
import com.linagora.android.linshare.view.MainNavigationFragment
import com.linagora.android.linshare.view.WriteExternalPermissionRequestCode
import com.linagora.android.linshare.view.widget.errorLayout
import com.linagora.android.linshare.view.widget.withLinShare
import kotlinx.android.synthetic.main.fragment_received_shares.swipeLayoutReceivedList
import org.slf4j.LoggerFactory

class ReceivedSharesFragment : MainNavigationFragment() {

    private val mainActivityViewModel: MainActivityViewModel
            by activityViewModels { viewModelFactory }

    private lateinit var receivedSharesViewModel: ReceivedSharesViewModel

    private lateinit var receivedShareContextMenuDialog: ReceivedShareContextMenuDialog

    private lateinit var binding: FragmentReceivedSharesBinding

    companion object {
        private val LOGGER = LoggerFactory.getLogger(ReceivedSharesFragment::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentReceivedSharesBinding.inflate(inflater, container, false)
        initViewModel(binding)
        return binding.root
    }

    private fun initViewModel(binding: FragmentReceivedSharesBinding) {
        receivedSharesViewModel = getViewModel(viewModelFactory)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = receivedSharesViewModel

        observeViewState()
        observeRequestPermission()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpSwipeRefreshLayout()
        getOrderListConfiguration()
    }

    private fun observeViewState() {
        receivedSharesViewModel.viewState.observe(viewLifecycleOwner, Observer { state ->
            state.fold(
                this@ReceivedSharesFragment::reactToFailureState,
                this@ReceivedSharesFragment::reactToSuccessState)
        })
    }

    private fun reactToFailureState(failure: Failure) {
        if (failure is CopyFailedWithFileSizeExceed) {
            showCopyToMySpaceError(StringId(R.string.copy_to_my_space_error_file_size_exceed))
        }
        if (failure is CopyFailedWithQuotaReach) {
            showCopyToMySpaceError(StringId(R.string.copy_to_my_space_error_quota_reach))
        }
    }

    private fun reactToSuccessState(success: Success) {
        when (success) {
            is Success.ViewState -> reactToViewState(success)
            is Success.ViewEvent -> reactToViewEvent(success)
        }
    }

    private fun reactToViewState(success: Success.ViewState) {
        when (success) {
            is CopyInMySpaceSuccess -> showCopyInMySpaceSuccess(success.documents)
            is GetOrderListConfigurationSuccess -> handleGetOrderListConfigSuccess(success.orderListConfigurationType)
        }
    }

    private fun reactToViewEvent(viewEvent: Success.ViewEvent) {
        LOGGER.info("reactToViewEvent(): $viewEvent")
        when (viewEvent) {
            is ContextMenuReceivedShareClick -> showContextMenuReceivedShare(viewEvent.share)
            is DownloadReceivedShareClick -> handleDownloadDocument(viewEvent.share)
            is ReceivedSharesCopyInMySpace -> handleCopyInMySpace(viewEvent.share)
        }
        resetState()
    }

    private fun getOrderListConfiguration() {
        receivedSharesViewModel.getOrderListConfiguration()
    }

    private fun handleGetOrderListConfigSuccess(orderListConfigurationType: OrderListConfigurationType) {
        receivedSharesViewModel.setCurrentOrderListConfigurationType(orderListConfigurationType)
        getReceivedList()
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

    private fun handleCopyInMySpace(share: Share) {
        LOGGER.info("handleCopyInMySpace(): $share")
        receivedShareContextMenuDialog.dismiss()
        receivedSharesViewModel.copyInMySpace(share)
    }

    private fun showCopyToMySpaceError(stringId: StringId) {
        LOGGER.info("showCopyToMySpaceError()")
        Snackbar.make(binding.root, getString(stringId.value), Snackbar.LENGTH_SHORT)
            .errorLayout(requireContext())
            .show()
        resetState()
    }

    private fun showCopyInMySpaceSuccess(documents: List<Document>) {
        LOGGER.info("showCopyInMySpaceSuccess(): $documents")
        Snackbar.make(binding.root, getString(R.string.copied_in_my_space, documents[0].name), Snackbar.LENGTH_LONG)
            .withLinShare(requireContext())
            .setAction(R.string.view) { goToMySpace() }
            .show()
        resetState()
    }

    private fun goToMySpace() = findNavController().navigate(R.id.navigation_my_space)

    private fun resetState() = receivedSharesViewModel.dispatchState(Either.right(Success.Idle))
}
