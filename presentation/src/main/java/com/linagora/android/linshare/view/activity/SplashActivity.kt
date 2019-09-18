package com.linagora.android.linshare.view.activity

import android.os.Bundle

import androidx.appcompat.app.AppCompatActivity

import com.linagora.android.linshare.R

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_splash)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }
}
