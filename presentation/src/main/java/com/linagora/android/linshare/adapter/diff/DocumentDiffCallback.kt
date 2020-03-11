package com.linagora.android.linshare.adapter.diff

import androidx.recyclerview.widget.DiffUtil
import com.linagora.android.linshare.domain.model.document.Document

object DocumentDiffCallback : DiffUtil.ItemCallback<Document>() {

    override fun areItemsTheSame(oldItem: Document, newItem: Document): Boolean {
        return oldItem.documentId == newItem.documentId
    }

    override fun areContentsTheSame(oldItem: Document, newItem: Document): Boolean {
        return oldItem == newItem
    }
}
