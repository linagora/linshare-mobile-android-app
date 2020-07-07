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

package com.linagora.android.linshare.util.binding

import android.widget.AdapterView
import androidx.core.widget.doAfterTextChanged
import com.linagora.android.linshare.databinding.AddMembersViewBinding
import com.linagora.android.linshare.domain.model.autocomplete.AutoCompletePattern
import com.linagora.android.linshare.domain.model.autocomplete.AutoCompleteResult
import com.linagora.android.linshare.domain.model.autocomplete.AutoCompleteType
import com.linagora.android.linshare.domain.model.sharedspace.SharedSpaceRoleName
import com.linagora.android.linshare.domain.usecases.sharedspace.role.GetAllSharedSpaceRolesSuccess
import com.linagora.android.linshare.domain.usecases.utils.Success
import com.linagora.android.linshare.util.OnAddMember
import com.linagora.android.linshare.util.OnRequestMemberAutoComplete
import com.linagora.android.linshare.util.binding.AddRecipientsViewBindingExtension.AUTO_COMPLETE_THRESHOLD
import com.linagora.android.linshare.util.dismissKeyboard
import com.linagora.android.linshare.util.showKeyboard

fun AddMembersViewBinding.initView() {
    addMembers.apply {
        threshold = AUTO_COMPLETE_THRESHOLD
    }
}

fun AddMembersViewBinding.bindingRoles(success: Success) {
    if (success is GetAllSharedSpaceRolesSuccess) {
        sharedSpaceRoles = success.roles
    }
}

fun AddMembersViewBinding.bindingDefaultSelectedRole(success: Success) {
    if (selectedRole == null && success is GetAllSharedSpaceRolesSuccess) {
        selectedRole = success.roles.firstOrNull { role -> role.name == SharedSpaceRoleName.READER }
    }
}

fun AddMembersViewBinding.queryAfterTextChange(onRequestMember: OnRequestMemberAutoComplete) {
    addMembers.doAfterTextChanged { pattern ->
        pattern?.toString()
            ?.takeIf { it.isNotBlank() && it.length >= AUTO_COMPLETE_THRESHOLD }
            ?.let { AutoCompletePattern(it) }
            ?.let { onRequestMember(it, AutoCompleteType.THREAD_MEMBERS, sharedSpaceId!!) }
    }
}

fun AddMembersViewBinding.onSelectedMember(onAddMember: OnAddMember) {
    addMembers.onItemClickListener = AdapterView.OnItemClickListener { parent, _, position, _ ->
        addMembers.text.clear()
        val selectedUser = parent.getItemAtPosition(position) as AutoCompleteResult
        onAddMember(sharedSpaceId!!, selectedUser, selectedRole!!)
    }
}

fun AddMembersViewBinding.showKeyBoard(viewState: Success.ViewState) {
    if (viewState is GetAllSharedSpaceRolesSuccess) {
        with(addMembers) {
            requestFocus()
            findFocus().showKeyboard()
        }
    }
}

fun AddMembersViewBinding.clearFocus() {
    with(addMembers) {
        dismissKeyboard()
        clearFocus()
    }
}
