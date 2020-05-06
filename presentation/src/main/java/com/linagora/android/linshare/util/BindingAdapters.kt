package com.linagora.android.linshare.util

import android.text.format.Formatter
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.databinding.BindingAdapter
import arrow.core.Either
import arrow.core.orNull
import com.auth0.android.jwt.JWT
import com.linagora.android.linshare.R
import com.linagora.android.linshare.domain.model.GenericUser
import com.linagora.android.linshare.domain.model.document.DocumentRequest
import com.linagora.android.linshare.domain.usecases.account.AccountDetailsViewState
import com.linagora.android.linshare.domain.usecases.quota.ExceedMaxFileSize
import com.linagora.android.linshare.domain.usecases.quota.ExtractInfoFailed
import com.linagora.android.linshare.domain.usecases.quota.PreUploadExecuting
import com.linagora.android.linshare.domain.usecases.quota.QuotaAccountNoMoreSpaceAvailable
import com.linagora.android.linshare.domain.usecases.sharedspace.EmptySharedSpaceState
import com.linagora.android.linshare.domain.usecases.utils.Failure
import com.linagora.android.linshare.domain.usecases.utils.Success
import com.linagora.android.linshare.glide.GlideApp
import com.linagora.android.linshare.util.FileSize.SizeFormat.SHORT
import com.linagora.android.linshare.util.TimeUtils.LinShareTimeFormat.LastLoginFormat
import com.linagora.android.linshare.view.authentication.login.ErrorType
import com.linagora.android.linshare.view.authentication.login.LoginFormState
import org.slf4j.LoggerFactory
import timber.log.Timber

private val LOGGER = LoggerFactory.getLogger(BindingAdapter::class.java)

@BindingAdapter("guide")
fun bindLoginGuide(textView: TextView, loginFormState: LoginFormState) {
    try {
        require(!loginFormState.isLoading)
        loginFormState.errorMessage
            ?.let {
                textView.apply {
                    setText(it)
                    setTextColor(ContextCompat.getColor(textView.context, R.color.error_border_color))
                }
            }
            ?: textView.apply {
                setText(R.string.please_enter_credential)
                setTextColor(ContextCompat.getColor(textView.context, R.color.text_with_logo_color))
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

@BindingAdapter("uploadInfo", "uploadErrorStateInfo")
fun bindingUploadInfo(textView: TextView, document: DocumentRequest?, uploadErrorState: Either<Failure, Success>) {
    textView.text = uploadErrorState.map { success ->
        when (success) {
            PreUploadExecuting -> textView.context.resources.getString(R.string.executing)
            else -> document?.uploadFileName
        }
    }.orNull()
}

@BindingAdapter("documentIcon", "uploadErrorStateIcon")
fun bindingUploadIcon(imageView: AppCompatImageView, document: DocumentRequest?, uploadErrorState: Either<Failure, Success>) {
    GlideApp.with(imageView.context)
        .load(document?.file)
        .placeholder(
            document?.mediaType?.getDrawableIcon()
                ?: uploadErrorState.fold(
                    ifLeft = { R.drawable.ic_warning },
                    ifRight = { android.R.drawable.screen_background_light_transparent })
        )
        .into(imageView)
}

@BindingAdapter("uploadErrorStateProgress")
fun bindingUploadProgressIcon(imageView: AppCompatImageView, uploadErrorState: Either<Failure, Success>) {
    uploadErrorState.fold(
        ifLeft = { imageView.stopAnimationDrawable() },
        ifRight = { success ->
            success.takeIf { success is PreUploadExecuting }
                ?.let { imageView.startAnimationDrawable() }
                ?: imageView.stopAnimationDrawable() }
    )
}

@BindingAdapter("uploadErrorMessage")
fun bindingUploadError(textView: TextView, uploadErrorState: Either<Failure, Success>) {
    LOGGER.info("uploadErrorMessage() $uploadErrorState")
    textView.visibility = View.GONE
    uploadErrorState.mapLeft { failure -> failure.getUploadErrorMessageId() }
        .mapLeft {
            textView.setText(it)
            textView.visibility = View.VISIBLE
        }
}

private fun Failure.getUploadErrorMessageId(): Int {
    return when (this) {
            QuotaAccountNoMoreSpaceAvailable -> { R.string.no_more_space_avalable }
            ExceedMaxFileSize -> { R.string.exceed_max_file_size }
            ExtractInfoFailed -> { R.string.extrac_info_failed }
            else -> { R.string.unable_to_prepare_file_for_upload }
        }
}

@BindingAdapter("uploadState")
fun bindingUploadButton(button: Button, uploadState: Either<Failure, Success>) {
    uploadState.fold(
        ifLeft = { disableButtonUpload(button) },
        ifRight = { success -> bindingUploadButtonWhenSuccess(success, button) }
    )
}

@BindingAdapter("shareRecipients")
fun bindingUploadButtonText(button: Button, recipients: Set<GenericUser>) {
    recipients.takeIf { it.isNotEmpty() }
        ?.run { button.setText(R.string.upload_and_share) }
        ?: button.setText(R.string.upload_to_my_space)
}

private fun bindingUploadButtonWhenSuccess(success: Success, button: Button) {
    when (success) {
        PreUploadExecuting -> disableButtonPreUploadExecuting(button)
        else -> enableButtonUpload(button)
    }
}

private fun disableButtonPreUploadExecuting(button: Button) {
    button.isEnabled = false
    button.setTextColor(ContextCompat.getColor(button.context, R.color.disable_state_color))
    button.setBackgroundResource(R.drawable.round_with_border_loading_button_layout)
}

private fun enableButtonUpload(button: Button) {
    button.isEnabled = true
    button.setTextColor(ContextCompat.getColor(button.context, R.color.white))
    button.setBackgroundResource(R.drawable.round_button_primary_solid)
}

private fun disableButtonUpload(button: Button) {
    button.isEnabled = false
    button.setTextColor(ContextCompat.getColor(button.context, R.color.white))
    button.setBackgroundResource(R.drawable.round_with_border_disable_button_layout)
}

@BindingAdapter("visibleEmptyMessage")
fun bindingEmptyMessage(textView: TextView, sharedSpace: Either<Failure, Success>?) {
    val visible = sharedSpace?.fold(
        ifLeft = { false },
        ifRight = { it is EmptySharedSpaceState })
    textView.isVisible = visible ?: false
}
