package com.linagora.android.linshare.view.sharedspace.details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.linagora.android.linshare.databinding.FragmentAddMemberBinding
import com.linagora.android.linshare.view.MainNavigationFragment

class SharedSpaceAddMemberFragment : MainNavigationFragment() {

    private lateinit var binding: FragmentAddMemberBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAddMemberBinding.inflate(inflater, container, false)
        return binding.root
    }
}
