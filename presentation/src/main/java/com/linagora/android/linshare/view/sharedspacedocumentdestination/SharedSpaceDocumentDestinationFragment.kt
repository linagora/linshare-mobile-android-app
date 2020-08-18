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

package com.linagora.android.linshare.view.sharedspacedocumentdestination

import android.net.Uri
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.linagora.android.linshare.domain.model.sharedspace.SharedSpaceId
import com.linagora.android.linshare.domain.model.sharedspace.WorkGroupDocument
import com.linagora.android.linshare.domain.model.sharedspace.WorkGroupNode
import com.linagora.android.linshare.domain.model.sharedspace.WorkGroupNodeId
import com.linagora.android.linshare.model.parcelable.SelectedDestinationInfo
import com.linagora.android.linshare.model.parcelable.SharedSpaceNavigationInfo
import com.linagora.android.linshare.model.parcelable.WorkGroupNodeIdParcelable
import com.linagora.android.linshare.model.parcelable.getCurrentNodeId
import com.linagora.android.linshare.model.parcelable.toParcelable
import com.linagora.android.linshare.model.parcelable.toSharedSpaceId
import com.linagora.android.linshare.model.parcelable.toWorkGroupNodeId
import com.linagora.android.linshare.util.generateFileType
import com.linagora.android.linshare.util.getViewModel
import com.linagora.android.linshare.view.Event
import com.linagora.android.linshare.view.Navigation
import com.linagora.android.linshare.view.Navigation.FileType
import com.linagora.android.linshare.view.sharedspacedocumentdestination.base.DestinationDocumentFragment
import com.linagora.android.linshare.view.sharedspacedocumentdestination.base.DestinationDocumentViewModel
import org.slf4j.LoggerFactory

class SharedSpaceDocumentDestinationFragment : DestinationDocumentFragment() {

    companion object {
        private val LOGGER = LoggerFactory.getLogger(SharedSpaceDocumentDestinationFragment::class.java)

        private const val ONLY_ROOT_ITEM = 1
    }

    private val arguments: SharedSpaceDocumentDestinationFragmentArgs by navArgs()

    override val destinationDocumentViewModel: DestinationDocumentViewModel by lazy {
        getViewModel<SharedSpaceDocumentDestinationViewModel>(viewModelFactory)
    }

    override fun bindingNavigationInfo(): SharedSpaceNavigationInfo? {
        return arguments.navigationInfo
            ?: arguments.selectedDestinationInfo
                ?.let(this@SharedSpaceDocumentDestinationFragment::generateBindingNavigationInfo)
    }

    override fun extractSharedSpaceId(): SharedSpaceId? {
        return arguments.navigationInfo?.sharedSpaceIdParcelable?.toSharedSpaceId()
            ?: arguments.selectedDestinationInfo?.sharedSpaceDestinationInfo?.sharedSpaceIdParcelable?.toSharedSpaceId()
    }

    override fun extractCurrentNodeId(): WorkGroupNodeId? {
        return arguments.navigationInfo?.nodeIdParcelable?.toWorkGroupNodeId()
            ?: arguments.selectedDestinationInfo?.parentDestinationInfo?.parentNodeId?.toWorkGroupNodeId()
    }

    override fun getRealCurrentNodeId(): WorkGroupNodeId? {
        arguments.navigationInfo
            ?.let { return it.getCurrentNodeId() }
            ?: return arguments.selectedDestinationInfo
                ?.parentDestinationInfo?.parentNodeId?.toWorkGroupNodeId()
    }

    private fun generateBindingNavigationInfo(selectedDestinationInfo: SelectedDestinationInfo): SharedSpaceNavigationInfo {
        val fileType = selectedDestinationInfo.generateFileType()
        return SharedSpaceNavigationInfo(
            selectedDestinationInfo.sharedSpaceDestinationInfo.sharedSpaceIdParcelable,
            fileType,
            generateBindingParentNodeIdParcelable(fileType, selectedDestinationInfo)
        )
    }

    private fun generateBindingParentNodeIdParcelable(fileType: FileType, selectedDestinationInfo: SelectedDestinationInfo): WorkGroupNodeIdParcelable {
        return fileType.takeIf { it == FileType.NORMAL }
            ?.let { selectedDestinationInfo.parentDestinationInfo.parentNodeId }
            ?: WorkGroupNodeIdParcelable(selectedDestinationInfo.sharedSpaceDestinationInfo.sharedSpaceIdParcelable.uuid)
    }

