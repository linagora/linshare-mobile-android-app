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

package com.linagora.android.linshare.data.repository.order

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.google.common.truth.Truth
import com.linagora.android.linshare.domain.model.order.OrderListConfigurationType
import com.linagora.android.linshare.domain.model.order.OrderListType
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PreferenceOrderListConfigurationRepositoryTest {

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var orderListConfigurationRepository: PreferenceOrderListConfigurationRepository

    @Before
    fun setup() {
        val context: Context = InstrumentationRegistry.getInstrumentation().targetContext
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        orderListConfigurationRepository = PreferenceOrderListConfigurationRepository(sharedPreferences)
    }

    @Test
    fun removeOrderListConfigurationShouldSuccessAfterPersist() {
        runBlocking {
            orderListConfigurationRepository.persistOrderListConfiguration(
                OrderListType.SharedSpace,
                OrderListConfigurationType.DescendingName
            )

            orderListConfigurationRepository.removeOrderConfiguration(OrderListType.SharedSpace)

            Truth.assertThat(orderListConfigurationRepository.getOrderConfiguration(OrderListType.SharedSpace))
                .isEqualTo(OrderListConfigurationType.AscendingName)
        }
    }

    @Test
    fun removeOrderListConfigurationShouldSuccessWhenNoOrderConfigurationWasSet() {
        runBlocking {
            orderListConfigurationRepository.removeOrderConfiguration(OrderListType.SharedSpace)

            Truth.assertThat(orderListConfigurationRepository.getOrderConfiguration(OrderListType.SharedSpace))
                .isEqualTo(OrderListConfigurationType.AscendingModificationDate)
        }
    }

    @Test
    fun persistOrderListConfigurationShouldSaveSuccessAscendingName() {
        runBlocking {
            orderListConfigurationRepository.persistOrderListConfiguration(
                OrderListType.SharedSpace,
                OrderListConfigurationType.AscendingName
            )

            Truth.assertThat(orderListConfigurationRepository.getOrderConfiguration(OrderListType.SharedSpace))
                .isEqualTo(OrderListConfigurationType.AscendingName)
        }
    }

    @Test
    fun persistOrderListConfigurationShouldSaveSuccessDescendingName() {
        runBlocking {
            orderListConfigurationRepository.persistOrderListConfiguration(
                OrderListType.SharedSpace,
                OrderListConfigurationType.DescendingName
            )

            Truth.assertThat(orderListConfigurationRepository.getOrderConfiguration(OrderListType.SharedSpace))
                .isEqualTo(OrderListConfigurationType.DescendingName)
        }
    }

    @Test
    fun persistOrderListConfigurationShouldSuccessUpdate() {
        runBlocking {
            orderListConfigurationRepository.persistOrderListConfiguration(
                OrderListType.SharedSpace,
                OrderListConfigurationType.DescendingName
            )
            orderListConfigurationRepository.persistOrderListConfiguration(
                OrderListType.SharedSpace,
                OrderListConfigurationType.AscendingName
            )

            Truth.assertThat(orderListConfigurationRepository.getOrderConfiguration(OrderListType.SharedSpace))
                .isEqualTo(OrderListConfigurationType.AscendingName)
        }
    }

    @Test
    fun getOrderConfigurationShouldReturnDefaultWhenNotSavedOrderListConfiguration() {
        runBlocking {
            Truth.assertThat(orderListConfigurationRepository.getOrderConfiguration(OrderListType.SharedSpace))
                .isEqualTo(OrderListConfigurationType.AscendingModificationDate)
        }
    }

    @After
    fun tearDown() {
        runBlocking {
            with(sharedPreferences.edit()) {
                orderListConfigurationRepository.removeOrderConfiguration(OrderListType.SharedSpace)
                commit()
            }
        }
    }
}
