package com.linagora.android.linshare.view.dialog

import android.app.Dialog
import android.view.View
import androidx.appcompat.widget.AppCompatButton
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.linagora.android.linshare.R
import com.linagora.android.linshare.view.dialog.DialogProperties.BottomDialogHeightRatio.ConfirmDialogHeightRatio

abstract class BaseConfirmDialogFragment(
    private val layoutId: Int,
    private val negativeText: String,
    private val positiveText: String,
    private val onNegativeCallback: OnNegativeCallback = NoOpCallback,
    private val onPositiveCallback: OnPositiveCallback = NoOpCallback
) : DaggerBottomSheetDialogFragment() {

    override fun setupDialog(dialog: Dialog, style: Int) {
        val view = View.inflate(context, layoutId, null)
        (dialog as BottomSheetDialog).setContentView(view)
        setUpContent(view)
        setUpConfirmButton(view)
        setUpLayoutParams(view)
    }

    abstract fun setUpContent(contentView: View)

    private fun setUpConfirmButton(contentView: View) {
        with(contentView) {
            val cancelBtn = findViewById<AppCompatButton>(R.id.cancelBtn)
            val removeBtn = findViewById<AppCompatButton>(R.id.confirmBtn)
            cancelBtn.text = negativeText
            removeBtn.text = positiveText
            cancelBtn.setOnClickListener {
                onNegativeCallback.invoke(it)
                this@BaseConfirmDialogFragment.dismiss()
            }
            removeBtn.setOnClickListener {
                onPositiveCallback.invoke(it)
                this@BaseConfirmDialogFragment.dismiss()
            }
        }
    }

    open fun setUpLayoutParams(contentView: View) {
        contentView.layoutParams.height =
            (resources.displayMetrics.heightPixels * ConfirmDialogHeightRatio.ratio).toInt()
    }
}
