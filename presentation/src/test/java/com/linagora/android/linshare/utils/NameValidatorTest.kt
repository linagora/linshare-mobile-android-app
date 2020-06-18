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
