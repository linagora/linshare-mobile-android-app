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

import android.content.Context
import android.view.View
import android.widget.Adapter
import android.widget.AdapterView
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.widget.doAfterTextChanged
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipDrawable
import com.linagora.android.linshare.R
import com.linagora.android.linshare.adapter.autocomplete.UserAutoCompleteAdapter
import com.linagora.android.linshare.adapter.autocomplete.UserAutoCompleteAdapter.StateSuggestionUser
import com.linagora.android.linshare.databinding.AddRecipientsViewBinding
import com.linagora.android.linshare.domain.model.GenericUser
import com.linagora.android.linshare.domain.model.autocomplete.AutoCompletePattern
import com.linagora.android.linshare.domain.model.autocomplete.AutoCompleteResult
import com.linagora.android.linshare.domain.model.autocomplete.MailingList
import com.linagora.android.linshare.domain.model.fullName
import com.linagora.android.linshare.util.binding.AddRecipientsViewBindingExtension.AUTO_COMPLETE_THRESHOLD
import com.linagora.android.linshare.util.generateCircleLetterAvatar
import com.linagora.android.linshare.util.showKeyboard
import com.linagora.android.linshare.view.dialog.OnRemoveMailingList
import com.linagora.android.linshare.view.dialog.OnRemoveRecipient
import com.linagora.android.linshare.view.share.ShareFragment

object AddRecipientsViewBindingExtension {
    const val AUTO_COMPLETE_THRESHOLD = 3
}

fun AddRecipientsViewBinding.initView() {
    addRecipients.apply {
        findFocus().showKeyboard()
        threshold = AUTO_COMPLETE_THRESHOLD
    }
}

fun AddRecipientsViewBinding.addRecipientView(
    context: Context,
    user: GenericUser,
    onRemoveRecipient: OnRemoveRecipient
) {
    val recipientChip = createChip(context)
        .with(user)
    recipientChip.setOnCloseIconClickListener {
        removeRecipientView(it)
        onRemoveRecipient(it.tag as GenericUser)
    }
    addRecipientView(recipientChip)
}

fun AddRecipientsViewBinding.addMailingListView(
    context: Context,
    mailingList: MailingList,
    onRemoveMailingList: OnRemoveMailingList
) {
    val mailingListChip = createChip(context)
        .with(mailingList)
    mailingListChip.setOnCloseIconClickListener {
        removeRecipientView(it)
        onRemoveMailingList(it.tag as MailingList)
    }
    addRecipientView(mailingListChip)
}

fun AddRecipientsViewBinding.addRecipientView(recipientChip: Chip) {
    recipientContainer.addView(recipientChip, 0)
}

fun AddRecipientsViewBinding.removeRecipientView(recipientChipView: View) {
    recipientContainer.removeView(recipientChipView)
}

fun AddRecipientsViewBinding.queryAfterTextChange(action: (AutoCompletePattern) -> Unit) {
    addRecipients.doAfterTextChanged { pattern ->
        pattern?.toString()
            ?.takeIf { it.isNotBlank() && it.length >= AUTO_COMPLETE_THRESHOLD }
            ?.let { AutoCompletePattern(it) }
            ?.let { action(it) }
    }
}

fun AddRecipientsViewBinding.onSelectedRecipient(action: (AutoCompleteResult) -> Unit) {
    addRecipients.onItemClickListener = AdapterView.OnItemClickListener { parent, _, position, _ ->
        if (parent.adapter?.filterFreezeSuggestionState() == true) {
            return@OnItemClickListener
        }
        addRecipients.text.clear()
        val selectedUser = parent.getItemAtPosition(position) as AutoCompleteResult
        action(selectedUser)
    }
}

fun Adapter.filterFreezeSuggestionState(): Boolean {
    if (this is UserAutoCompleteAdapter) {
        return getStateSuggestion() == StateSuggestionUser.NOT_FOUND
    }
    return false
}

fun AddRecipientsViewBinding.createChip(context: Context): Chip {
    return Chip(context).apply {

        setChipDrawable(
            ChipDrawable.createFromAttributes(
                context,
                ShareFragment.RECIPIENT_ATTRIBUTES,
                ShareFragment.NO_RECIPIENT_ATTRIBUTES_RESOURCE,
                R.style.RecipientChip
            )
        )
    }
}

fun Chip.with(genericUser: GenericUser): Chip {
    val iconTint = ContextCompat.getColor(context, R.color.colorAccent)
    val icon = genericUser.generateCircleLetterAvatar(context)
        .also { DrawableCompat.setTint(it, iconTint) }

    chipIcon = icon
    text = genericUser.fullName() ?: genericUser.mail

    tag = genericUser
    return this
}

fun Chip.with(mailingList: MailingList): Chip {
    val iconTint = ContextCompat.getColor(context, R.color.colorAccent)
    val icon = mailingList.generateCircleLetterAvatar(context)
        .also { DrawableCompat.setTint(it, iconTint) }

    chipIcon = icon
    text = mailingList.display

    tag = mailingList
    return this
}
