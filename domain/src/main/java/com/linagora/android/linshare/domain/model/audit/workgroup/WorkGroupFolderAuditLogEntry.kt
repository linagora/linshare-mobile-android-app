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

package com.linagora.android.linshare.domain.model.audit.workgroup

import com.google.gson.annotations.SerializedName
import com.linagora.android.linshare.domain.model.account.Account
import com.linagora.android.linshare.domain.model.audit.AuditLogEntryId
import com.linagora.android.linshare.domain.model.audit.AuditLogEntryType
import com.linagora.android.linshare.domain.model.audit.AuditLogEntryUser
import com.linagora.android.linshare.domain.model.audit.LogAction
import com.linagora.android.linshare.domain.model.audit.LogActionCause
import com.linagora.android.linshare.domain.model.sharedspace.WorkGroupCopy
import com.linagora.android.linshare.domain.model.sharedspace.WorkGroupLight
import com.linagora.android.linshare.domain.model.sharedspace.WorkGroupNode
import java.util.Date
import java.util.UUID

data class WorkGroupFolderAuditLogEntry(
    override val actor: Account,
    @SerializedName("uuid")
    override val auditLogEntryId: AuditLogEntryId,
    override val resourceUuid: UUID,
    override val fromResourceUuid: UUID? = null,
    override val creationDate: Date,
    override val authUser: Account,
    override val type: AuditLogEntryType,
    override val action: LogAction,
    override val cause: LogActionCause? = null,
    val workGroup: WorkGroupLight,
    val resource: WorkGroupNode,
    val resourceUpdated: WorkGroupNode? = null,
    val copiedTo: WorkGroupCopy? = null,
    val copiedFrom: WorkGroupCopy? = null
) : AuditLogEntryUser {
    override fun getActionMessageComponents(): Triple<String, String, String> {
        return Triple(actor.name, resource.name, workGroup.name)
    }
}
