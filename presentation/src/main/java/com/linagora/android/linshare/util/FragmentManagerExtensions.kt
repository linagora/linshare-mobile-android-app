package com.linagora.android.linshare.util

import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager

fun FragmentManager.dismissDialogFragmentByTag(tag: String) {
    val dialog = findFragmentByTag(tag)
    if (dialog is DialogFragment) {
        dialog.dismiss()
    }
}
