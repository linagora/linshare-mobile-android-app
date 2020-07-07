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

import android.content.Context
import android.graphics.drawable.AnimationDrawable
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.widget.AppCompatImageView
import com.linagora.android.linshare.R
import com.linagora.android.linshare.domain.model.autocomplete.AutoCompletePattern
import com.linagora.android.linshare.domain.model.autocomplete.AutoCompleteResult
import com.linagora.android.linshare.domain.model.autocomplete.AutoCompleteType
import com.linagora.android.linshare.domain.model.autocomplete.ThreadMemberAutoCompleteResult
import com.linagora.android.linshare.domain.model.autocomplete.UserAutoCompleteResult
import com.linagora.android.linshare.domain.model.sharedspace.SharedSpaceId
import com.linagora.android.linshare.domain.model.sharedspace.SharedSpaceRole
import com.linagora.android.linshare.domain.model.sharedspace.SharedSpaceRoleName
import com.linagora.android.linshare.domain.model.sharedspace.member.SharedSpaceMember
import com.linagora.android.linshare.model.resources.StringId
import com.linagora.android.linshare.util.Constant.DEFAULT_AVATAR_CHARACTER

fun View.showKeyboard() {
    val inputMethodManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.showSoftInput(this, InputMethodManager.SHOW_FORCED)
}

fun View.dismissKeyboard() {
    val inputMethodManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(windowToken, 0)
}

fun AppCompatImageView.startAnimationDrawable() {
    visibility = View.VISIBLE
    (background as? AnimationDrawable)?.start()
}

fun AppCompatImageView.stopAnimationDrawable() {
    (background as? AnimationDrawable)?.stop()
    visibility = View.GONE
}

fun SharedSpaceRoleName.toDisplayRoleNameId(): StringId {
    return StringId(
        when (this) {
            SharedSpaceRoleName.WRITER -> R.string.writer_role_name
            SharedSpaceRoleName.CONTRIBUTOR -> R.string.contributor_role_name
            SharedSpaceRoleName.ADMIN -> R.string.admin_role_name
            else -> R.string.reader_role_name
    })
}

fun SharedSpaceMember.getAvatarCharacter(): Char {
    return sharedSpaceAccount.firstName
        .getFirstLetter()?.get(0)
        ?: sharedSpaceAccount.mail[0]
}

fun UserAutoCompleteResult.getAvatarCharacter(): String {
    return firstName?.getFirstLetter()
        ?: display.getFirstLetter()
        ?: DEFAULT_AVATAR_CHARACTER
}

fun ThreadMemberAutoCompleteResult.getAvatarCharacter(): String {
    return firstName.getFirstLetter()
        ?: display.getFirstLetter()
        ?: DEFAULT_AVATAR_CHARACTER
}

typealias OnRequestMemberAutoComplete = (AutoCompletePattern, AutoCompleteType, SharedSpaceId) -> Unit

typealias OnAddMember = (SharedSpaceId, AutoCompleteResult, SharedSpaceRole) -> Unit
