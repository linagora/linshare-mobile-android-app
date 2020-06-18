package com.linagora.android.linshare.view.dialog

import android.view.View
import com.linagora.android.linshare.domain.model.GenericUser
import com.linagora.android.linshare.domain.model.autocomplete.MailingList
import com.linagora.android.linshare.domain.model.workgroup.NewNameRequest

typealias OnNegativeCallback = (View) -> Unit

typealias OnPositiveCallback = (View) -> Unit

object NoOpCallback : (View) -> Unit {

    override fun invoke(view: View) {
        // do nothing
    }
}

typealias OnRemoveRecipient = (GenericUser) -> Unit

typealias OnRemoveMailingList = (MailingList) -> Unit

typealias OnPositiveWithEnteredCharactersCallback = (String) -> Unit

typealias OnNewNameRequestChange = (NewNameRequest) -> Unit
