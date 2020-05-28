package com.linagora.android.linshare.view.dialog

import android.view.View
import com.linagora.android.linshare.domain.model.GenericUser
import com.linagora.android.linshare.domain.model.autocomplete.MailingList

typealias OnNegativeCallback = (View) -> Unit

typealias OnPositiveCallback = (View) -> Unit

object NoOpCallback : (View) -> Unit {

    override fun invoke(view: View) {
        // do nothing
    }
}

typealias OnRemoveRecipient = (GenericUser) -> Unit

typealias OnRemoveMailingList = (MailingList) -> Unit