    override fun generateSelectNodeId(currentNode: WorkGroupNode): WorkGroupNodeId {
        return generateSelectedNodeIdByFileType(currentNode)
    }

    private fun generateSelectedNodeIdByFileType(currentNode: WorkGroupNode): WorkGroupNodeId {
        return arguments.navigationInfo.takeIf { it?.fileType == FileType.ROOT }
            ?.let { currentNode.parentWorkGroupNodeId }
            ?: currentNode.workGroupNodeId
    }

    private fun generateNavigationInfoForSubFolder(workGroupNode: WorkGroupNode): SharedSpaceNavigationInfo {
        return SharedSpaceNavigationInfo(
            sharedSpaceIdParcelable = workGroupNode.sharedSpaceId.toParcelable(),
            fileType = FileType.NORMAL,
            nodeIdParcelable = WorkGroupNodeIdParcelable(workGroupNode.workGroupNodeId.uuid)
        )
    }

    private fun generateNavigationInfoForPreviousFolder(workGroupNode: WorkGroupNode): SharedSpaceNavigationInfo {
        val lastTreePath = workGroupNode.treePath.last()
        val destinationFileType = workGroupNode.treePath.takeIf { it.size == ONLY_ROOT_ITEM }
            ?.let { FileType.ROOT }
            ?: FileType.NORMAL
        return SharedSpaceNavigationInfo(
            sharedSpaceIdParcelable = workGroupNode.sharedSpaceId.toParcelable(),
            fileType = destinationFileType,
            nodeIdParcelable = WorkGroupNodeIdParcelable(lastTreePath.workGroupNodeId.uuid)
        )
    }

    override fun navigateIntoSubFolder(subFolder: WorkGroupNode) {
        if (subFolder is WorkGroupDocument) {
            return
        }

        val action = SharedSpaceDocumentDestinationFragmentDirections.actionNavigationPickDestinationToPickDestination(
            arguments.uploadType,
            arguments.uri,
            arguments.selectedDestinationInfo,
            generateNavigationInfoForSubFolder(subFolder))
        findNavController().navigate(action)
    }

    override fun navigateInCancelDestination() {
        navigateToUpload(
            Navigation.UploadType.OUTSIDE_APP,
            arguments.uri,
            arguments.selectedDestinationInfo,
            Event.DestinationPickerEvent.CANCEL)
    }

    override fun navigateInChooseDestination() {
        runCatching { selectCurrentDestination() }
            .onFailure { LOGGER.error("handleChooseDestination(): ${it.printStackTrace()} - ${it.message}") }
            .map { navigateToUpload(Navigation.UploadType.OUTSIDE_APP_TO_WORKGROUP, arguments.uri, it, Event.DestinationPickerEvent.CHOOSE) }
    }

    private fun navigateToUpload(
        uploadType: Navigation.UploadType,
        uri: Uri,
        selectedDestinationInfo: SelectedDestinationInfo?,
        destinationPickerEvent: Event.DestinationPickerEvent
    ) {
        val action = SharedSpaceDocumentDestinationFragmentDirections
            .actionNavigationPickDestinationToUploadFragment(
                uploadType,
                uri,
                selectedDestinationInfo,
                destinationPickerEvent)
        findNavController().navigate(action)
    }

    override fun navigateBackToPreviousFolder(workGroupNode: WorkGroupNode) {
        val action = SharedSpaceDocumentDestinationFragmentDirections.actionNavigationPickDestinationToPickDestination(
            arguments.uploadType,
            arguments.uri,
            arguments.selectedDestinationInfo,
            generateNavigationInfoForPreviousFolder(workGroupNode))
        findNavController().navigate(action)
    }

    override fun navigateBackToSharedSpaceDestination() {
        val action = SharedSpaceDocumentDestinationFragmentDirections.actionNavigationPickDestinationToNavigationDestination(
            arguments.uploadType,
            arguments.uri,
            arguments.selectedDestinationInfo)
        findNavController().navigate(action)
    }
}
