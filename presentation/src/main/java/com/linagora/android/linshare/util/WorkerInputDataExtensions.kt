package com.linagora.android.linshare.util

import androidx.work.Data

fun Data.append(data: Data): Data {
    return Data.Builder()
        .putAll(this)
        .putAll(data)
        .build()
}
