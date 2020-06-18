package com.linagora.android.linshare.util

import arrow.core.Either
import com.linagora.android.linshare.domain.usecases.sharedspace.BlankNameError
import com.linagora.android.linshare.domain.usecases.sharedspace.NameContainSpecialCharacter
import com.linagora.android.linshare.domain.usecases.sharedspace.ValidName
import com.linagora.android.linshare.domain.usecases.utils.Failure
import com.linagora.android.linshare.domain.usecases.utils.Success
import java.util.regex.Pattern
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NameValidator @Inject constructor() {

    companion object {
        private val unAcceptLabelCharacter = Pattern.compile("[\"<>:,/\\\\|?*]")
    }

    fun validateName(name: String): Either<Failure, Success> {
        return name.takeIf { it.isBlank() }
            ?.let { (Either.left(BlankNameError)) }
            ?: run { checkSpecialChar(name) }
    }

    private fun checkSpecialChar(name: String): Either<Failure, Success> {
        return name.takeIf(this@NameValidator::containsSpecialCharacter)
            ?.let { Either.left(NameContainSpecialCharacter) }
            ?: Either.right(ValidName(name))
    }

    private fun containsSpecialCharacter(name: String): Boolean {
        return unAcceptLabelCharacter.matcher(name).find()
    }
}
