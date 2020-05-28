package com.linagora.android.linshare.util

import android.content.Context
import android.graphics.drawable.Drawable
import android.graphics.drawable.shapes.OvalShape
import androidx.core.content.ContextCompat
import com.linagora.android.linshare.R
import com.linagora.android.linshare.domain.model.GenericUser
import com.linagora.android.linshare.domain.model.autocomplete.MailingList
import com.linagora.android.linshare.drawable.LetterAvatarDrawable
import com.linagora.android.linshare.model.resources.ResourceColor

fun GenericUser.generateCircleLetterAvatar(context: Context): Drawable {
    return LetterAvatarDrawable(
        ovalShape = OvalShape(),
        letterColor = ResourceColor(ContextCompat.getColor(context, R.color.white)),
        letter = firstName?.first() ?: mail.first()
    )
}

fun MailingList.generateCircleLetterAvatar(context: Context): Drawable {
    return LetterAvatarDrawable(
        ovalShape = OvalShape(),
        letterColor = ResourceColor(ContextCompat.getColor(context, R.color.white)),
        letter = display.first()
    )
}
