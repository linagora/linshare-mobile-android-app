/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 *
 * Copyright (C) 2020 LINAGORA
 *
 * This program is free software: you can redistribute it and/or modify it under the
 * terms of the GNU Affero General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later version,
 * provided you comply with the Additional Terms applicable for LinShare software by
 * Linagora pursuant to Section 7 of the GNU Affero General Public License,
 * subsections (b), (c), and (e), pursuant to which you must notably (i) retain the
 * display in the interface of the “LinShare™” trademark/logo, the "Libre & Free" mention,
 * the words “You are using the Free and Open Source version of LinShare™, powered by
 * Linagora © 2009–2020. Contribute to Linshare R&D by subscribing to an Enterprise
 * offer!”. You must also retain the latter notice in all asynchronous messages such as
 * e-mails sent with the Program, (ii) retain all hypertext links between LinShare and
 * http://www.linshare.org, between linagora.com and Linagora, and (iii) refrain from
 * infringing Linagora intellectual property rights over its trademarks and commercial
 * brands. Other Additional Terms apply, see
 * <http://www.linshare.org/licenses/LinShare-License_AfferoGPL-v3.pdf>
 * for more details.
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for
 * more details.
 * You should have received a copy of the GNU Affero General Public License and its
 * applicable Additional Terms for LinShare along with this program. If not, see
 * <http://www.gnu.org/licenses/> for the GNU Affero General Public License version
 *  3 and <http://www.linshare.org/licenses/LinShare-License_AfferoGPL-v3.pdf> for
 *  the Additional Terms applicable to LinShare software.
 */

package com.linagora.android.linshare.view.accounts

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import arrow.core.Either
import com.linagora.android.linshare.R
import com.linagora.android.linshare.databinding.FragmentAccountDetailBinding
import com.linagora.android.linshare.domain.usecases.account.AccountDetailsViewState
import com.linagora.android.linshare.domain.usecases.auth.AuthenticationViewState
import com.linagora.android.linshare.domain.usecases.auth.SuccessRemoveAccount
import com.linagora.android.linshare.domain.usecases.utils.Success
import com.linagora.android.linshare.model.mapper.toCredential
import com.linagora.android.linshare.util.getViewModel
import com.linagora.android.linshare.view.MainActivityViewModel
import com.linagora.android.linshare.view.MainActivityViewModel.AuthenticationState.UNAUTHENTICATED
import com.linagora.android.linshare.view.MainNavigationFragment
import com.linagora.android.linshare.view.dialog.ConfirmRemoveAccountDialog
import kotlinx.android.synthetic.main.fragment_account_detail.imgBtnRemoveAcc
import org.slf4j.LoggerFactory
import javax.inject.Inject

class AccountDetailsFragment : MainNavigationFragment() {

    companion object {
        private val LOGGER = LoggerFactory.getLogger(AccountDetailsFragment::class.java)
    }

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var accountDetailViewModel: AccountDetailsViewModel

    private val mainActivityViewModel: MainActivityViewModel by activityViewModels { viewModelFactory }

    private val detailsViewState = MutableLiveData(AccountDetailsViewState.INIT_STATE)

    private val argument: AccountDetailsFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        LOGGER.info("onCreateView()")
        val binding = FragmentAccountDetailBinding.inflate(inflater, container, false)
        binding.details = detailsViewState
        binding.lifecycleOwner = this
        initViewModel()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        LOGGER.info("onViewCreated()")
        super.onViewCreated(view, savedInstanceState)
        initView(view)
    }

    override fun configureToolbar(toolbar: Toolbar) {
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back)
    }

    private fun initView(rootView: View) {
        imgBtnRemoveAcc.setOnClickListener {
            with(rootView.context) {
                ConfirmRemoveAccountDialog(
                    title = getString(R.string.confirm_remove_account_title),
                    negativeText = getString(R.string.cancel),
                    positiveText = getString(R.string.logout),
                    onPositiveCallback = {
                        accountDetailViewModel.removeAccount(argument.credential.toCredential())
                    }
                ).show(childFragmentManager, "confirm_remove_account_dialog")
            }
        }
    }

    private fun initViewModel() {
        LOGGER.info("initViewModel()")
        accountDetailViewModel = getViewModel(viewModelFactory)

        accountDetailViewModel.viewState.observe(viewLifecycleOwner, Observer {
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
            is AccountDetailsViewState -> {
                detailsViewState.value = success
            }
            is SuccessRemoveAccount -> {
                accountDetailViewModel.resetInterceptors()
                mainActivityViewModel.authenticationState.value = UNAUTHENTICATED
                gotoLoginScreen()
            }
        }
    }

    private fun gotoLoginScreen() {
        findNavController().navigate(R.id.mainFragment)
    }
}
