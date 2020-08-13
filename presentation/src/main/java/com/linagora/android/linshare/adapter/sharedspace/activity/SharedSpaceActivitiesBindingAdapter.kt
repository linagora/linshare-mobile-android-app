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

package com.linagora.android.linshare.adapter.sharedspace.activity

import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import arrow.core.Either
import com.linagora.android.linshare.R
import com.linagora.android.linshare.domain.model.audit.AuditLogEntryUser
import com.linagora.android.linshare.domain.usecases.sharedspace.activity.GetActivitiesFailure
import com.linagora.android.linshare.domain.usecases.sharedspace.activity.GetActivitiesSuccess
import com.linagora.android.linshare.domain.usecases.sharedspace.member.GetMembersNoResult
import com.linagora.android.linshare.domain.usecases.utils.Failure
import com.linagora.android.linshare.domain.usecases.utils.Success
import com.linagora.android.linshare.glide.GlideApp
import com.linagora.android.linshare.util.TimeUtils
import com.linagora.android.linshare.util.TimeUtils.LinShareTimeFormat.LastModifiedFormat
import com.linagora.android.linshare.util.getActionTitleResourceId
import com.linagora.android.linshare.util.getAuditLogIconResourceId
import com.linagora.android.linshare.util.getResourceName

@BindingAdapter("sharedSpaceActivityState")
fun bindingSharedSpaceActivities(
    recyclerView: RecyclerView,
    sharedSpaceActivitiesState: Either<Failure, Success>
) {
    if (recyclerView.adapter == null) {
        recyclerView.adapter = SharedSpaceActivityAdapter()
    }

    sharedSpaceActivitiesState.fold(
        ifLeft = { failure ->
            when (failure) {
                is GetActivitiesFailure, GetMembersNoResult -> recyclerView.isVisible = false
                else -> recyclerView.isVisible = true
            }
        },
        ifRight = { success ->
            recyclerView.isVisible = true
            if (success is GetActivitiesSuccess) {
                (recyclerView.adapter as SharedSpaceActivityAdapter).submitList(success.activities)
            }
        }
    )
}

@BindingAdapter("workGroupAuditIcon")
fun bindingWorkGroupAuditIcon(imageView: ImageView, auditLogEntryUser: AuditLogEntryUser) {
    GlideApp.with(imageView.context)
        .load(auditLogEntryUser.getAuditLogIconResourceId())
        .placeholder(R.drawable.ic_file)
        .into(imageView)
}

@BindingAdapter("workGroupAuditResourceName")
fun bindingWorkGroupAuditResourceName(textView: TextView, auditLogEntryUser: AuditLogEntryUser) {
    textView.text = auditLogEntryUser.getResourceName()
}

@BindingAdapter("workGroupAuditCreationDate")
fun bindingWorkGroupAuditCreationTime(textView: TextView, auditLogEntryUser: AuditLogEntryUser) {
    textView.text = runCatching {
        TimeUtils(textView.context)
            .convertToLocalTime(auditLogEntryUser.creationDate, LastModifiedFormat) }
        .getOrNull()
}

@BindingAdapter("workGroupAuditActor")
fun bindingWorkGroupAuditActor(textView: TextView, auditLogEntryUser: AuditLogEntryUser) {
    textView.text = textView.context.getString(R.string.audit_log_actor, auditLogEntryUser.actor.name)
}

@BindingAdapter("workGroupActivitiesActionTitle")
fun bindingWorkGroupAuditActionTitle(textView: TextView, auditLogEntryUser: AuditLogEntryUser) {
    val clientLogAction = auditLogEntryUser.type.generateClientLogAction(auditLogEntryUser)
    textView.text = textView.context.getString(auditLogEntryUser.getActionTitleResourceId(clientLogAction))
}
