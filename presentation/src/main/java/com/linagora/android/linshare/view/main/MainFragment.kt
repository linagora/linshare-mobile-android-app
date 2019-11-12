package com.linagora.android.linshare.view.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.linagora.android.linshare.databinding.FragmentMainBinding
import com.linagora.android.linshare.domain.usecases.auth.AuthenticationViewState
import com.linagora.android.linshare.model.mapper.toParcelable
import com.linagora.android.linshare.util.getViewModel
import com.linagora.android.linshare.view.MainNavigationFragment
import javax.inject.Inject

class MainFragment : MainNavigationFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var viewModel: MainFragmentViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initViewModel()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    private fun initViewModel() {
        viewModel = getViewModel(viewModelFactory)
        viewModel.viewState.observe(this, Observer { state ->
            state.fold(
                ifLeft = { gotoLoginPage() },
                ifRight = { success -> success
                    .takeIf { it is AuthenticationViewState }
                    ?.let { jumpIn(it as AuthenticationViewState) }
                }
            )
        })
        viewModel.checkSignedIn()
    }

    private fun gotoLoginPage() {
        val action = MainFragmentDirections.actionMainFragmentToWizardFragment()
        findNavController().navigate(action)
    }

    private fun jumpIn(authenticationViewState: AuthenticationViewState) {
        viewModel.setUpInterceptors(authenticationViewState)
        val action = MainFragmentDirections
            .actionMainFragmentToAccountDetailsFragment(
                credential = authenticationViewState.credential.toParcelable()
            )
        findNavController().navigate(action)
    }
}
