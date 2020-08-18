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

package com.linagora.android.linshare.adapter.member

import android.widget.TextView
import androidx.core.view.isVisible
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import arrow.core.Either
import com.linagora.android.linshare.R
import com.linagora.android.linshare.domain.model.sharedspace.SharedSpaceRoleName
import com.linagora.android.linshare.domain.model.sharedspace.member.SharedSpaceMember
import com.linagora.android.linshare.domain.usecases.sharedspace.member.GetMembersFailed
import com.linagora.android.linshare.domain.usecases.sharedspace.member.GetMembersNoResult
import com.linagora.android.linshare.domain.usecases.sharedspace.member.GetMembersSuccess
import com.linagora.android.linshare.domain.usecases.utils.Failure
import com.linagora.android.linshare.domain.usecases.utils.Success
import com.linagora.android.linshare.util.Constant.EMPTY_BOTTOM_DRAWABLE_RESOURCE
import com.linagora.android.linshare.util.Constant.EMPTY_LEFT_DRAWABLE_RESOURCE
import com.linagora.android.linshare.util.Constant.EMPTY_RIGHT_DRAWABLE_RESOURCE
import com.linagora.android.linshare.util.Constant.EMPTY_TOP_DRAWABLE_RESOURCE
import com.linagora.android.linshare.util.getAvatarCharacter
import com.linagora.android.linshare.util.toDisplayRoleNameId
import com.linagora.android.linshare.view.base.OnSelectRolesForUpdate
import com.linagora.android.linshare.view.base.WorkGroupMemberBehavior

@BindingAdapter("sharedSpaceMemberState", "ownRoleName", "selectRoleForUpdateBehavior", "workGroupMemberBehavior", requireAll = true)
fun bindingSharedSpaceMember(
    recyclerView: RecyclerView,
    sharedSpaceMemberState: Either<Failure, Success>,
    ownRoleName: SharedSpaceRoleName,
    selectRoleForUpdateBehavior: OnSelectRolesForUpdate,
    workGroupMemberBehavior: WorkGroupMemberBehavior
) {
    if (recyclerView.adapter == null) {
        recyclerView.adapter = SharedSpaceMemberAdapter(ownRoleName, selectRoleForUpdateBehavior, workGroupMemberBehavior)
    }

    sharedSpaceMemberState.fold(
        ifLeft = { failure ->
            when (failure) {
                is GetMembersFailed, GetMembersNoResult -> recyclerView.isVisible = false
                else -> recyclerView.isVisible = true
            }
        },
        ifRight = { success ->
            recyclerView.isVisible = true
            if (success is GetMembersSuccess) {
                (recyclerView.adapter as SharedSpaceMemberAdapter)
                    .submitList(success.members)
            }
        }
    )
}

@BindingAdapter("memberRole")
fun bindingMemberRole(textView: TextView, sharedSpaceMember: SharedSpaceMember) {
    val stringId = sharedSpaceMember.role.name.toDisplayRoleNameId()
    textView.text = textView.context.getString(stringId.value)
}

@BindingAdapter("ownRoleName", "operationRoles")
fun bindingClickableForEditRole(textView: TextView, ownRoleName: SharedSpaceRoleName, operationRoles: List<SharedSpaceRoleName>) {
    val clickable = operationRoles.takeIf { it.isNotEmpty() && it.contains(ownRoleName) }
        ?.let { true }
        ?: false
    textView.isEnabled = clickable

    val rightDrawableResource = clickable.takeIf { it }
        ?.let { R.drawable.ic_drop_down }
        ?: EMPTY_RIGHT_DRAWABLE_RESOURCE

    textView.setCompoundDrawablesWithIntrinsicBounds(EMPTY_LEFT_DRAWABLE_RESOURCE, EMPTY_TOP_DRAWABLE_RESOURCE, rightDrawableResource, EMPTY_BOTTOM_DRAWABLE_RESOURCE)
}

@BindingAdapter("memberAvatar")
fun bindingMemberAvatar(textView: TextView, sharedSpaceMember: SharedSpaceMember) {
    textView.text = sharedSpaceMember.getAvatarCharacter().toString()
}
