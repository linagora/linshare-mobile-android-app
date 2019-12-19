package com.linagora.android.linshare.view.dialog

import android.view.View
import android.widget.TextView
import com.linagora.android.linshare.R

class ConfirmRemoveAccountDialog(
    private val title: String,
    negativeText: String,
    positiveText: String,
    onNegativeCallback: OnNegativeCallback = NoOpCallback,
    onPositiveCallback: OnPositiveCallback = NoOpCallback
) : BaseConfirmDialogFragment(R.layout.dialog_confirm_layout, negativeText, positiveText, onNegativeCallback, onPositiveCallback) {

    override fun setUpContent(contentView: View) {
        with(contentView) {
            val titleDialog = findViewById<TextView>(R.id.titleDialog)
            titleDialog.text = title
        }
    }
}
