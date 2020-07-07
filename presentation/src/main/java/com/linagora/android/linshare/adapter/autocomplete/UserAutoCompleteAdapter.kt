/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 *
 * Copyright (C) 2020 LINAGORA
 *
 * This program is free software: you can redistribute it and/or modify it under the
 * terms of the GNU Affero General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later version,
 * provided you comply with the Additional Terms applicable for LinShare software by
 * Linagora pursuant to Section 7 of the GNU Affero General Public License,
 * subsections (b), (c), and (e), pursuant to which you must notably (i) retain the
 * display in the interface of the “LinShare™” trademark/logo, the "Libre & Free" mention,
 * the words “You are using the Free and Open Source version of LinShare™, powered by
 * Linagora © 2009–2020. Contribute to Linshare R&D by subscribing to an Enterprise
 * offer!”. You must also retain the latter notice in all asynchronous messages such as
 * e-mails sent with the Program, (ii) retain all hypertext links between LinShare and
 * http://www.linshare.org, between linagora.com and Linagora, and (iii) refrain from
 * infringing Linagora intellectual property rights over its trademarks and commercial
 * brands. Other Additional Terms apply, see
 * <http://www.linshare.org/licenses/LinShare-License_AfferoGPL-v3.pdf>
 * for more details.
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for
 * more details.
 * You should have received a copy of the GNU Affero General Public License and its
 * applicable Additional Terms for LinShare along with this program. If not, see
 * <http://www.gnu.org/licenses/> for the GNU Affero General Public License version
 *  3 and <http://www.linshare.org/licenses/LinShare-License_AfferoGPL-v3.pdf> for
 *  the Additional Terms applicable to LinShare software.
 */

package com.linagora.android.linshare.adapter.autocomplete

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.linagora.android.linshare.R
import com.linagora.android.linshare.domain.model.autocomplete.AutoCompleteResult
import com.linagora.android.linshare.domain.model.autocomplete.SimpleAutoCompleteResult
import com.linagora.android.linshare.domain.model.autocomplete.ThreadMemberAutoCompleteResult
import com.linagora.android.linshare.domain.model.autocomplete.UserAutoCompleteResult
import com.linagora.android.linshare.domain.model.autocomplete.fullName
import com.linagora.android.linshare.model.resources.LayoutId
import com.linagora.android.linshare.util.getAvatarCharacter
import com.linagora.android.linshare.util.getFirstLetter
import java.util.concurrent.atomic.AtomicReference

class UserAutoCompleteAdapter(context: Context, private val layoutId: LayoutId) :
    ArrayAdapter<AutoCompleteResult>(context, layoutId.layoutResId) {

    companion object {
        private const val NO_SUGGESTION_ITEM = 1

        private const val DEFAULT_AVATAR_CHARACTER = "U"
    }

    enum class StateSuggestionUser {
        FOUND, NOT_FOUND, EXTERNAL_USER
    }

    private val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    private var suggestions: List<AutoCompleteResult> = ArrayList()

    private val stateSuggestions = AtomicReference(StateSuggestionUser.NOT_FOUND)

    override fun getItem(position: Int): AutoCompleteResult? {
        return suggestions.takeIf { suggestions.isNotEmpty() }
            ?.let { suggestions[position] }
    }

    override fun getCount(): Int {
        return takeIf { isNotFoundInternalUser(stateSuggestions.get()) }
            ?.let { NO_SUGGESTION_ITEM }
            ?: suggestions.size
    }

    private fun isNotFoundInternalUser(state: StateSuggestionUser): Boolean {
        return state == StateSuggestionUser.NOT_FOUND || state == StateSuggestionUser.EXTERNAL_USER
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val currentItem = getItem(position)

        val (inflatedView, viewHolder) = convertView?.tag
            ?.let { Pair(convertView, it as UserAutoCompleteViewHolder) }
            ?: inflateView(parent)

        viewHolder.apply {
            bindingAvatar(avatarTextView, currentItem)
            bindingName(nameTextView, currentItem)
            bindingMail(mailTextView, currentItem)
        }

        return inflatedView
    }

    private fun bindingAvatar(avatarTextView: TextView, item: AutoCompleteResult?) {
        setVisibleView(avatarTextView)
        avatarTextView.text = getAvatarCharacter(item)
    }

    private fun bindingName(nameTextView: TextView, autoCompleteResult: AutoCompleteResult?) {
        setVisibleView(nameTextView)
        nameTextView.text = getSuggestionName(autoCompleteResult)
    }

    private fun getSuggestionName(autoCompleteResult: AutoCompleteResult?): String? {
        return when (autoCompleteResult) {
            is UserAutoCompleteResult -> autoCompleteResult.fullName() ?: autoCompleteResult.display
            is ThreadMemberAutoCompleteResult -> autoCompleteResult.fullName() ?: autoCompleteResult.display
            else -> autoCompleteResult?.display
        }
    }

    private fun bindingMail(mailTextView: TextView, item: AutoCompleteResult?) {
        val (textColorId, textContent) =
            when (stateSuggestions.get()) {
                StateSuggestionUser.NOT_FOUND -> Pair(R.color.error_border_color, mailTextView.context.getString(R.string.unknown_user))
                StateSuggestionUser.EXTERNAL_USER -> Pair(R.color.file_name_color, item?.display)
                else -> Pair(R.color.file_name_color, getSuggestionMail(item))
            }

        mailTextView.setTextColor(ContextCompat.getColor(mailTextView.context, textColorId))
        mailTextView.text = textContent
    }

    private fun getSuggestionMail(autoCompleteResult: AutoCompleteResult?): String? {
        return when (autoCompleteResult) {
            is UserAutoCompleteResult -> autoCompleteResult.mail ?: autoCompleteResult.display
            is SimpleAutoCompleteResult -> autoCompleteResult.identifier
            is ThreadMemberAutoCompleteResult -> autoCompleteResult.mail ?: autoCompleteResult.display
            else -> autoCompleteResult?.display
        }
    }

    private fun setVisibleView(view: View) {
        view.visibility = takeIf { stateSuggestions.get() == StateSuggestionUser.NOT_FOUND }
            ?.let { View.GONE }
            ?: View.VISIBLE
    }

    private fun getAvatarCharacter(autoCompleteResult: AutoCompleteResult?): String {
        return when (autoCompleteResult) {
            is UserAutoCompleteResult -> autoCompleteResult.getAvatarCharacter()
            is ThreadMemberAutoCompleteResult -> autoCompleteResult.getAvatarCharacter()
            else -> autoCompleteResult?.display?.getFirstLetter()
                ?: DEFAULT_AVATAR_CHARACTER
        }
    }

    fun submitList(newSuggestions: List<AutoCompleteResult>) {
        suggestions = newSuggestions
        notifyDataSetChanged()
    }

    fun submitStateSuggestions(state: StateSuggestionUser) {
        stateSuggestions.set(state)
        notifyDataSetChanged()
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
