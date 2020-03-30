package com.linagora.android.linshare.view.dialog

import android.text.format.Formatter
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.linagora.android.linshare.R
import com.linagora.android.linshare.domain.model.document.Document
import com.linagora.android.linshare.glide.GlideApp
import com.linagora.android.linshare.util.getDrawableIcon

@BindingAdapter("documentSize")
fun bindingFileSize(textView: TextView, document: Document?) {
    textView.text = runCatching { Formatter.formatFileSize(textView.context, document!!.size) }
        .getOrNull()
}

@BindingAdapter("documentName")
fun bindingDocumentName(textView: TextView, document: Document?) {
    textView.text = runCatching { document!!.name }
        .getOrNull()
}

@BindingAdapter("documentIcon")
fun bindingDocumentIcon(imageView: ImageView, document: Document?) {
    GlideApp.with(imageView.context)
        .load(document?.type?.getDrawableIcon())
        .placeholder(R.drawable.ic_file)
        .into(imageView)
}
