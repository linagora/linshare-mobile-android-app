package com.linagora.android.linshare.util.binding

import android.content.Context
import android.view.View
import android.widget.AdapterView
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.widget.doAfterTextChanged
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipDrawable
import com.linagora.android.linshare.R
import com.linagora.android.linshare.databinding.AddRecipientsViewBinding
import com.linagora.android.linshare.domain.model.GenericUser
import com.linagora.android.linshare.domain.model.autocomplete.AutoCompletePattern
import com.linagora.android.linshare.domain.model.autocomplete.UserAutoCompleteResult
import com.linagora.android.linshare.domain.model.fullName
import com.linagora.android.linshare.util.binding.AddRecipientsViewBindingExtension.AUTO_COMPLETE_THRESHOLD
import com.linagora.android.linshare.util.generateCircleLetterAvatar
import com.linagora.android.linshare.util.showKeyboard
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
    val recipientChip = createRecipientChip(context, user, onRemoveRecipient)
    addRecipientView(recipientChip)
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

fun AddRecipientsViewBinding.onSelectedRecipient(action: (UserAutoCompleteResult) -> Unit) {
    addRecipients.onItemClickListener = AdapterView.OnItemClickListener { parent, _, position, _ ->
        addRecipients.text.clear()
        val selectedUser = parent.getItemAtPosition(position) as UserAutoCompleteResult
        action(selectedUser)
    }
}

private fun AddRecipientsViewBinding.createRecipientChip(context: Context, genericUser: GenericUser, onRemoveRecipient: OnRemoveRecipient): Chip {
    return Chip(context).apply {

        setChipDrawable(
            ChipDrawable.createFromAttributes(
                context,
                ShareFragment.RECIPIENT_ATTRIBUTES,
                ShareFragment.NO_RECIPIENT_ATTRIBUTES_RESOURCE,
                R.style.RecipientChip
            ))

        val iconTint = ContextCompat.getColor(context, R.color.colorAccent)
        val icon = genericUser.generateCircleLetterAvatar(context)
            .also { DrawableCompat.setTint(it, iconTint) }

        chipIcon = icon
        text = genericUser.fullName() ?: genericUser.mail

        tag = genericUser

        setOnCloseIconClickListener {
            removeRecipientView(it)
            onRemoveRecipient(it.tag as GenericUser)
        }
    }
}
