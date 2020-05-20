package com.linagora.android.linshare.view.dialog

import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.linagora.android.linshare.R
import com.linagora.android.linshare.domain.model.sharedspace.WorkGroupDocument
import com.linagora.android.linshare.domain.model.sharedspace.WorkGroupNode
import com.linagora.android.linshare.glide.GlideApp
import com.linagora.android.linshare.util.FileSize
import com.linagora.android.linshare.util.getDrawableIcon

@BindingAdapter("workGroupNodeSize")
fun bindingWorkGroupNodeSize(textView: TextView, workGroupNode: WorkGroupNode?) {
    textView.text = workGroupNode.takeIf { it is WorkGroupDocument }
        ?.let { FileSize((it as WorkGroupDocument).size).format(FileSize.SizeFormat.LONG) }
}

@BindingAdapter("workGroupNodeIcon")
fun bindingWorkGroupNodeIcon(imageView: ImageView, workGroupNode: WorkGroupNode?) {
    GlideApp.with(imageView.context)
        .load(workGroupNode.takeIf { it is WorkGroupDocument }
            ?.let { (it as WorkGroupDocument)?.mimeType?.getDrawableIcon() }
            ?: R.drawable.ic_folder)
        .placeholder(R.drawable.ic_file)
        .into(imageView)
}
