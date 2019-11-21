package com.linagora.android.linshare.view.authentication.wizard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.activity.addCallback
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.viewpager2.widget.ViewPager2
import com.linagora.android.linshare.R
import com.linagora.android.linshare.view.MainNavigationFragment
import com.linagora.android.linshare.view.widget.PageIndicator

class WizardFragment : MainNavigationFragment() {

    private lateinit var viewPagerAdapter: WizardPagerAdapter

    private val args: WizardFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.wizard_fragment, container, false)
        initView(view)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpOnBackPressed()
    }

    private fun initView(root: View) {
        val viewPager = root.findViewById<ViewPager2>(R.id.wizardViewPager)
        val indicator = root.findViewById<PageIndicator>(R.id.wizardIndicator)
        val loginButton = root.findViewById<Button>(R.id.wizardLoginBtn)

        viewPagerAdapter = WizardPagerAdapter(context!!)
        viewPager.adapter = viewPagerAdapter
        indicator.setViewPager(viewPager)

        loginButton.setOnClickListener {
            val action = WizardFragmentDirections.toLoginFragment(args.loginFlow)
            findNavController().navigate(action)
        }
    }

    private fun setUpOnBackPressed() {
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            requireActivity().finish()
        }
    }
}
