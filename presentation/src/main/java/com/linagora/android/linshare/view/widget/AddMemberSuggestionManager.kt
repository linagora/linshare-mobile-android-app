package com.linagora.android.linshare.view.widget

import androidx.lifecycle.asLiveData
import com.linagora.android.linshare.domain.model.autocomplete.ThreadMemberAutoCompleteRequest
import com.linagora.android.linshare.domain.usecases.autocomplete.GetAutoCompleteSharingInteractor
import com.linagora.android.linshare.util.Constant
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import javax.inject.Inject

class AddMemberSuggestionManager @Inject constructor(
    private val getAutoCompleteSharing: GetAutoCompleteSharingInteractor
) {

    private val queryChannel = BroadcastChannel<ThreadMemberAutoCompleteRequest>(Channel.CONFLATED)

    private val queryState = queryChannel.asFlow()
        .debounce(Constant.QUERY_INTERVAL_MS)
        .flatMapLatest {
            getAutoCompleteSharing(it.autoCompletePattern, it.autoCompleteType, it.threadId)
        }

    val suggestions = queryState.asLiveData()

    suspend fun query(threadMemberAutoCompleteRequest: ThreadMemberAutoCompleteRequest) {
        queryChannel.send(threadMemberAutoCompleteRequest)
    }
}
