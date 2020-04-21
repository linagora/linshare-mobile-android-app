package com.linagora.android.linshare.view.widget

import android.content.Context
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.linagora.android.linshare.R

fun Snackbar.withLinShare(context: Context): Snackbar {
    view.background = ContextCompat.getDrawable(context, R.drawable.border_toast_layout)
    setActionTextColor(ContextCompat.getColor(context, R.color.white))
    return this
}
