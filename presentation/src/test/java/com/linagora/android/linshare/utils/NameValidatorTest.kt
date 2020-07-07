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

import arrow.core.Either
import com.google.common.truth.Truth.assertThat
import com.linagora.android.linshare.domain.usecases.sharedspace.BlankNameError
import com.linagora.android.linshare.domain.usecases.sharedspace.NameContainSpecialCharacter
import com.linagora.android.linshare.domain.usecases.sharedspace.ValidName
import com.linagora.android.linshare.util.NameValidator
import org.junit.Before
import org.junit.Test

class NameValidatorTest {

    private lateinit var nameValidator: NameValidator

    companion object {

        private const val BLANK_CHAIN = ""

        private const val NORMAL_CHAIN = "New workgroup"

        private const val SPECIAL_CHARACTERS_CHAIN_CONTAIN_RIGHT_BRACES = "New workgroup>"

        private const val SPECIAL_CHARACTERS_CHAIN_CONTAIN_LEFT_BRACES = "<New workgroup"

        private const val SPECIAL_CHARACTERS_CHAIN_CONTAIN_COLON = "New: workgroup"

        private const val SPECIAL_CHARACTERS_CHAIN_CONTAIN_COMMA = "New,workgroup"

        private const val SPECIAL_CHARACTERS_CHAIN_CONTAIN_RIGHT_SLASH = "New/workgroup"

        private const val SPECIAL_CHARACTERS_CHAIN_CONTAIN_LEFT_SLASH = "New\\workgroup"

        private const val SPECIAL_CHARACTERS_CHAIN_CONTAIN_MIDDLE_SLASH = "New|workgroup"

        private const val SPECIAL_CHARACTERS_CHAIN_CONTAIN_QUESTION_MARK = "New workgroup?"

        private const val SPECIAL_CHARACTERS_CHAIN_CONTAIN_STAR = "New workgroup*"

        private const val SPECIAL_CHARACTERS_CHAIN_CONTAIN_QUOTATION_MARK = "New\"workgroup"
    }

    @Before
    fun setUp() {
        nameValidator = NameValidator()
    }

    @Test
    fun checkSpecialCharacterShouldReturnValidNameStateWithNormalChain() {
        assertThat(nameValidator.validateName(NORMAL_CHAIN))
            .isEqualTo(Either.right(ValidName(NORMAL_CHAIN)))
    }

    @Test
    fun checkSpecialCharacterShouldReturnNameContainSpecialCharacterStateWithChainHaveRightBracesCharacters() {
        assertThat(nameValidator.validateName(SPECIAL_CHARACTERS_CHAIN_CONTAIN_RIGHT_BRACES))
            .isEqualTo(Either.left(NameContainSpecialCharacter))
    }

    @Test
    fun checkSpecialCharacterShouldReturnNameContainSpecialCharacterStateWithChainHaveLeftBracesCharacters() {
        assertThat(nameValidator.validateName(SPECIAL_CHARACTERS_CHAIN_CONTAIN_LEFT_BRACES))
            .isEqualTo(Either.left(NameContainSpecialCharacter))
    }

    @Test
    fun checkSpecialCharacterShouldReturnNameContainSpecialCharacterStateWithChainHaveColonCharacters() {
        assertThat(nameValidator.validateName(SPECIAL_CHARACTERS_CHAIN_CONTAIN_COLON))
            .isEqualTo(Either.left(NameContainSpecialCharacter))
    }

    @Test
    fun checkSpecialCharacterShouldReturnNameContainSpecialCharacterStateWithChainHaveCommaCharacters() {
        assertThat(nameValidator.validateName(SPECIAL_CHARACTERS_CHAIN_CONTAIN_COMMA))
            .isEqualTo(Either.left(NameContainSpecialCharacter))
    }

    @Test
    fun checkSpecialCharacterShouldReturnNameContainSpecialCharacterStateWithChainHaveRightSlashCharacters() {
        assertThat(nameValidator.validateName(SPECIAL_CHARACTERS_CHAIN_CONTAIN_RIGHT_SLASH))
            .isEqualTo(Either.left(NameContainSpecialCharacter))
    }

    @Test
    fun checkSpecialCharacterShouldReturnNameContainSpecialCharacterStateWithChainHaveLeftSlashCharacters() {
        assertThat(nameValidator.validateName(SPECIAL_CHARACTERS_CHAIN_CONTAIN_LEFT_SLASH))
            .isEqualTo(Either.left(NameContainSpecialCharacter))
    }

    @Test
    fun checkSpecialCharacterShouldReturnNameContainSpecialCharacterStateWithChainHaveMidleSlashCharacters() {
        assertThat(nameValidator.validateName(SPECIAL_CHARACTERS_CHAIN_CONTAIN_MIDDLE_SLASH))
            .isEqualTo(Either.left(NameContainSpecialCharacter))
    }

    @Test
    fun checkSpecialCharacterShouldReturnNameContainSpecialCharacterStateWithChainHaveQuestionMarkCharacters() {
        assertThat(nameValidator.validateName(SPECIAL_CHARACTERS_CHAIN_CONTAIN_QUESTION_MARK))
            .isEqualTo(Either.left(NameContainSpecialCharacter))
    }

    @Test
    fun checkSpecialCharacterShouldReturnNameContainSpecialCharacterStateWithChainHaveStarCharacters() {
        assertThat(nameValidator.validateName(SPECIAL_CHARACTERS_CHAIN_CONTAIN_STAR))
            .isEqualTo(Either.left(NameContainSpecialCharacter))
    }

    @Test
    fun checkSpecialCharacterShouldReturnNameContainSpecialCharacterStateWithChainHaveQuotationMarkCharacters() {
        assertThat(nameValidator.validateName(SPECIAL_CHARACTERS_CHAIN_CONTAIN_QUOTATION_MARK))
            .isEqualTo(Either.left(NameContainSpecialCharacter))
    }

    @Test
    fun checkSpecialCharacterShouldReturnBlankNameErrorStateWithChainHaveQuotationMarkCharacters() {
        assertThat(nameValidator.validateName(BLANK_CHAIN)).isEqualTo(Either.left(BlankNameError))
    }
}
