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

package com.linagora.android.linshare.util

import com.linagora.android.linshare.R
import com.linagora.android.linshare.domain.model.audit.AuditLogEntryType
import com.linagora.android.linshare.domain.model.audit.AuditLogEntryUser
import com.linagora.android.linshare.domain.model.audit.ClientLogAction
import com.linagora.android.linshare.domain.model.audit.workgroup.SharedSpaceMemberAuditLogEntry
import com.linagora.android.linshare.domain.model.audit.workgroup.SharedSpaceNodeAuditLogEntry
import com.linagora.android.linshare.domain.model.audit.workgroup.WorkGroupDocumentAuditLogEntry
import com.linagora.android.linshare.domain.model.audit.workgroup.WorkGroupDocumentRevisionAuditLogEntry
import com.linagora.android.linshare.domain.model.audit.workgroup.WorkGroupFolderAuditLogEntry
import com.linagora.android.linshare.domain.model.sharedspace.WorkGroupDocument
import com.linagora.android.linshare.domain.model.sharedspace.WorkGroupNode
import com.linagora.android.linshare.util.AuditLogEntryExtensions.AUDIT_LOG_MAPPING
import com.linagora.android.linshare.util.Constant.NO_RESOURCE

fun AuditLogEntryUser.getAuditLogIconResourceId(): Int {
    return when (type) {
        AuditLogEntryType.WORKGROUP -> R.drawable.ic_shared_space_item
        AuditLogEntryType.WORKGROUP_MEMBER -> R.drawable.ic_add_person
        AuditLogEntryType.WORKGROUP_FOLDER -> R.drawable.ic_folder
        AuditLogEntryType.WORKGROUP_DOCUMENT -> getDocumentAuditLogIconResourceId()
        AuditLogEntryType.WORKGROUP_DOCUMENT_REVISION -> getDocumentAuditLogIconResourceId()
    }
}

private fun AuditLogEntryUser.getDocumentAuditLogIconResourceId(): Int {
    return when (this) {
        is WorkGroupDocumentAuditLogEntry -> resource.getIconResourceId()
        is WorkGroupDocumentRevisionAuditLogEntry -> resource.getIconResourceId()
        else -> R.drawable.ic_file
    }
}

fun WorkGroupNode.getIconResourceId(): Int {
    return when (this) {
        is WorkGroupDocument -> mimeType.getDrawableIcon()
        else -> R.drawable.ic_folder
    }
}

fun AuditLogEntryUser.getResourceName(): String {
    return when (this) {
        is SharedSpaceMemberAuditLogEntry -> resource.sharedSpaceAccount.name
        is SharedSpaceNodeAuditLogEntry -> resource.name
        is WorkGroupDocumentAuditLogEntry -> resource.name
        is WorkGroupFolderAuditLogEntry -> resource.name
        is WorkGroupDocumentRevisionAuditLogEntry -> resource.name
        else -> this.action.name
    }
}

enum class AuditLogActionMessage {
    TITLE,
    DETAILS
}

