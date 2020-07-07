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

package com.linagora.android.linshare.util

import android.content.Context
import com.linagora.android.linshare.R
import com.linagora.android.linshare.util.Constant.INDEX_1
import com.linagora.android.linshare.util.Constant.UP_TO_NEXT_INDEX

class SuggestNewNameUtils(val context: Context) {

    enum class SuggestNameType(val originalSuggestNameId: Int) {
        WORKGROUP(R.string.new_workgroup_original_name)
    }

    fun suggestNewName(listName: List<String>, suggestNameType: SuggestNameType): String {
        val originalSuggestName = context.getString(suggestNameType.originalSuggestNameId)

        return listName.takeIf { it.isNullOrEmpty() || originalSuggestName !in it }
            ?.let { originalSuggestName }
            ?: suggestNameWithIndex(originalSuggestName, listName)
    }

    private fun suggestNameWithIndex(originalSuggestName: String, listName: List<String>): String {
        val sizeList = listName.size
        for (index in INDEX_1..sizeList) {
            val suggestName = patternNameWithIndex(originalSuggestName, index)
            if (suggestName !in listName) {
                return suggestName
            }
        }
        return patternNameWithIndex(originalSuggestName, sizeList.plus(UP_TO_NEXT_INDEX))
    }

    private fun patternNameWithIndex(originalName: String, index: Int): String {
        return originalName.plus(" ($index)")
    }
}
