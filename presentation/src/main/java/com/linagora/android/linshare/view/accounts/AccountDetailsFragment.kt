package com.linagora.android.linshare.view.accounts

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.linagora.android.linshare.databinding.FragmentAccountDetailBinding
import com.linagora.android.linshare.view.MainNavigationFragment

class AccountDetailsFragment : MainNavigationFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentAccountDetailBinding.inflate(inflater, container, false)
        return binding.root
    }
}
