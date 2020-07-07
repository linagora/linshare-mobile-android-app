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

package com.linagora.android.linshare.view.sharedspace.details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import com.linagora.android.linshare.R
import com.linagora.android.linshare.databinding.FragmentSharedSpaceDetailsBinding
import com.linagora.android.linshare.model.parcelable.SharedSpaceIdParcelable
import com.linagora.android.linshare.model.parcelable.toSharedSpaceId
import com.linagora.android.linshare.util.getViewModel
import com.linagora.android.linshare.view.MainNavigationFragment
import javax.inject.Inject

class SharedSpaceDetailsFragment : MainNavigationFragment() {

    companion object {
        private val DETAILS_TITLES = arrayOf(
            R.string.members
        )
    }

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var viewModel: SharedSpaceDetailsViewModel

    private lateinit var binding: FragmentSharedSpaceDetailsBinding

    private val sharedSpaceDetailsArgs: SharedSpaceDetailsFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSharedSpaceDetailsBinding.inflate(inflater, container, false)
            .apply { lifecycleOwner = viewLifecycleOwner }
        initViewModel(binding)
        return binding.root
    }

    private fun initViewModel(binding: FragmentSharedSpaceDetailsBinding) {
        viewModel = getViewModel(viewModelFactory)
        binding.viewModel = viewModel
        binding.sharedSpaceId = sharedSpaceDetailsArgs.sharedSpaceId.toSharedSpaceId()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.run {
            viewpager.adapter = DetailsPageAdapter()
            TabLayoutMediator(tabsDetails, viewpager) { tab, position ->
                tab.text = getString(DETAILS_TITLES[position])
            }.attach()
        }
        getCurrentSharedSpace(sharedSpaceDetailsArgs.sharedSpaceId)
    }

    override fun configureToolbar(toolbar: Toolbar) {
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back)
    }

    private fun getCurrentSharedSpace(sharedSpaceIdParcelable: SharedSpaceIdParcelable) {
        viewModel.getCurrentSharedSpace(sharedSpaceIdParcelable.toSharedSpaceId())
    }

    inner class DetailsPageAdapter() : FragmentStateAdapter(this) {

        override fun getItemCount() = DETAILS_TITLES.size

        override fun createFragment(position: Int): Fragment {
            require(position < itemCount) { "page number is not supported" }
            return SharedSpaceMembersFragment(sharedSpaceDetailsArgs.sharedSpaceId.toSharedSpaceId())
        }
    }
}
