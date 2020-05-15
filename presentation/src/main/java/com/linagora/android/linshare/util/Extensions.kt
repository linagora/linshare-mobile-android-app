package com.linagora.android.linshare.util

import android.content.Intent
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.linagora.android.linshare.view.OpenFilePickerRequestCode

inline fun <reified VM : ViewModel> FragmentActivity.getViewModel(
    provider: ViewModelProvider.Factory
) = ViewModelProviders.of(this, provider).get(VM::class.java)

inline fun <reified VM : ViewModel> Fragment.getViewModel(
    provider: ViewModelProvider.Factory
) = ViewModelProviders.of(this, provider).get(VM::class.java)

inline fun <reified VM : ViewModel> Fragment.getParentViewModel(
    provider: ViewModelProvider.Factory
) = ViewModelProviders.of(this.parentFragment!!, provider).get(VM::class.java)

fun Fragment.openFilePicker() {
    val getDocumentIntent = Intent(Intent.ACTION_OPEN_DOCUMENT)
    getDocumentIntent.type = MimeType.ALL_TYPE
    startActivityForResult(getDocumentIntent, OpenFilePickerRequestCode.code)
}
