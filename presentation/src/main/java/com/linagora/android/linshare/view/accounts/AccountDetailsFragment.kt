package com.linagora.android.linshare.view.accounts

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import arrow.core.Either
import com.linagora.android.linshare.databinding.FragmentAccountDetailBinding
import com.linagora.android.linshare.domain.usecases.account.AccountDetailsViewState
import com.linagora.android.linshare.domain.usecases.auth.AuthenticationViewState
import com.linagora.android.linshare.domain.usecases.utils.Success
import com.linagora.android.linshare.model.mapper.toCredential
import com.linagora.android.linshare.util.getViewModel
import com.linagora.android.linshare.view.MainNavigationFragment
import javax.inject.Inject

class AccountDetailsFragment : MainNavigationFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var accountDetailViewModel: AccountDetailsViewModel

    private val detailsViewState = MutableLiveData(AccountDetailsViewState.INIT_STATE)

    private val argument: AccountDetailsFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentAccountDetailBinding.inflate(inflater, container, false)
        binding.details = detailsViewState
        binding.lifecycleOwner = this
        initViewModel()
        return binding.root
    }

    private fun initViewModel() {
        accountDetailViewModel = getViewModel(viewModelFactory)

        accountDetailViewModel.viewState.observe(this, Observer {
            when (it) {
                is Either.Right -> reactToSuccess(it.b)
                is Either.Left -> return@Observer
            }
        })

        accountDetailViewModel.retrieveAccountDetails(argument.credential.toCredential())
    }

    private fun reactToSuccess(success: Success) {
        when (success) {
            is AuthenticationViewState -> {
                detailsViewState.value = AccountDetailsViewState(
                    credential = success.credential,
                    token = success.token
                )
            }
        }
    }
}
