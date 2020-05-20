package com.linagora.android.linshare.util

import android.os.Build
import com.linagora.android.linshare.domain.utils.ErrorResponseConstant.DEVICE_NOT_ENOUGH_SPACE_MESSAGE
import java.io.IOException

object AndroidUtils {

    fun supportAndroidO(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
    }

    fun isDeviceNotEnoughStorage(throwable: Throwable) = throwable is IOException &&
            throwable.message.equals(DEVICE_NOT_ENOUGH_SPACE_MESSAGE)
}
