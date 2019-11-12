package com.linagora.android.linshare.view.dialog

import android.app.Dialog
import android.view.View
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.linagora.android.linshare.R
import com.linagora.android.linshare.util.Constant.CONFIRM_DIALOG_HEIGHT_RATIO

open class BaseConfirmDialogFragment(
    private val title: String,
    private val negativeText: String,
    private val positiveText: String,
    private val onNegative: ((View) -> Unit)? = null,
    private val onPositive: ((View) -> Unit)? = null
) : DaggerBottomSheetDialogFragment() {

    override fun setupDialog(dialog: Dialog, style: Int) {
        val view = View.inflate(context, R.layout.dialog_confirm_layout, null)
        (dialog as BottomSheetDialog).setContentView(view)
        setUpContent(view)
    }

    private fun setUpContent(contentView: View) {
        with(contentView) {
            val titleDialog = findViewById<TextView>(R.id.titleDialog)
            val cancelBtn = findViewById<AppCompatButton>(R.id.cancelBtn)
            val removeBtn = findViewById<AppCompatButton>(R.id.confirmBtn)
            titleDialog.text = title
            cancelBtn.text = negativeText
            removeBtn.text = positiveText
            cancelBtn.setOnClickListener {
                this@BaseConfirmDialogFragment.dismiss()
                onNegative?.invoke(it)
            }
            removeBtn.setOnClickListener {
                this@BaseConfirmDialogFragment.dismiss()
                onPositive?.invoke(it)
            }
        }
        contentView.layoutParams.height = (resources.displayMetrics.heightPixels * CONFIRM_DIALOG_HEIGHT_RATIO)
            .toInt()
    }
}
