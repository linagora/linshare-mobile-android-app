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

package com.linagora.android.linshare.view.authentication.wizard

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.linagora.android.linshare.R
import com.linagora.android.linshare.databinding.WizardPageLayoutBinding
import com.linagora.android.linshare.view.authentication.wizard.WizardPagerAdapter.WizardViewHolder
import java.security.InvalidParameterException

class WizardPagerAdapter(val context: Context) : RecyclerView.Adapter<WizardViewHolder>() {

    companion object {
        const val WIZARD_PAGE_TYPE = 0

        val PAGE_TYPES = listOf(WIZARD_PAGE_TYPE)

        private val WIZARD_PAGE_1 = WizardPage(
            R.drawable.ic_group,
            R.string.wizard_intro_one)

        private val WIZARD_PAGE_2 = WizardPage(
            R.drawable.ic_group,
            R.string.wizard_intro_two)

        private val WIZARD_PAGE_3 = WizardPage(
            R.drawable.ic_group,
            R.string.wizard_intro_three)

        val PAGES = listOf(WIZARD_PAGE_1, WIZARD_PAGE_2, WIZARD_PAGE_3)
    }

    private var wizardPageLayoutBinding: WizardPageLayoutBinding? = null

    override fun getItemViewType(position: Int): Int {
        return WIZARD_PAGE_TYPE
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WizardViewHolder {
        return WizardViewHolder(
            when (viewType) {
                WIZARD_PAGE_TYPE -> createWizard(parent)
                else -> throw InvalidParameterException()
            }
        )
    }

    override fun getItemCount(): Int = PAGES.size

    override fun onBindViewHolder(holder: WizardViewHolder, position: Int) {
        val page = PAGES[position]
        holder.binding.txtPageIntro.text = context.getString(page.pageIntroRes)
    }

    private fun createWizard(parent: ViewGroup): WizardPageLayoutBinding {
        return WizardPageLayoutBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
    }

    class WizardViewHolder(val binding: WizardPageLayoutBinding) : RecyclerView.ViewHolder(binding.root)
}
