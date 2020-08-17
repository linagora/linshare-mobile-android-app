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

object Constant {

    const val LINSHARE_APPLICATION_ID = "com.linagora.android.linshare"

    const val DEFAULT_LINSHARE_BASE_URL = "http://localhost.com"

    const val DEFAULT_TIMEOUT_SECONDS = 30L

    const val UPLOAD_URI_BUNDLE_KEY = "uriFile"

    const val NO_TIMEOUT = 0L

    const val QUERY_INTERVAL_MS = 500L

    const val MIN_LENGTH_CHARACTERS_TO_SEARCH = 3

    const val CLEAR_QUERY_STRING = ""

    const val NOT_SUBMIT_TEXT = false

    const val AUTO_COMPLETE_RESULT_TYPE_SIMPLE = "simple"

    const val AUTO_COMPLETE_RESULT_TYPE_USER = "user"

    const val AUTO_COMPLETE_RESULT_TYPE_MAILING_LIST = "mailinglist"

    const val AUTO_COMPLETE_RESULT_TYPE_THREAD_MEMBER = "threadmember"

    const val DEFAULT_AVATAR_CHARACTER = "U"

    const val INDEX_1 = 1

    const val UP_TO_NEXT_INDEX = 1

    const val NO_RESOURCE = -1

    const val EMPTY_TOP_DRAWABLE_RESOURCE = 0
    const val EMPTY_BOTTOM_DRAWABLE_RESOURCE = 0
    const val EMPTY_LEFT_DRAWABLE_RESOURCE = 0
    const val EMPTY_RIGHT_DRAWABLE_RESOURCE = 0

    object Session {
        val NO_SESSION_ID = null
        const val J_SESSION_ID = "JSESSIONID"
    }

    object Tokens {
        val NO_TOKEN = null
    }
}
