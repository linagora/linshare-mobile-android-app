package com.linagora.android.linshare.view.dialog

import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.linagora.android.linshare.R
import com.linagora.android.linshare.domain.model.sharedspace.WorkGroupDocument
import com.linagora.android.linshare.glide.GlideApp
import com.linagora.android.linshare.util.FileSize
import com.linagora.android.linshare.util.getDrawableIcon

@BindingAdapter("workGroupDocumentSize")
fun bindingWorkGroupDocumentSize(textView: TextView, workGroupDocument: WorkGroupDocument?) {
    textView.text = workGroupDocument
        ?.let { FileSize(it.size).format(FileSize.SizeFormat.LONG) }
}

@BindingAdapter("workGroupDocumentIcon")
fun bindingWorkGroupDocumentIcon(imageView: ImageView, workGroupDocument: WorkGroupDocument?) {
    GlideApp.with(imageView.context)
        .load(workGroupDocument?.mimeType?.getDrawableIcon())
        .placeholder(R.drawable.ic_file)
        .into(imageView)
}
