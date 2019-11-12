package com.linagora.android.linshare.view.dialog

import android.view.View

class ConfirmRemoveAccountDialog(
    title: String,
    negativeText: String,
    positiveText: String,
    onNegative: ((View) -> Unit)? = null,
    onPositive: ((View) -> Unit)? = null
) : BaseConfirmDialogFragment(title, negativeText, positiveText, onNegative, onPositive)
