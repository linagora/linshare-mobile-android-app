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

typealias OnAddMember = (AutoCompleteResult, SharedSpaceRole) -> Unit
