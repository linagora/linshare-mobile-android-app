package com.linagora.android.linshare.view.authentication

import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.Observer
import arrow.core.Either
import com.linagora.android.linshare.R
import com.linagora.android.linshare.domain.model.Password
import com.linagora.android.linshare.domain.model.Username
import com.linagora.android.linshare.domain.usecases.auth.AuthenticationViewState
import com.linagora.android.linshare.domain.usecases.utils.Failure
import com.linagora.android.linshare.domain.usecases.utils.Success
import com.linagora.android.linshare.util.getViewModel
import com.linagora.android.linshare.view.base.BaseActivity
import kotlinx.android.synthetic.main.activity_login.login
import java.net.URL

class LoginActivity : BaseActivity() {

    private lateinit var viewModel: LoginActivityViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = getViewModel(viewModelFactory)

        setContentView(R.layout.activity_login)

        observeAuthenticateState()
        initView()
    }

    private fun observeAuthenticateState() {
        viewModel.viewState.observe(this, Observer {
            when (it) {
                is Either.Right -> { reactToSuccess(it.b) }
                is Either.Left -> { reactToFailure(it.a) }
            }
        })
    }

    private fun initView() {
        login.setOnClickListener { authenticateUser() }
    }

    private fun authenticateUser() {
        viewModel.authenticate(
            baseUrl = URL("http://172.16.47.30:28080"),
            username = Username("user1@linshare.org"),
            password = Password("password1")
        )
    }

    private fun reactToSuccess(success: Success) {
        when (success) {
            is AuthenticationViewState -> Toast.makeText(this, "${success.token}", Toast.LENGTH_LONG).show()
        }
    }

    private fun reactToFailure(failure: Failure) {
        Toast.makeText(this, "Failed", Toast.LENGTH_LONG).show()
    }
}
