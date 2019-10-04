package com.linagora.android.linshare.domain.usecases.utils

class State <T>(private val f: T.() -> T) {
    operator fun invoke(t: T) = t.f()
}
