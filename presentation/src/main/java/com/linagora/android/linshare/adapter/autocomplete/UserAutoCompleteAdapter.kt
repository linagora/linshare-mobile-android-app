package com.linagora.android.linshare.adapter.autocomplete

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.linagora.android.linshare.R
import com.linagora.android.linshare.domain.model.autocomplete.UserAutoCompleteResult
import com.linagora.android.linshare.domain.model.autocomplete.fullName
import com.linagora.android.linshare.model.resources.LayoutId
import com.linagora.android.linshare.util.getFirstLetter
import java.util.concurrent.atomic.AtomicReference

class UserAutoCompleteAdapter(context: Context, private val layoutId: LayoutId) :
    ArrayAdapter<UserAutoCompleteResult>(context, layoutId.layoutResId) {

    companion object {
        private const val NO_SUGGESTION_ITEM = 1
    }

    enum class StateSuggestionUser {
        FOUND, NOT_FOUND
    }

    private val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    private var suggestions: List<UserAutoCompleteResult> = ArrayList()

    private val stateSuggestions = AtomicReference(StateSuggestionUser.NOT_FOUND)

    override fun getItem(position: Int): UserAutoCompleteResult? {
        return suggestions.takeIf { suggestions.isNotEmpty() }
            ?.let { suggestions[position] }
    }

    override fun getCount(): Int {
        return takeIf { stateSuggestions.get() == StateSuggestionUser.NOT_FOUND }
            ?.let { NO_SUGGESTION_ITEM }
            ?: suggestions.size
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val currentItem = getItem(position)

        val (inflatedView, viewHolder) = convertView?.tag
            ?.let { Pair(convertView, it as UserAutoCompleteViewHolder) }
            ?: inflateView(parent)

        currentItem?.also { viewHolder.apply {
            bindingAvatar(avatarTextView, currentItem)
            bindingName(nameTextView, currentItem)
            bindingMail(mailTextView, currentItem)
        } }

        return inflatedView
    }

    private fun bindingAvatar(avatarTextView: TextView, item: UserAutoCompleteResult) {
        setVisibleView(avatarTextView)
        avatarTextView.text = getAvatarCharacter(item)
    }

    private fun bindingName(nameTextView: TextView, item: UserAutoCompleteResult) {
        setVisibleView(nameTextView)
        nameTextView.text = item.fullName() ?: item.display
    }

    private fun bindingMail(mailTextView: TextView, item: UserAutoCompleteResult) {
        val (textColorId, textContent) = takeIf { stateSuggestions.get() == StateSuggestionUser.NOT_FOUND }
            ?.let { Pair(R.color.error_border_color, mailTextView.context.getString(R.string.unknown_user)) }
            ?: Pair(R.color.file_name_color, item.mail ?: item.display)

        mailTextView.setTextColor(textColorId)
        mailTextView.text = textContent
    }

    private fun setVisibleView(view: View) {
        view.visibility = takeIf { stateSuggestions.get() == StateSuggestionUser.NOT_FOUND }
            ?.let { View.GONE }
            ?: View.VISIBLE
    }

    private fun getAvatarCharacter(userAutoCompleteResult: UserAutoCompleteResult): String {
        return userAutoCompleteResult.firstName?.getFirstLetter()
            ?: userAutoCompleteResult.display?.getFirstLetter()
            ?: "U"
    }

    fun submitList(newSuggestions: List<UserAutoCompleteResult>) {
        suggestions = newSuggestions
    }

    fun submitStateSuggestions(state: StateSuggestionUser) {
        stateSuggestions.set(state)
    }

    private fun inflateView(parent: ViewGroup): Pair<View, UserAutoCompleteViewHolder> {
        val convertView = inflater.inflate(layoutId.layoutResId, parent, false)
        val userAutoCompleteViewHolder = UserAutoCompleteViewHolder(
            avatarTextView = convertView.findViewById(R.id.userAvatar),
            nameTextView = convertView.findViewById(R.id.userFullName),
            mailTextView = convertView.findViewById(R.id.userMail)
        )
        convertView.tag = userAutoCompleteViewHolder
        return Pair(convertView, userAutoCompleteViewHolder)
    }

    internal data class UserAutoCompleteViewHolder(
        val avatarTextView: TextView,
        val nameTextView: TextView,
        val mailTextView: TextView
    )
}
