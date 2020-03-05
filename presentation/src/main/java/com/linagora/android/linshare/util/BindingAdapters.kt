package com.linagora.android.linshare.util

import android.text.format.Formatter
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.databinding.BindingAdapter
import arrow.core.Either
import com.auth0.android.jwt.JWT
import com.linagora.android.linshare.R
import com.linagora.android.linshare.domain.model.document.DocumentRequest
import com.linagora.android.linshare.domain.usecases.account.AccountDetailsViewState
import com.linagora.android.linshare.domain.usecases.quota.ExceedMaxFileSize
import com.linagora.android.linshare.domain.usecases.quota.ExtractInfoFailed
import com.linagora.android.linshare.domain.usecases.quota.QuotaAccountNoMoreSpaceAvailable
import com.linagora.android.linshare.domain.usecases.utils.Failure
import com.linagora.android.linshare.domain.usecases.utils.Success
import com.linagora.android.linshare.glide.GlideApp
import com.linagora.android.linshare.util.FileSize.SizeFormat.SHORT
import com.linagora.android.linshare.util.TimeUtils.LinShareTimeFormat.LastLoginFormat
import com.linagora.android.linshare.view.authentication.login.ErrorType
import com.linagora.android.linshare.view.authentication.login.LoginFormState
import timber.log.Timber

@BindingAdapter("guide")
fun bindLoginGuide(textView: TextView, loginFormState: LoginFormState) {
    try {
        require(!loginFormState.isLoading)
        loginFormState.errorMessage
            ?.let {
                textView.apply {
                    setText(it)
                    setTextColor(resources.getColor(R.color.error_border_color))
                }
            }
            ?: textView.apply {
                setText(R.string.please_enter_credential)
                setTextColor(resources.getColor(R.color.text_with_logo_color))
            }
    } catch (exp: Exception) {
        Timber.w("bindLoginGuide() ignore this exception: ${exp.message}")
    }
}

@BindingAdapter("inputError")
fun bindingInputError(editText: EditText, loginFormState: LoginFormState) {
    try {
        require(!loginFormState.isLoading)
        val background = when (loginFormState.errorType) {
            ErrorType.WRONG_CREDENTIAL -> {
                editText.id.takeIf { it != R.id.edtLoginUrl }
                    ?.let { R.drawable.round_error_layout }
                    ?: R.drawable.round_layout
            }
            ErrorType.WRONG_URL -> {
                editText.id.takeIf { it == R.id.edtLoginUrl }
                    ?.let { R.drawable.round_error_layout }
                    ?: R.drawable.round_layout
            }
            ErrorType.WRONG_EMAIL -> {
                editText.id.takeIf { it == R.id.edtLoginUsername }
                    ?.let { R.drawable.round_error_layout }
                    ?: R.drawable.round_layout
            }
            ErrorType.UNKNOWN_ERROR -> R.drawable.round_error_layout
            else -> R.drawable.round_layout
        }

        editText.setBackgroundResource(background)
    } catch (exp: Exception) {
        Timber.w("bindInputError() ignore this exception: ${exp.message}")
    }
}

@BindingAdapter("android:text")
fun bindingDomainName(textView: TextView, accountDetailsViewState: AccountDetailsViewState) {
    textView.text = accountDetailsViewState.credential
        ?.serverUrl
        ?.authority
}

@BindingAdapter("lastName")
fun bindingLastName(textView: TextView, accountDetailsViewState: AccountDetailsViewState) {
    textView.text = accountDetailsViewState.user
        ?.lastName
}

@BindingAdapter("firstName")
fun bindingFirstName(textView: TextView, accountDetailsViewState: AccountDetailsViewState) {
    textView.text = accountDetailsViewState.user
        ?.firstName
}

@BindingAdapter("subject")
fun bindingSubjectFromDecodedToken(textView: TextView, accountDetailsViewState: AccountDetailsViewState) {
    textView.text = runCatching { JWT(accountDetailsViewState.token!!.token).subject }.getOrNull()
}

@BindingAdapter("lastLogin")
fun bindingLastLogin(textView: TextView, accountDetailsViewState: AccountDetailsViewState) {
    textView.text = runCatching {
        TimeUtils.convertToLocalTime(accountDetailsViewState.lastLogin!!.date, LastLoginFormat)
    }.getOrNull()
}

@BindingAdapter("availableSpace")
fun bindingAvailabeSpace(textView: TextView, accountDetailsViewState: AccountDetailsViewState) {
    textView.text = runCatching {
        val accountQuota = accountDetailsViewState.quota!!
        val quotaSize = FileSize(accountQuota.quota.size)
            .format(SHORT)
        val availableSize = FileSize(accountQuota.quota - accountQuota.usedSpace)
            .format(SHORT)
        String.format(textView.context.getString(R.string.available_space), availableSize, quotaSize)
    }.getOrNull()
}

@BindingAdapter("uploadSize")
fun bindingFileSize(textView: TextView, document: DocumentRequest?) {
    textView.text = runCatching {
        Formatter.formatFileSize(textView.context, document!!.file.length())
    }.getOrNull()
}

@BindingAdapter("uploadInfo")
fun bindingUploadInfo(textView: TextView, document: DocumentRequest?) {
    textView.text = document?.uploadFileName
}

@BindingAdapter("uploadIcon")
fun bindingUploadIcon(imageView: AppCompatImageView, document: DocumentRequest?) {
    GlideApp.with(imageView.context)
        .load(document?.file)
        .placeholder(document?.mediaType?.getDrawableIcon()
            ?: R.drawable.ic_warning)
        .into(imageView)
}

@BindingAdapter("uploadErrorMessage")
fun bindingUploadError(textView: TextView, uploadErrorState: Either<Failure, Success>) {
    uploadErrorState.fold(
        ifLeft = { failure ->
            when (failure) {
                QuotaAccountNoMoreSpaceAvailable -> { R.string.no_more_space_avalable }
                ExceedMaxFileSize -> { R.string.exceed_max_file_size }
                ExtractInfoFailed -> { R.string.extrac_info_failed }
                else -> null
            }
        },
        ifRight = { null }
    )
    ?.let {
        textView.setText(it)
        textView.visibility = View.VISIBLE
    } ?: textView.setVisibility(View.GONE)
}

@BindingAdapter("uploadError")
fun bindingUploadButton(button: Button, uploadErrorState: Either<Failure, Success>) {
    uploadErrorState.fold(
        ifLeft = { failure ->
            when (failure) {
                QuotaAccountNoMoreSpaceAvailable, ExceedMaxFileSize, ExtractInfoFailed
                -> {
                    button.isEnabled = false
                    button.setTextColor(button.context.resources.getColor(R.color.white))
                }
                else -> button.isEnabled = true
            }
        },
        ifRight = { button.isEnabled = true }
    )
}
