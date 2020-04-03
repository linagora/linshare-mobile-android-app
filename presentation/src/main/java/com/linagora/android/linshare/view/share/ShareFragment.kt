package com.linagora.android.linshare.view.share

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import com.linagora.android.linshare.R
import com.linagora.android.linshare.databinding.FragmentShareBinding
import com.linagora.android.linshare.model.parcelable.DocumentParcelable
import com.linagora.android.linshare.model.parcelable.toDocument
import com.linagora.android.linshare.view.MainNavigationFragment

class ShareFragment : MainNavigationFragment() {

    companion object {
        const val SHARE_DOCUMENT_BUNDLE_KEY = "shareDocument"
    }

    private lateinit var binding: FragmentShareBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentShareBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindingShareDocument()
    }

    override fun configureToolbar(toolbar: Toolbar) {
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back)
    }

    private fun bindingShareDocument() {
        val bundle = requireArguments()
        bundle.getParcelable<DocumentParcelable>(SHARE_DOCUMENT_BUNDLE_KEY)
            ?.toDocument()
            ?.run { binding.document = this }
    }
}
