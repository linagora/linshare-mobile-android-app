package com.linagora.android.linshare.view.dialog

import android.app.Dialog
import android.view.View
import com.linagora.android.linshare.R
import com.linagora.android.linshare.view.dialog.DialogProperties.BottomDialogHeightRatio.ReadStorageExplanationDialogHeightRatio

class ReadStorageExplanationPermissionDialog(
    negativeText: String,
    positiveText: String,
    onNegativeCallback: OnNegativeCallback = NoOpCallback,
    onPositiveCallback: OnPositiveCallback = NoOpCallback
) : BaseConfirmDialogFragment(R.layout.read_storage_explanation_popup_layout, negativeText, positiveText, onNegativeCallback, onPositiveCallback) {

    override fun setUpContent(contentView: View) {
        // do nothing
    }

    override fun setupDialog(dialog: Dialog, style: Int) {
        super.setupDialog(dialog, style)
        dialog.setCancelable(false)
        dialog.setCanceledOnTouchOutside(true)
    }

    override fun setUpLayoutParams(contentView: View) {
        contentView.layoutParams.height =
            (resources.displayMetrics.heightPixels * ReadStorageExplanationDialogHeightRatio.ratio).toInt()
    }
}
