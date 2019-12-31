package com.linagora.android.linshare.adapter.myspace.diff

import androidx.recyclerview.widget.DiffUtil
import com.linagora.android.linshare.domain.model.document.Document

object DocumentDiffCallback : DiffUtil.ItemCallback<Document>() {

    override fun areItemsTheSame(oldItem: Document, newItem: Document): Boolean {
        return oldItem.uuid == newItem.uuid
    }

    override fun areContentsTheSame(oldItem: Document, newItem: Document): Boolean {
        return oldItem == newItem
    }
}
