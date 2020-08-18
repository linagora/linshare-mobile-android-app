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

package com.linagora.android.linshare.view.sharedspacedocumentdestination.copy.sharedspace

import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.linagora.android.linshare.domain.model.sharedspace.SharedSpaceId
import com.linagora.android.linshare.domain.model.sharedspace.WorkGroupNode
import com.linagora.android.linshare.domain.model.sharedspace.WorkGroupNodeId
import com.linagora.android.linshare.model.parcelable.SharedSpaceNavigationInfo
import com.linagora.android.linshare.model.parcelable.getCurrentNodeId
import com.linagora.android.linshare.model.parcelable.toParcelable
import com.linagora.android.linshare.model.parcelable.toSharedSpaceId
import com.linagora.android.linshare.model.parcelable.toWorkGroupNodeId
import com.linagora.android.linshare.util.getViewModel
import com.linagora.android.linshare.view.Navigation
import com.linagora.android.linshare.view.sharedspacedocumentdestination.base.DestinationDocumentFragment
import com.linagora.android.linshare.view.sharedspacedocumentdestination.base.DestinationDocumentViewModel

class CopySharedSpaceDestinationDocumentFragment : DestinationDocumentFragment() {

    private val args: CopySharedSpaceDestinationDocumentFragmentArgs by navArgs()

    override val destinationDocumentViewModel: DestinationDocumentViewModel by lazy {
        getViewModel<CopySharedSpaceDestinationDocumentViewModel>(viewModelFactory) }

    override fun bindingNavigationInfo(): SharedSpaceNavigationInfo? {
        return args.navigationInfo
    }

    override fun extractSharedSpaceId(): SharedSpaceId? {
        return args.navigationInfo.sharedSpaceIdParcelable.toSharedSpaceId()
    }

    override fun extractCurrentNodeId(): WorkGroupNodeId? {
        return args.navigationInfo.nodeIdParcelable.toWorkGroupNodeId()
    }

    override fun getRealCurrentNodeId(): WorkGroupNodeId? {
        return args.navigationInfo.getCurrentNodeId()
    }

    override fun navigateIntoSubFolder(subFolder: WorkGroupNode) {
        val actionIntoSubfolder = CopySharedSpaceDestinationDocumentFragmentDirections
            .navigateToFolder(
                args.copyFromSharedSpaceId,
                args.copyFromParentNodeId,
                args.copyFromNodeId,
                SharedSpaceNavigationInfo(
                    subFolder.sharedSpaceId.toParcelable(),
                    Navigation.FileType.NORMAL,
                    subFolder.workGroupNodeId.toParcelable()))

        findNavController().navigate(actionIntoSubfolder)
    }

    override fun navigateInCancelDestination() {
        val actionCancelCopy = CopySharedSpaceDestinationDocumentFragmentDirections
            .navigateToSharedSpacedDocument(
                SharedSpaceNavigationInfo(
                    args.copyFromSharedSpaceId,
                    generateFileTypeToBackToSharedSpaceDocument(),
                    args.copyFromParentNodeId
                )
            )

        findNavController().navigate(actionCancelCopy)
    }

    private fun generateFileTypeToBackToSharedSpaceDocument(): Navigation.FileType {
        if (args.copyFromParentNodeId.uuid == args.copyFromSharedSpaceId.uuid) {
            return Navigation.FileType.ROOT
        }
        return Navigation.FileType.NORMAL
    }

    override fun navigateInChooseDestination() {
        TODO("Not yet implemented")
    }

    override fun navigateBackToSharedSpaceDestination() {
        val actionToDestination = CopySharedSpaceDestinationDocumentFragmentDirections
            .navigateToCopySharedSpaceDestinationFragment(
                args.copyFromSharedSpaceId,
                args.copyFromParentNodeId,
                args.copyFromNodeId
            )

        findNavController().navigate(actionToDestination)
    }

    override fun navigateBackToPreviousFolder(workGroupNode: WorkGroupNode) {
        val destinationFileType = workGroupNode.treePath.takeIf { it.size == 1 }
            ?.let { Navigation.FileType.ROOT }
            ?: Navigation.FileType.NORMAL

        val actionToParent = CopySharedSpaceDestinationDocumentFragmentDirections
            .navigateToFolder(
                args.copyFromSharedSpaceId,
                args.copyFromParentNodeId,
                args.copyFromNodeId,
                SharedSpaceNavigationInfo(
                    workGroupNode.sharedSpaceId.toParcelable(),
                    destinationFileType,
                    workGroupNode.parentWorkGroupNodeId.toParcelable()
                )
            )

        findNavController().navigate(actionToParent)
    }
}
