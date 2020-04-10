package com.linagora.android.linshare.util

fun String.getFirstLetter(): String? {
    return this.takeIf { isNotEmpty() && isNotBlank() }
        ?.let(String::first)
        ?.let(Char::toString)
}
