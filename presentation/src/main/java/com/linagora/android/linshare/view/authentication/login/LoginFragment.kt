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
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import arrow.core.Either
import com.linagora.android.linshare.R
import com.linagora.android.linshare.databinding.LoginFragmentBinding
import com.linagora.android.linshare.domain.model.Credential
import com.linagora.android.linshare.domain.network.SupportVersion
import com.linagora.android.linshare.domain.usecases.auth.AuthenticationException
import com.linagora.android.linshare.domain.usecases.auth.AuthenticationFailure
import com.linagora.android.linshare.domain.usecases.auth.AuthenticationViewState
import com.linagora.android.linshare.domain.usecases.auth.BadCredentials
import com.linagora.android.linshare.domain.usecases.auth.ConnectError
import com.linagora.android.linshare.domain.usecases.auth.EmptyToken
import com.linagora.android.linshare.domain.usecases.auth.ServerNotFoundException
import com.linagora.android.linshare.domain.usecases.auth.ServerNotFoundFailure
import com.linagora.android.linshare.domain.usecases.utils.Failure
import com.linagora.android.linshare.domain.usecases.utils.Success
import com.linagora.android.linshare.domain.usecases.utils.Success.Loading
import com.linagora.android.linshare.util.afterTextChanged
import com.linagora.android.linshare.util.getViewModel
import com.linagora.android.linshare.view.MainActivityViewModel
import com.linagora.android.linshare.view.MainNavigationFragment
import com.linagora.android.linshare.view.Navigation.LoginFlow.DIRECT
import kotlinx.android.synthetic.main.login_fragment.btnLogin
import kotlinx.android.synthetic.main.login_fragment.edtLoginPassword
import kotlinx.android.synthetic.main.login_fragment.edtLoginUrl
import kotlinx.android.synthetic.main.login_fragment.edtLoginUsername

class LoginFragment : MainNavigationFragment() {

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
            loginFormState.set(LoginFormState.INIT_STATE)
        }

        edtLoginPassword.apply {
            afterTextChanged {
                loginFormState.set(LoginFormState.INIT_STATE)
            }

            setOnEditorActionListener { _, actionId, _ ->
                when (actionId) {
                    EditorInfo.IME_ACTION_DONE -> authenticate(SupportVersion.Version4)
                }
                false
            }

            btnLogin.setOnClickListener { authenticate(SupportVersion.Version4) }
        }
    }

    private fun initViewModel() {
        loginViewModel = getViewModel(viewModelFactory)

        loginViewModel.viewState.observe(viewLifecycleOwner, Observer {
            when (it) {
                is Either.Right -> reactToSuccess(it.b)
                is Either.Left -> reactToFailure(it.a)
            }
        })
    }

    private fun authenticate(version: SupportVersion) {
        loginViewModel.authenticate(
            baseUrl = edtLoginUrl.text.toString(),
            supportVersion = version,
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
            is ServerNotFoundFailure -> handleServerNotFound(failure)
            is AuthenticationFailure -> handleAuthenticationException(failure.exception)
        }
    }

    private fun handleServerNotFound(serverNotFoundFailure: ServerNotFoundFailure) {
        when (serverNotFoundFailure.exception.supportVersion) {
            SupportVersion.Version4 -> authenticate(SupportVersion.Version2)
            else -> handleAuthenticationException(serverNotFoundFailure.exception)
        }
    }

    private fun handleAuthenticationException(authenticationException: AuthenticationException) {
        val errorType = when (authenticationException) {
            is EmptyToken -> ErrorType.WRONG_CREDENTIAL
            is BadCredentials -> ErrorType.WRONG_CREDENTIAL
            is ServerNotFoundException, ConnectError -> ErrorType.WRONG_URL
            else -> ErrorType.UNKNOWN_ERROR
        }
        loginFormState.set(
            LoginFormState(
                isLoading = false,
                errorMessage = getErrorMessageByType(errorType),
                errorType = errorType
            ))
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
