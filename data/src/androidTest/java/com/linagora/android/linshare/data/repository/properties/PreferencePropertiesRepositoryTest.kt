package com.linagora.android.linshare.data.repository.properties

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.google.common.truth.Truth.assertThat
import com.linagora.android.linshare.domain.model.properties.RecentUserPermissionAction.DENIED
import com.linagora.android.linshare.domain.model.properties.RecentUserPermissionAction.NONE
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PreferencePropertiesRepositoryTest {

    private lateinit var sharedPreferences: SharedPreferences

    private lateinit var propertiesRepository: PreferencePropertiesRepository

    @Before
    fun setUp() {
        val context: Context = InstrumentationRegistry.getInstrumentation().targetContext
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        propertiesRepository = PreferencePropertiesRepository(sharedPreferences)
    }

    @Test
    fun storeRecentActionForReadStoragePermissionShouldSuccess() {
        runBlockingTest {
            propertiesRepository.storeRecentActionForReadStoragePermission(DENIED)

            assertThat(propertiesRepository.getRecentActionForReadStoragePermission()).isEqualTo(DENIED)
        }
    }

    @Test
    fun getRecentActionForReadStoragePermissionShouldReturnNoneWithoutStorePreviously() {
        runBlockingTest {
            assertThat(propertiesRepository.getRecentActionForReadStoragePermission()).isEqualTo(NONE)
        }
    }

    @Test
    fun storeRecentActionForWriteStoragePermissionShouldSuccess() {
        runBlockingTest {
            propertiesRepository.storeRecentActionForWriteStoragePermission(DENIED)

            assertThat(propertiesRepository.getRecentActionForWriteStoragePermission()).isEqualTo(DENIED)
        }
    }

    @Test
    fun getRecentActionForWriteStoragePermissionShouldReturnNoneWithoutStorePreviously() {
        runBlockingTest {
            assertThat(propertiesRepository.getRecentActionForWriteStoragePermission()).isEqualTo(NONE)
        }
    }

    @After
    fun tearDown() {
        sharedPreferences
            .edit()
            .clear()
            .commit()
    }
}
