package com.linagora.android.linshare.view.splash

import android.content.Intent
import android.os.Bundle
import com.linagora.android.linshare.R
import com.linagora.android.linshare.util.getViewModel
import com.linagora.android.linshare.view.MainActivity
import com.linagora.android.linshare.view.base.BaseActivity

class SplashActivity : BaseActivity() {

    private lateinit var viewModel: SplashActivityViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = getViewModel(viewModelFactory)

        setContentView(R.layout.activity_splash)

        startActivity(Intent(this@SplashActivity, MainActivity::class.java))
        finish()
    }
}
