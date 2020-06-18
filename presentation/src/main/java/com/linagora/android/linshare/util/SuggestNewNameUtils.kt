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
