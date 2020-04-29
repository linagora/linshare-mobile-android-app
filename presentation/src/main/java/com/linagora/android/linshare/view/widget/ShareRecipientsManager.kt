package com.linagora.android.linshare.view.widget

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import com.linagora.android.linshare.domain.model.GenericUser
import com.linagora.android.linshare.domain.model.autocomplete.AutoCompletePattern
import com.linagora.android.linshare.domain.usecases.autocomplete.GetAutoCompleteSharingInteractor
import com.linagora.android.linshare.util.Constant
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import org.slf4j.LoggerFactory
import javax.inject.Inject

class ShareRecipientsManager @Inject constructor(
    private val getAutoCompleteSharingInteractor: GetAutoCompleteSharingInteractor
) {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(ShareRecipientsManager::class.java)
    }
    private val queryChannel = BroadcastChannel<AutoCompletePattern>(Channel.CONFLATED)

    private val queryState = queryChannel.asFlow()
        .debounce(Constant.QUERY_INTERVAL_MS)
        .flatMapLatest { getAutoCompleteSharingInteractor(it) }

    val suggestions = queryState.asLiveData()

    private val mutableRecipients = MutableLiveData<Set<GenericUser>>()
        .apply { value = HashSet() }

    val recipients: LiveData<Set<GenericUser>> = mutableRecipients

    suspend fun query(pattern: AutoCompletePattern) {
        queryChannel.send(pattern)
    }

    fun addRecipient(user: GenericUser): Boolean {
        LOGGER.info("addRecipient() $user")
        if (mutableRecipients.value?.contains(user) == true) {
            return false
        }

        val newRecipients = mutableRecipients.value
            ?.let { mutableSetOf(user).plus(it) }

        mutableRecipients.value = newRecipients
        return true
    }

    fun removeRecipient(user: GenericUser) {
        LOGGER.info("removeRecipient() $user")
        mutableRecipients.value?.flatMap { mutableSetOf(it) }
            ?.minus(user)
            ?.toCollection(LinkedHashSet())
            ?.also { mutableRecipients.value = it }
    }

    fun resetRecipients() {
        mutableRecipients.value = mutableSetOf()
    }
}
