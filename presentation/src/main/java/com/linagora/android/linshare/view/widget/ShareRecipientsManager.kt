package com.linagora.android.linshare.view.widget

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import com.linagora.android.linshare.domain.model.GenericUser
import com.linagora.android.linshare.domain.model.autocomplete.AutoCompletePattern
import com.linagora.android.linshare.domain.model.autocomplete.MailingList
import com.linagora.android.linshare.domain.usecases.autocomplete.GetReceiverSuggestionInteractor
import com.linagora.android.linshare.util.Constant
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import org.slf4j.LoggerFactory
import javax.inject.Inject

class ShareRecipientsManager @Inject constructor(
    private val getReceiverSuggestionInteractor: GetReceiverSuggestionInteractor
) {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(ShareRecipientsManager::class.java)
    }
    private val queryChannel = BroadcastChannel<AutoCompletePattern>(Channel.CONFLATED)

    private val queryState = queryChannel.asFlow()
        .debounce(Constant.QUERY_INTERVAL_MS)
        .flatMapLatest { getReceiverSuggestionInteractor(it) }

    val suggestions = queryState.asLiveData()

    private val mutableRecipients = MutableLiveData<Set<GenericUser>>()
        .apply { value = HashSet() }

    val recipients: LiveData<Set<GenericUser>> = mutableRecipients

    private val mutableMailingLists = MutableLiveData<Set<MailingList>>()
        .apply { value = HashSet() }

    val mailingLists: LiveData<Set<MailingList>> = mutableMailingLists

    val shareReceiverCount = MediatorLiveData<Int>()

    init {
        shareReceiverCount.addSource(recipients) { onUpdateReceiver(it, mailingLists.value) }
        shareReceiverCount.addSource(mailingLists) { onUpdateReceiver(recipients.value, it) }
    }

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

    private fun resetRecipients() {
        mutableRecipients.value = mutableSetOf()
    }

    fun addMailingList(mailingList: MailingList): Boolean {
        LOGGER.info("addMailingList(): $mailingList")
        if (mutableMailingLists.value?.contains(mailingList) == true) {
            return false
        }

        val newMailingLists = mutableMailingLists.value
            ?.let { mutableSetOf(mailingList).plus(it) }

        mutableMailingLists.value = newMailingLists
        return true
    }

    fun removeMailingList(mailingList: MailingList) {
        LOGGER.info("removeMailingList(): $mailingList")
        mutableMailingLists.value?.flatMap { mutableSetOf(it) }
            ?.minus(mailingList)
            ?.toCollection(LinkedHashSet())
            ?.also { mutableMailingLists.value = it }
    }

    private fun onUpdateReceiver(recipients: Set<GenericUser>?, mailingLists: Set<MailingList>?) {
        val count = recipients?.size ?: 0
        shareReceiverCount.postValue(mailingLists
            ?.let { it.size + count }
            ?: count
        )
    }

    private fun resetMailingLists() {
        mutableMailingLists.value = mutableSetOf()
    }

    fun resetShareRecipientManager() {
        resetRecipients()
        resetMailingLists()
    }
}
