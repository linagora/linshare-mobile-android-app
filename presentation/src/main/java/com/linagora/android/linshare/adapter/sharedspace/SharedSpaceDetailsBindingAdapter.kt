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

package com.linagora.android.linshare.adapter.sharedspace

import android.view.View
import android.widget.CheckBox
import android.widget.TextView
import androidx.databinding.BindingAdapter
import arrow.core.Either
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.linagora.android.linshare.domain.model.sharedspace.SharedSpaceRole
import com.linagora.android.linshare.domain.model.sharedspace.SharedSpaceRoleName
import com.linagora.android.linshare.domain.model.sharedspace.VersioningParameter
import com.linagora.android.linshare.domain.usecases.quota.GetQuotaSuccess
import com.linagora.android.linshare.domain.usecases.sharedspace.GetSharedSpaceSuccess
import com.linagora.android.linshare.domain.usecases.utils.Failure
import com.linagora.android.linshare.domain.usecases.utils.Success
import com.linagora.android.linshare.util.FileSize
import com.linagora.android.linshare.util.FileSize.SizeFormat
import com.linagora.android.linshare.util.toDisplayRoleNameId

@BindingAdapter("sharedSpaceDetailsTitle")
fun bindingDetailsTitle(textView: TextView, sharedSpaceDetailsState: Either<Failure, Success>) {
    sharedSpaceDetailsState.map { success ->
        if (success is GetSharedSpaceSuccess) {
            textView.text = success.sharedSpace.name
        }
    }
}

@BindingAdapter("contextActionVisible", "operationRoles", requireAll = true)
fun bindingAddMembersWithRole(
    addMembersButton: FloatingActionButton,
    sharedSpaceDetailsState: Either<Failure, Success>,
    operationRoles: List<SharedSpaceRoleName>
) {
    sharedSpaceDetailsState.map { success ->
        if (success is GetSharedSpaceSuccess) {
            val visible = operationRoles.takeIf { it.isNotEmpty() && it.contains(success.sharedSpace.role.name) }
                ?.let { View.VISIBLE }
                ?: View.GONE
            addMembersButton.visibility = visible
        }
    }
}

@BindingAdapter("roleName")
fun bindingRoleName(textView: TextView, sharedSpaceRole: SharedSpaceRole?) {
    textView.text = sharedSpaceRole?.name?.toDisplayRoleNameId()
        ?.let { textView.context.getString(it.value) }
}

@BindingAdapter("versioningEnabled")
fun bindingDetailsVersioningEnable(checkBox: CheckBox, versioningParameter: VersioningParameter?) {
    checkBox.isChecked = versioningParameter?.enabled ?: false
}

@BindingAdapter("sharedSpaceUsedSpace")
fun bindingSharedSpaceUsedSpace(textView: TextView, quotaState: Either<Failure, Success>) {
    quotaState.map { success ->
        when (success) {
            is GetQuotaSuccess -> textView.text = FileSize(success.quota.usedSpace.size)
                .format(SizeFormat.LONG)
        }
    }
}

@BindingAdapter("sharedSpaceMaxFileSize")
fun bindingSharedSpaceMaxFileSize(textView: TextView, quotaState: Either<Failure, Success>) {
    quotaState.map { success ->
        when (success) {
            is GetQuotaSuccess -> textView.text = FileSize(success.quota.maxFileSize.size)
                .format(SizeFormat.LONG)
        }
    }
}