object AuditLogEntryExtensions {
    val AUDIT_LOG_MAPPING = mapOf(
        AuditLogEntryType.WORKGROUP to mapOf(
            ClientLogAction.CREATE to mapOf(
                AuditLogActionMessage.TITLE to R.string.audit_action_title_create,
                AuditLogActionMessage.DETAILS to R.string.audit_action_message_create_workgroup
            ),
            ClientLogAction.DELETE to mapOf(
                AuditLogActionMessage.TITLE to R.string.audit_action_title_delete,
                AuditLogActionMessage.DETAILS to R.string.audit_action_message_delete_workgroup
            ),
            ClientLogAction.UPDATE to mapOf(
                AuditLogActionMessage.TITLE to R.string.audit_action_title_update,
                AuditLogActionMessage.DETAILS to R.string.audit_action_message_update_workgroup
            )
        ),
        AuditLogEntryType.WORKGROUP_DOCUMENT to mapOf(
            ClientLogAction.UPLOAD to mapOf(
                AuditLogActionMessage.TITLE to R.string.audit_action_title_upload,
                AuditLogActionMessage.DETAILS to R.string.audit_action_message_upload_workgroup_document
            ),
            ClientLogAction.COPY_FROM_PERSONAL_SPACE to mapOf(
                AuditLogActionMessage.TITLE to R.string.audit_action_title_copy,
                AuditLogActionMessage.DETAILS to R.string.audit_action_message_copy_document_from_personal_space
            ),
            ClientLogAction.COPY_FROM_SHARED_SPACE to mapOf(
                AuditLogActionMessage.TITLE to R.string.audit_action_title_copy,
                AuditLogActionMessage.DETAILS to R.string.audit_action_message_copy_document_from_shared_space
            ),
            ClientLogAction.COPY_FROM_RECEIVED_SHARE to mapOf(
                AuditLogActionMessage.TITLE to R.string.audit_action_title_copy,
                AuditLogActionMessage.DETAILS to R.string.audit_action_message_copy_document_from_received_share
            ),
            ClientLogAction.COPY_TO_PERSONAL_SPACE to mapOf(
                AuditLogActionMessage.TITLE to R.string.audit_action_title_copy,
                AuditLogActionMessage.DETAILS to R.string.audit_action_message_copy_document_to_personal_space
            ),
            ClientLogAction.COPY_TO_SHARED_SPACE to mapOf(
                AuditLogActionMessage.TITLE to R.string.audit_action_title_copy,
                AuditLogActionMessage.DETAILS to R.string.audit_action_message_copy_document_to_shared_space
            ),
            ClientLogAction.DELETE to mapOf(
                AuditLogActionMessage.TITLE to R.string.audit_action_title_delete,
                AuditLogActionMessage.DETAILS to R.string.audit_action_message_delete_workgroup_document
            ),
            ClientLogAction.UPDATE to mapOf(
                AuditLogActionMessage.TITLE to R.string.audit_action_title_update,
                AuditLogActionMessage.DETAILS to R.string.audit_action_message_update_workgroup_document
            ),
            ClientLogAction.DOWNLOAD to mapOf(
                AuditLogActionMessage.TITLE to R.string.audit_action_title_download,
                AuditLogActionMessage.DETAILS to R.string.audit_action_message_download_workgroup_document
            )
        ),
        AuditLogEntryType.WORKGROUP_DOCUMENT_REVISION to mapOf(
            ClientLogAction.UPLOAD_REVISION to mapOf(
                AuditLogActionMessage.TITLE to R.string.audit_action_title_upload_revision,
                AuditLogActionMessage.DETAILS to R.string.audit_action_message_upload_revision
            ),
            ClientLogAction.DELETE_REVISION to mapOf(
                AuditLogActionMessage.TITLE to R.string.audit_action_title_delete_revision,
                AuditLogActionMessage.DETAILS to R.string.audit_action_message_delete_revision
            ),
            ClientLogAction.DOWNLOAD_REVISION to mapOf(
                AuditLogActionMessage.TITLE to R.string.audit_action_title_download_revision,
                AuditLogActionMessage.DETAILS to R.string.audit_action_message_download_revision
            ),
            ClientLogAction.RESTORE_REVISION to mapOf(
                AuditLogActionMessage.TITLE to R.string.audit_action_title_restore_revision,
                AuditLogActionMessage.DETAILS to R.string.audit_action_message_copy_revision_from_shared_space
            )
        ),
        AuditLogEntryType.WORKGROUP_FOLDER to mapOf(
            ClientLogAction.CREATE to mapOf(
                AuditLogActionMessage.TITLE to R.string.audit_action_title_create,
                AuditLogActionMessage.DETAILS to R.string.audit_action_message_create_folder
            ),
            ClientLogAction.DELETE to mapOf(
                AuditLogActionMessage.TITLE to R.string.audit_action_title_delete,
                AuditLogActionMessage.DETAILS to R.string.audit_action_message_delete_folder
            ),
            ClientLogAction.UPDATE to mapOf(
                AuditLogActionMessage.TITLE to R.string.audit_action_title_update,
                AuditLogActionMessage.DETAILS to R.string.audit_action_message_update_folder
            ),
            ClientLogAction.DOWNLOAD to mapOf(
                AuditLogActionMessage.TITLE to R.string.audit_action_title_download,
                AuditLogActionMessage.DETAILS to R.string.audit_action_message_download_folder
            )
        ),
        AuditLogEntryType.WORKGROUP_MEMBER to mapOf(
            ClientLogAction.ADDITION to mapOf(
                AuditLogActionMessage.TITLE to R.string.audit_action_title_addition,
                AuditLogActionMessage.DETAILS to R.string.audit_action_message_add_member
            ),
            ClientLogAction.DELETE to mapOf(
                AuditLogActionMessage.TITLE to R.string.audit_action_title_delete,
                AuditLogActionMessage.DETAILS to R.string.audit_action_message_delete_member
            ),
            ClientLogAction.UPDATE to mapOf(
                AuditLogActionMessage.TITLE to R.string.audit_action_title_update,
                AuditLogActionMessage.DETAILS to R.string.audit_action_message_update_member
            )
        )
    )
}

fun AuditLogEntryUser.getActionTitleResourceId(clientLogAction: ClientLogAction): Int {
    return AUDIT_LOG_MAPPING[type]
        ?.get(clientLogAction)
        ?.get(AuditLogActionMessage.TITLE)
        ?: R.string.audit_action_title_update
}

fun AuditLogEntryUser.getActionDetailsResourceId(clientLogAction: ClientLogAction): Int {
    return AUDIT_LOG_MAPPING[type]
        ?.get(clientLogAction)
        ?.get(AuditLogActionMessage.DETAILS)
        ?: NO_RESOURCE
}
