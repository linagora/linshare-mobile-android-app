package com.linagora.android.linshare.view

import android.os.Bundle

import com.linagora.android.linshare.R
import com.linagora.android.linshare.util.getViewModel
import com.linagora.android.linshare.view.base.BaseActivity

class MainActivity : BaseActivity() {

    private lateinit var viewModel: MainActivityViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = getViewModel(viewModelFactory)

        setContentView(R.layout.activity_main)
    }
}
