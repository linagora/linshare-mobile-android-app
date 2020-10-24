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

package com.linagora.android.linshare.view.authentication.login

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatDialog
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import arrow.core.Either
import com.linagora.android.linshare.R
import com.linagora.android.linshare.databinding.DialogSecondFactorAuthBinding
import com.linagora.android.linshare.domain.model.Password
import com.linagora.android.linshare.domain.model.Username
import com.linagora.android.linshare.domain.model.secondfa.SecondFactorAuthCode
import com.linagora.android.linshare.domain.network.SupportVersion
import com.linagora.android.linshare.util.binding.initView
import com.linagora.android.linshare.util.binding.onSecondFActorAuthChange
import com.linagora.android.linshare.util.dismissKeyboard
import com.linagora.android.linshare.util.getParentViewModel
import com.linagora.android.linshare.util.showKeyboard
import dagger.android.support.DaggerAppCompatDialogFragment
import java.net.URL
import javax.inject.Inject

class SecondFactorAuthDialog(
    private val baseUrl: URL,
    private val supportVersion: SupportVersion,
    private val username: Username,
    private val password: Password
) : DaggerAppCompatDialogFragment() {
    companion object {
        const val TAG = "secondFactorAuthDialog"
    }

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val loginViewModel: LoginViewModel
        by lazy { getParentViewModel<LoginViewModel>(viewModelFactory) }

    private lateinit var binding: DialogSecondFactorAuthBinding

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        inflateView()
        initViewModel()
        val dialog = AppCompatDialog(context!!, android.R.style.Theme_Light)
        dialog.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(binding.root)
        return dialog
    }

    private fun initViewModel() {
        binding.viewModel = loginViewModel
    }

    private fun inflateView() {
        binding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.dialog_second_factor_auth, null, false)
        with(binding) {
            inputSecondFAContainer.initView()
            inputSecondFAContainer.onSecondFActorAuthChange(this@SecondFactorAuthDialog::onCodeChange)
            inputSecondFAContainer.codeZero.requestFocus()
            inputSecondFAContainer.codeZero.setOnFocusChangeListener { v, hasFocus ->
                if (hasFocus) {
                    v.showKeyboard()
                }
            }

            toolbar.setNavigationOnClickListener {
                loginViewModel.dispatchUIState(Either.Right(CloseSecondFactorAuthScreen))
                dismiss()
            }
        }
    }



    override fun onPause() {
        binding.root.dismissKeyboard()
        super.onPause()
    }

    private fun onCodeChange(secondFactorAuthCode: SecondFactorAuthCode?) {
        val state = secondFactorAuthCode?.let { SecondFactorAuthState.Completed }
            ?: SecondFactorAuthState.NotCompleted

        loginViewModel.dispatchUIState(
            Either.Right(SecondFactorAuthChangeViewState(state)))
    }
}
