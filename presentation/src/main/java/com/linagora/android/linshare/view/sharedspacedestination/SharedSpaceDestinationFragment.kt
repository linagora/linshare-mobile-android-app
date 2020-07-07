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

package com.linagora.android.linshare.view.sharedspacedestination

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import arrow.core.Either
import com.linagora.android.linshare.R
import com.linagora.android.linshare.databinding.FragmentSharedSpaceDestinationBinding
import com.linagora.android.linshare.domain.model.sharedspace.SharedSpaceNodeNested
import com.linagora.android.linshare.domain.usecases.sharedspace.SharedSpaceItemClick
import com.linagora.android.linshare.domain.usecases.utils.Success
import com.linagora.android.linshare.model.parcelable.SharedSpaceNavigationInfo
import com.linagora.android.linshare.model.parcelable.WorkGroupNodeIdParcelable
import com.linagora.android.linshare.model.parcelable.toParcelable
import com.linagora.android.linshare.util.getViewModel
import com.linagora.android.linshare.view.MainNavigationFragment
import com.linagora.android.linshare.view.Navigation
import com.linagora.android.linshare.view.upload.UploadFragment
import org.slf4j.LoggerFactory
import javax.inject.Inject

class SharedSpaceDestinationFragment : MainNavigationFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var sharedSpaceDestinationViewModel: SharedSpaceDestinationViewModel

    private lateinit var binding: FragmentSharedSpaceDestinationBinding

    private val args: SharedSpaceDestinationFragmentArgs by navArgs()

    companion object {
        private val LOGGER = LoggerFactory.getLogger(SharedSpaceDestinationFragment::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSharedSpaceDestinationBinding.inflate(inflater, container, false)
        initViewModel(binding)
        return binding.root
    }

    override fun configureToolbar(toolbar: Toolbar) {
        toolbar.setNavigationIcon(R.drawable.ic_navigation_back)
        toolbar.navigationIcon?.setTint(ContextCompat.getColor(context!!, R.color.toolbar_primary_color))
        toolbar.setNavigationOnClickListener { navigateToUpload() }
    }

    private fun initViewModel(binding: FragmentSharedSpaceDestinationBinding) {
        sharedSpaceDestinationViewModel = getViewModel(viewModelFactory)
        binding.lifecycleOwner = this
        binding.viewModel = sharedSpaceDestinationViewModel
        observeViewState()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpSwipeRefreshLayout()
        getSharedSpace()
        setUpOnBackPressed()
    }

    private fun observeViewState() {
        sharedSpaceDestinationViewModel.viewState.observe(viewLifecycleOwner, Observer {
            it.map { success ->
                when (success) {
                    is Success.ViewEvent -> reactToViewEvent(success)
                }
            }
        })
    }

    private fun reactToViewEvent(viewEvent: Success.ViewEvent) {
        when (viewEvent) {
            is SharedSpaceItemClick -> navigateIntoSharedSpace(viewEvent.sharedSpaceNodeNested)
        }
        sharedSpaceDestinationViewModel.dispatchState(Either.right(Success.Idle))
    }

    private fun setUpSwipeRefreshLayout() {
        binding.swipeLayoutSharedSpace.setColorSchemeResources(R.color.colorPrimary)
    }

    private fun getSharedSpace() {
        LOGGER.info("getSharedSpaces")
        sharedSpaceDestinationViewModel.getSharedSpace()
    }

    private fun navigateIntoSharedSpace(sharedSpaceNodeNested: SharedSpaceNodeNested) {
        val action = SharedSpaceDestinationFragmentDirections
            .actionNavigationDestinationToNavigationPickDestination(
                args.uploadType,
                args.uri,
                args.uploadDestinationInfo,
                generateNavigationInfoForSharedSpaceRoot(sharedSpaceNodeNested))
        findNavController().navigate(action)
    }

    private fun generateNavigationInfoForSharedSpaceRoot(sharedSpaceNodeNested: SharedSpaceNodeNested): SharedSpaceNavigationInfo {
        return SharedSpaceNavigationInfo(
            sharedSpaceIdParcelable = sharedSpaceNodeNested.sharedSpaceId.toParcelable(),
            fileType = Navigation.FileType.ROOT,
            nodeIdParcelable = WorkGroupNodeIdParcelable(sharedSpaceNodeNested.sharedSpaceId.uuid)
        )
    }

    private fun setUpOnBackPressed() {
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) { navigateToUpload() }
    }

    private fun navigateToUpload() {
        val uploadDestinationInfo = args.uploadDestinationInfo

        val action = uploadDestinationInfo
            ?.let { navigateUpLoadToDestination() }
            ?: navigateUploadToMySpace()

        findNavController().navigate(action)
    }

    private fun navigateUploadToMySpace(): NavDirections {
        return SharedSpaceDestinationFragmentDirections.actionNavigationDestinationToUploadFragment(
            Navigation.UploadType.OUTSIDE_APP,
            args.uri,
            UploadFragment.UPLOAD_TO_MY_SPACE_DESTINATION_INFO)
    }

    private fun navigateUpLoadToDestination(): NavDirections {
        return SharedSpaceDestinationFragmentDirections.actionNavigationDestinationToUploadFragment(
            args.uploadType,
            args.uri,
            args.uploadDestinationInfo)
    }
}
