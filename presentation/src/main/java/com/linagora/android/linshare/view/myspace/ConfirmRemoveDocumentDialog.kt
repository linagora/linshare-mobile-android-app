package com.linagora.android.linshare.view.myspace

import android.view.View
import android.widget.TextView
import com.linagora.android.linshare.R
import com.linagora.android.linshare.domain.model.document.Document
import com.linagora.android.linshare.model.resources.LayoutId
import com.linagora.android.linshare.view.dialog.BaseConfirmDialogFragment
import com.linagora.android.linshare.view.dialog.NoOpCallback
import com.linagora.android.linshare.view.dialog.OnNegativeCallback
import com.linagora.android.linshare.view.dialog.OnPositiveCallback

class ConfirmRemoveDocumentDialog(
    private val document: Document,
    private val title: String,
    negativeText: String,
    positiveText: String,
    onNegativeCallback: OnNegativeCallback = NoOpCallback,
    onPositiveCallback: OnPositiveCallback = NoOpCallback
) : BaseConfirmDialogFragment(
    LayoutId(R.layout.dialog_confirm_layout),
    negativeText,
    positiveText,
    onNegativeCallback,
    onPositiveCallback
) {

    override fun setUpContent(contentView: View) {
        with(contentView) {
            val titleDialog = findViewById<TextView>(R.id.titleDialog)
            titleDialog.text = title
        }
    }
}
