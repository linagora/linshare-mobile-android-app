package com.linagora.android.linshare.view.authentication.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import androidx.appcompat.widget.Toolbar
import androidx.databinding.ObservableField
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import arrow.core.Either
import com.linagora.android.linshare.R
import com.linagora.android.linshare.databinding.LoginFragmentBinding
import com.linagora.android.linshare.domain.model.Credential
import com.linagora.android.linshare.domain.usecases.auth.AuthenticationFailure
import com.linagora.android.linshare.domain.usecases.auth.AuthenticationViewState
import com.linagora.android.linshare.domain.usecases.auth.BadCredentials
import com.linagora.android.linshare.domain.usecases.auth.ConnectError
import com.linagora.android.linshare.domain.usecases.auth.EmptyToken
import com.linagora.android.linshare.domain.usecases.auth.ServerNotFound
import com.linagora.android.linshare.domain.usecases.utils.Failure
import com.linagora.android.linshare.domain.usecases.utils.Success
import com.linagora.android.linshare.domain.usecases.utils.Success.Loading
import com.linagora.android.linshare.util.afterTextChanged
import com.linagora.android.linshare.util.getViewModel
import com.linagora.android.linshare.view.MainActivityViewModel
import com.linagora.android.linshare.view.MainNavigationFragment
import com.linagora.android.linshare.view.Navigation.LoginFlow.DIRECT
import com.linagora.android.linshare.view.authentication.login.LoginFormState.Companion
import kotlinx.android.synthetic.main.login_fragment.btnLogin
import kotlinx.android.synthetic.main.login_fragment.edtLoginPassword
import kotlinx.android.synthetic.main.login_fragment.edtLoginUrl
import kotlinx.android.synthetic.main.login_fragment.edtLoginUsername
import javax.inject.Inject

class LoginFragment : MainNavigationFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var loginViewModel: LoginViewModel

    private val mainActivityViewModel: MainActivityViewModel
            by activityViewModels { viewModelFactory }

    private lateinit var binding: LoginFragmentBinding

    private val args: LoginFragmentArgs by navArgs()

    private val loginFormState = ObservableField(LoginFormState.INIT_STATE)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = LoginFragmentBinding.inflate(inflater, container, false)
        binding.loginFormState = loginFormState
        initViewModel()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpView()
    }

    override fun configureToolbar(toolbar: Toolbar) {
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back)
    }

    private fun setUpView() {
        edtLoginUrl.setOnFocusChangeListener { loginUrl, hasFocus ->
            if (hasFocus) {
                with(loginUrl as EditText) {
                    if (text.isEmpty()) {
                        setText(R.string.https)
                    }
                    setSelection(text.length)
                }
            }
        }

        edtLoginUrl.afterTextChanged {
            loginFormState.set(LoginFormState.INIT_STATE)
        }

        edtLoginUsername.afterTextChanged {
            loginFormState.set(Companion.INIT_STATE)
        }

        edtLoginPassword.apply {
            afterTextChanged {
                loginFormState.set(Companion.INIT_STATE)
            }

            setOnEditorActionListener { _, actionId, _ ->
                when (actionId) {
                    EditorInfo.IME_ACTION_DONE -> authenticate()
                }
                false
            }

            btnLogin.setOnClickListener {
                authenticate()
            }
        }
    }

    private fun initViewModel() {
        loginViewModel = getViewModel(viewModelFactory)

        loginViewModel.viewState.observe(this, Observer {
            when (it) {
                is Either.Right -> reactToSuccess(it.b)
                is Either.Left -> reactToFailure(it.a)
            }
        })
    }

    private fun authenticate() {
        loginViewModel.authenticate(
            baseUrl = edtLoginUrl.text.toString(),
            username = edtLoginUsername.text.toString(),
            password = edtLoginPassword.text.toString()
        )
    }

    private fun reactToSuccess(success: Success) {
        when (success) {
            is Loading -> loginFormState.set(LoginFormState(isLoading = true))
            is LoginFormState -> loginFormState.set(success)
            is AuthenticationViewState -> {
                loginFormState.set(LoginFormState(isLoading = false))
                mainActivityViewModel.setUpAuthenticated(success)
                loginSuccess(success.credential)
            }
        }
    }

    private fun reactToFailure(failure: Failure) {
        when (failure) {
            is AuthenticationFailure -> {
                val errorType = when (failure.exception) {
                    is EmptyToken -> ErrorType.WRONG_CREDENTIAL
                    is BadCredentials -> ErrorType.WRONG_CREDENTIAL
                    is ServerNotFound, ConnectError -> ErrorType.WRONG_URL
                    else -> ErrorType.UNKNOWN_ERROR
                }
                loginFormState.set(
                    LoginFormState(
                        isLoading = false,
                        errorMessage = getErrorMessageByType(errorType),
                        errorType = errorType
                ))
            }
        }
    }

    private fun getErrorMessageByType(errorType: ErrorType): Int? {
        return when (errorType) {
            ErrorType.WRONG_CREDENTIAL -> R.string.credential_error_message
            ErrorType.WRONG_URL -> R.string.server_not_found
            ErrorType.UNKNOWN_ERROR -> R.string.unknow_error
            else -> null
        }
    }

    private fun loginSuccess(credentials: Credential) {
        when (args.loginFlow) {
            DIRECT -> { findNavController().navigate(R.id.navigation_my_space) }
            else -> findNavController().popBackStack(R.id.uploadFragment, false)
        }
    }
}
