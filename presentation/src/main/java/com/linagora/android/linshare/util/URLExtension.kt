package com.linagora.android.linshare.util

import com.linagora.android.linshare.network.ServicePath
import java.net.URL

fun URL.withServicePath(servicePath: ServicePath): URL {
    val urlString = this.toString()
    val newUrl = urlString
        .takeIf { it.endsWith("/") }
        ?.let { it.plus(servicePath.path) }
        ?: urlString.plus("/${servicePath.path}")
    return URL(newUrl)
}
