package com.linagora.android.linshare.view.dialog

import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.linagora.android.linshare.R
import com.linagora.android.linshare.domain.model.share.Share
import com.linagora.android.linshare.glide.GlideApp
import com.linagora.android.linshare.util.FileSize
import com.linagora.android.linshare.util.getDrawableIcon

@BindingAdapter("shareSize")
fun bindingShareSize(textView: TextView, share: Share?) {
    textView.text = share
        ?.let { FileSize(it.size).format(FileSize.SizeFormat.LONG) }
}

@BindingAdapter("shareIcon")
fun bindingDocumentIcon(imageView: ImageView, share: Share?) {
    GlideApp.with(imageView.context)
        .load(share?.type?.getDrawableIcon())
        .placeholder(R.drawable.ic_file)
        .into(imageView)
}
