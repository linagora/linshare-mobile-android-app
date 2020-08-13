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

package com.linagora.android.linshare.domain.model.audit

import com.linagora.android.linshare.domain.model.audit.AuditLogEntryTypeObject.LOGGER
import com.linagora.android.linshare.domain.model.audit.workgroup.WorkGroupDocumentAuditLogEntry
import com.linagora.android.linshare.domain.model.audit.workgroup.WorkGroupDocumentRevisionAuditLogEntry
import com.linagora.android.linshare.domain.model.copy.SpaceType
import org.slf4j.LoggerFactory

enum class AuditLogEntryType {
    WORKGROUP {
        override fun generateClientLogAction(auditLogEntry: AuditLogEntry): ClientLogAction {
            return when (auditLogEntry.action) {
                LogAction.CREATE -> ClientLogAction.CREATE
                LogAction.DELETE -> ClientLogAction.DELETE
                else -> ClientLogAction.UPDATE
            }
        }
    },
    WORKGROUP_MEMBER {
        override fun generateClientLogAction(auditLogEntry: AuditLogEntry): ClientLogAction {
            return when (auditLogEntry.action) {
                LogAction.CREATE -> ClientLogAction.ADDITION
                LogAction.DELETE -> ClientLogAction.DELETE
                else -> ClientLogAction.UPDATE
            }
        }
    },
    WORKGROUP_FOLDER {
        override fun generateClientLogAction(auditLogEntry: AuditLogEntry): ClientLogAction {
            return when (auditLogEntry.action) {
                LogAction.CREATE -> ClientLogAction.CREATE
                LogAction.DELETE -> ClientLogAction.DELETE
                LogAction.DOWNLOAD -> ClientLogAction.DOWNLOAD
                else -> ClientLogAction.UPDATE
            }
        }
    },
    WORKGROUP_DOCUMENT {
        override fun generateClientLogAction(auditLogEntry: AuditLogEntry): ClientLogAction {
            return auditLogEntry.cause?.takeIf { it == LogActionCause.COPY }
                ?.let { getClientLogActionForCopyDocumentAction(auditLogEntry) }
                ?: mappingClientLogAction(auditLogEntry.action)
        }

        private fun mappingClientLogAction(logAction: LogAction): ClientLogAction {
            return when (logAction) {
                LogAction.CREATE -> ClientLogAction.UPLOAD
                LogAction.DELETE -> ClientLogAction.DELETE
                LogAction.DOWNLOAD -> ClientLogAction.DOWNLOAD
                else -> ClientLogAction.UPDATE
            }
        }

        private fun getClientLogActionForCopyDocumentAction(auditLogEntry: AuditLogEntry): ClientLogAction {
            return runCatching {
                require(auditLogEntry is WorkGroupDocumentAuditLogEntry) { "invalid log entry" }
                require(auditLogEntry.copiedFrom != null || auditLogEntry.copiedTo != null) { "log entry is not a copy action" }
                if (auditLogEntry.copiedFrom != null) {
                    return when (auditLogEntry.copiedFrom.kind) {
                        SpaceType.PERSONAL_SPACE -> ClientLogAction.COPY_FROM_PERSONAL_SPACE
                        SpaceType.RECEIVED_SHARE -> ClientLogAction.COPY_FROM_RECEIVED_SHARE
                        SpaceType.SHARED_SPACE -> ClientLogAction.COPY_FROM_SHARED_SPACE
                    }
                }
                return auditLogEntry.copiedTo!!.let {
                    when (it.kind) {
                        SpaceType.SHARED_SPACE -> ClientLogAction.COPY_TO_SHARED_SPACE
                        else -> ClientLogAction.COPY_TO_PERSONAL_SPACE
                    }
                }
            }.onFailure {
                it.printStackTrace()
                LOGGER.error("getClientLogActionForDocumentUpdateAction(): ${it.message}")
            }.getOrDefault(mappingClientLogAction(auditLogEntry.action))
        }
    },
    WORKGROUP_DOCUMENT_REVISION {
        override fun generateClientLogAction(auditLogEntry: AuditLogEntry): ClientLogAction {
            return runCatching {
                require(auditLogEntry is WorkGroupDocumentRevisionAuditLogEntry) { "invalid log entry" }
                require(auditLogEntry.copiedFrom != null) { "invalid copy action" }
                require(auditLogEntry.copiedFrom.kind == SpaceType.SHARED_SPACE) { "copy revision action should from SHARED_SPACE" }
                return ClientLogAction.RESTORE_REVISION
            }.onFailure {
                it.printStackTrace()
                LOGGER.error("generateClientLogAction(): ${it.message}")
            }.getOrDefault(mappingClientLogAction(auditLogEntry.action))
        }

        private fun mappingClientLogAction(logAction: LogAction): ClientLogAction {
            return when (logAction) {
                LogAction.CREATE -> ClientLogAction.UPLOAD_REVISION
                LogAction.DELETE -> ClientLogAction.DELETE_REVISION
                LogAction.DOWNLOAD -> ClientLogAction.DOWNLOAD_REVISION
                else -> ClientLogAction.UPDATE
            }
        }
    };

    abstract fun generateClientLogAction(auditLogEntry: AuditLogEntry): ClientLogAction
}

internal object AuditLogEntryTypeObject {
    internal val LOGGER = LoggerFactory.getLogger(AuditLogEntryType::class.java)
}
