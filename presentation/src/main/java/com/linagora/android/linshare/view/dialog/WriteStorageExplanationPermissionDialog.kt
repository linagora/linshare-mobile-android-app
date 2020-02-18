package com.linagora.android.linshare.view.dialog

import android.app.Dialog
import android.view.View
import com.linagora.android.linshare.R
import com.linagora.android.linshare.model.resources.LayoutId
import com.linagora.android.linshare.view.dialog.DialogProperties.BottomDialogHeightRatio.ReadStorageExplanationDialogHeightRatio

class WriteStorageExplanationPermissionDialog(
    negativeText: String,
    positiveText: String,
    onNegativeCallback: OnNegativeCallback = NoOpCallback,
    onPositiveCallback: OnPositiveCallback = NoOpCallback
) : BaseConfirmDialogFragment(
    LayoutId(R.layout.write_storage_explanation_popup),
    negativeText,
    positiveText,
    onNegativeCallback,
    onPositiveCallback
) {

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
