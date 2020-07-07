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

package com.linagora.android.linshare.utils

import android.content.Context
import android.os.Build
import androidx.test.core.app.ApplicationProvider
import com.google.common.truth.Truth.assertThat
import com.linagora.android.linshare.util.TimeUtils
import com.linagora.android.linshare.util.TimeUtils.LinShareTimeFormat.LastLoginFormat
import com.linagora.android.linshare.util.TimeUtils.LinShareTimeFormat.LastModifiedFormat
import com.linagora.android.testshared.TestFixtures.Accounts.LAST_LOGIN_DATE
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.time.format.DateTimeFormatter

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.P])
class TimeUtilsTest {

    lateinit var context: Context

    private lateinit var timeUtils: TimeUtils

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        timeUtils = TimeUtils(context)
    }

    @Test
    fun convertToLocalTimeShouldFollowLastLoginFormat() {
        val dateTime = timeUtils.convertToLocalTime(LAST_LOGIN_DATE, LastLoginFormat)

        assertThat(isValidFormat("dd.MM.YYYY hh:mm a", dateTime)).isTrue()
    }

    @Test
    fun convertToLocalTimeShouldFollowLastModifiedFormat() {
        val dateTime = timeUtils.convertToLocalTime(LAST_LOGIN_DATE, LastModifiedFormat)

        assertThat(isValidFormat("MMM dd, YYYY", dateTime)).isTrue()
    }

    private fun isValidFormat(format: String, value: String): Boolean {
        return runCatching {
            val formatter = DateTimeFormatter.ofPattern(format)
            val test = formatter.parse(value)
            return formatter.format(test) == value
        }.getOrDefault(false)
    }
}
