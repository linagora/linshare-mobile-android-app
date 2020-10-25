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

package com.linagora.android.linshare.util.binding

import android.view.inputmethod.EditorInfo
import android.widget.EditText
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.text.isDigitsOnly
import com.linagora.android.linshare.databinding.SecondFactorAuthenticationCodeViewBinding
import com.linagora.android.linshare.domain.model.secondfa.SecondFactorAuthCode
import com.linagora.android.linshare.util.afterTextChanged
import com.linagora.android.linshare.util.binding.SecondFactorAuthenticationCodeViewBindingExtensions.NO_CODES
import com.linagora.android.linshare.view.dialog.OnSecondFactorAuthChange

object SecondFactorAuthenticationCodeViewBindingExtensions {
    val NO_CODES = null
}

private fun SecondFactorAuthenticationCodeViewBinding.allInputCode() = listOf(
    codeZero, codeOne, codeTwo, codeThree, codeFour, codeFive
)

private fun SecondFactorAuthenticationCodeViewBinding.validateCompletedState(): Boolean {
    return allInputCode().all(AppCompatEditText::digitValidation)
}

fun SecondFactorAuthenticationCodeViewBinding.getSecondFactorAuthCode(): SecondFactorAuthCode? {
    if (validateCompletedState()) {
        val codeBuilder = StringBuilder(codeZero.text.toString())
            .append(codeOne.text.toString())
            .append(codeTwo.text.toString())
            .append(codeThree.text.toString())
            .append(codeFour.text.toString())
            .append(codeFive.text.toString())
        return SecondFactorAuthCode(codeBuilder.toString())
    }
    return NO_CODES
}

fun SecondFactorAuthenticationCodeViewBinding.initView() {
    codeZero.afterTextChanged { input ->
        input.takeIf { it.isNotEmpty() }
            ?.let { codeOne.requestFocus() }
    }

    codeOne.afterTextChanged { input ->
        input.takeIf { it.isNotEmpty() }
            ?.let { codeTwo.requestFocus() }
            ?: codeZero.requestFocus()
    }

    codeTwo.afterTextChanged { input ->
        input.takeIf { it.isNotEmpty() }
            ?.let { codeThree.requestFocus() }
            ?: codeOne.requestFocus()
    }

    codeThree.afterTextChanged { input ->
        input.takeIf { it.isNotEmpty() }
            ?.let { codeFour.requestFocus() }
            ?: codeTwo.requestFocus()
    }

    codeFour.afterTextChanged { input ->
        input.takeIf { it.isNotEmpty() }
            ?.let { codeFive.requestFocus() }
            ?: codeThree.requestFocus()
    }

    codeFive.afterTextChanged { input ->
        input.takeIf { it.isEmpty() }
            ?.let { codeFour.requestFocus() }
    }
}

fun SecondFactorAuthenticationCodeViewBinding.onSecondFactorAuthChange(
    onSecondFactorAuthChange: OnSecondFactorAuthChange
) {
    allInputCode().map {
        it.afterTextChanged { onSecondFactorAuthChange(getSecondFactorAuthCode()) }
    }

    codeFive.setOnEditorActionListener { _, actionId, _ ->
        when (actionId) {
            EditorInfo.IME_ACTION_DONE -> onSecondFactorAuthChange(getSecondFactorAuthCode())
        }
        false
    }
}

fun EditText.digitValidation(): Boolean {
    val input = text.toString().trim()
    if (input.isNullOrEmpty() || input.isNullOrBlank()) {
        return false
    }

    if (input.length > 1) {
        return false
    }

    if (!input.isDigitsOnly()) {
        return false
    }

    return true
}
