package com.linagora.android.testshared.extension

import org.mockito.Mockito

object MockitoUtils {

    fun <T> any(): T {
        Mockito.any<T>()
        return uninitialized()
    }

    fun <T> uninitialized(): T = null as T
}
