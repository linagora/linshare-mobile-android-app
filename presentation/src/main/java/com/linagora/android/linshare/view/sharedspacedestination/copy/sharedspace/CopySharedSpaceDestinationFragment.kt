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

package com.linagora.android.linshare.view.sharedspacedestination.copy.sharedspace

import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.linagora.android.linshare.domain.model.sharedspace.SharedSpaceId
import com.linagora.android.linshare.domain.model.sharedspace.SharedSpaceNodeNested
import com.linagora.android.linshare.domain.model.sharedspace.WorkGroupNodeId
import com.linagora.android.linshare.model.parcelable.SharedSpaceNavigationInfo
import com.linagora.android.linshare.model.parcelable.WorkGroupNodeIdParcelable
import com.linagora.android.linshare.model.parcelable.toParcelable
import com.linagora.android.linshare.model.parcelable.toSharedSpaceId
import com.linagora.android.linshare.model.parcelable.toWorkGroupNodeId
import com.linagora.android.linshare.util.getViewModel
import com.linagora.android.linshare.view.Navigation
import com.linagora.android.linshare.view.sharedspacedestination.base.DestinationFragment
import com.linagora.android.linshare.view.sharedspacedestination.base.DestinationViewModel

class CopySharedSpaceDestinationFragment : DestinationFragment() {

    object CopySharedSpaceDestination {

        fun generateFileTypeToBackToCopyFromDestination(
            copyFromSharedSpaceId: SharedSpaceId,
            copyFromParentNodeId: WorkGroupNodeId
        ): Navigation.FileType {
            if (copyFromParentNodeId.uuid == copyFromSharedSpaceId.uuid) {
                return Navigation.FileType.ROOT
            }
            return Navigation.FileType.NORMAL
        }
    }

    private val args: CopySharedSpaceDestinationFragmentArgs by navArgs()

    override val destinationViewModel: DestinationViewModel by lazy {
        getViewModel<CopySharedSpaceDestinationViewModel>(viewModelFactory) }

    override fun toolbarNavigationListener() = navigateToCopyFrom()

    override fun onDestinationBackPressed() = navigateToCopyFrom()

    private fun navigateToCopyFrom() {
        val actionToBack = CopySharedSpaceDestinationFragmentDirections
            .actionNavigateToSharedSpacedDocument(
                SharedSpaceNavigationInfo(
                    args.copyFromSharedSpaceId,
                    CopySharedSpaceDestination.generateFileTypeToBackToCopyFromDestination(
                        args.copyFromSharedSpaceId.toSharedSpaceId(),
                        args.copyFromParentNodeId.toWorkGroupNodeId()
                    ),
                    args.copyFromParentNodeId
                ),
                searchInfo = args.searchInfo
            )

        findNavController().navigate(actionToBack)
    }

    override fun navigateIntoDocumentDestination(sharedSpaceNodeNested: SharedSpaceNodeNested) {
        val actionToDocument = CopySharedSpaceDestinationFragmentDirections
            .navigateToCopySharedSpaceDestinationDocumentFragment(
                copyFromSharedSpaceId = args.copyFromSharedSpaceId,
                copyFromParentNodeId = args.copyFromParentNodeId,
                copyFromNodeId = args.copyFromNodeId,
                navigationInfo = SharedSpaceNavigationInfo(
                    sharedSpaceNodeNested.sharedSpaceId.toParcelable(),
                    Navigation.FileType.ROOT,
                    WorkGroupNodeIdParcelable(sharedSpaceNodeNested.sharedSpaceId.uuid)),
                searchInfo = args.searchInfo
            )

        findNavController().navigate(actionToDocument)
    }
}
