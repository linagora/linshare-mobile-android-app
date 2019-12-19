package com.linagora.android.linshare.view.dialog

import android.view.View

typealias OnNegativeCallback = (View) -> Unit

typealias OnPositiveCallback = (View) -> Unit

object NoOpCallback : (View) -> Unit {

    override fun invoke(view: View) {
        //do nothing
    }
}
