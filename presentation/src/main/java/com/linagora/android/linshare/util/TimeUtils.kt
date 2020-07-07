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
import android.text.format.DateFormat
import androidx.core.os.ConfigurationCompat
import org.threeten.bp.DateTimeUtils
import org.threeten.bp.ZoneId
import org.threeten.bp.format.DateTimeFormatter
import java.util.Date

class TimeUtils(private val context: Context) {

    companion object {
        private const val LAST_LOGIN_FORMAT = "dd.MM.YYYY hh:mm a"

        private const val LAST_MODIFIED_FORMAT = "MMM dd, YYYY"
    }

    sealed class LinShareTimeFormat(val pattern: String) {
        object LastLoginFormat : LinShareTimeFormat(LAST_LOGIN_FORMAT)

        object LastModifiedFormat : LinShareTimeFormat(LAST_MODIFIED_FORMAT)
    }

    fun convertToLocalTime(date: Date, format: LinShareTimeFormat): String {
        val currentLocale = ConfigurationCompat.getLocales(context.resources.configuration)[0]
        val lastModified = DateFormat.getBestDateTimePattern(currentLocale, format.pattern)
        val formatter = DateTimeFormatter.ofPattern(lastModified)
        return DateTimeUtils.toInstant(date)
            .atZone(ZoneId.systemDefault())
            .toLocalDateTime()
            .format(formatter)
    }
}
