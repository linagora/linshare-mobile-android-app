package com.linagora.android.linshare.util

import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders

inline fun <reified VM : ViewModel> FragmentActivity.getViewModel(provider: ViewModelProvider.Factory)
        = ViewModelProviders.of(this, provider).get(VM::class.java)