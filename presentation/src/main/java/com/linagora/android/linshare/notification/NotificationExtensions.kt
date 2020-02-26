package com.linagora.android.linshare.notification

import android.app.Notification

fun Notification.setAutoCancel(): Notification {
    this.flags = flags.or(Notification.FLAG_AUTO_CANCEL)
    return this
}
