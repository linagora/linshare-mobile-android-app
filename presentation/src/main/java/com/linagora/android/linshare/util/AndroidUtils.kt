package com.linagora.android.linshare.util

import android.os.Build

object AndroidUtils {

    fun supportAndroidO(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
    }
}
