package com.linagora.android.linshare.utils

import android.content.Context
import android.os.Build
import androidx.test.core.app.ApplicationProvider
import com.google.common.truth.Truth.assertThat
import com.linagora.android.linshare.util.SuggestNewNameUtils
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.P])
class SuggestNewNameUtilsTest {

    private lateinit var context: Context

    private lateinit var suggestNewNameUtils: SuggestNewNameUtils

    companion object {

        private const val NEW_WORKGROUP = "New workgroup"
        private const val NEW_WORKGROUP_1 = "New workgroup (1)"
        private const val NEW_WORKGROUP_2 = "New workgroup (2)"
        private const val NEW_WORKGROUP_3 = "New workgroup (3)"

        private val FULL_NEW_WORKGROUP_LIST = listOf(
            NEW_WORKGROUP_2,
            NEW_WORKGROUP_1,
            NEW_WORKGROUP
        )

        private val FULL_NEW_WORKGROUP_LIST_MISS_ITEM_IN_MIDLE_LIST = listOf(
            NEW_WORKGROUP_2,
            NEW_WORKGROUP
        )

        private val FULL_NEW_WORKGROUP_LIST_MISS_FIRST_ITEM = listOf(
            NEW_WORKGROUP_2,
            NEW_WORKGROUP_1
        )

        private val LIST_NOT_HAVE_NEW_WORKGROUP = listOf("testName1", "testName2")

        private val LIST_EMPTY = listOf<String>()

        private val LIST_RANDOM = listOf("testName2",
            NEW_WORKGROUP_2, "testName1",
            NEW_WORKGROUP
        )
    }

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        suggestNewNameUtils = SuggestNewNameUtils(context)
    }

    @Test
    fun suggestNameWorkgroupShouldReturnNextItemWithFullNewWorkGroupList() {
        assertThat(suggestNewNameUtils.suggestNewName(FULL_NEW_WORKGROUP_LIST, SuggestNewNameUtils.SuggestNameType.WORKGROUP))
            .isEqualTo(NEW_WORKGROUP_3)
    }

    @Test
    fun suggestNameWorkgroupShouldReturnNewWorkGroupIndexWithFullNewWorkGroupListMissingItemAtMidle() {
        assertThat(suggestNewNameUtils.suggestNewName(FULL_NEW_WORKGROUP_LIST_MISS_ITEM_IN_MIDLE_LIST, SuggestNewNameUtils.SuggestNameType.WORKGROUP))
            .isEqualTo(NEW_WORKGROUP_1)
    }

    @Test
    fun suggestNameWorkgroupShouldReturnNewWorkGroupWithFullNewWorkGroupLisMissingItemAtFirst() {
        assertThat(suggestNewNameUtils.suggestNewName(FULL_NEW_WORKGROUP_LIST_MISS_FIRST_ITEM, SuggestNewNameUtils.SuggestNameType.WORKGROUP))
            .isEqualTo(NEW_WORKGROUP)
    }

    @Test
    fun suggestNameWorkgroupShouldReturnNewWorkGroupWithListNotContainNewWorkGroupName() {
        assertThat(suggestNewNameUtils.suggestNewName(LIST_NOT_HAVE_NEW_WORKGROUP, SuggestNewNameUtils.SuggestNameType.WORKGROUP))
            .isEqualTo(NEW_WORKGROUP)
    }

    @Test
    fun suggestNameWorkgroupShouldReturnNewWorkGroupWithListEmpty() {
        assertThat(suggestNewNameUtils.suggestNewName(LIST_EMPTY, SuggestNewNameUtils.SuggestNameType.WORKGROUP))
            .isEqualTo(NEW_WORKGROUP)
    }

    @Test
    fun suggestNameWorkgroupShouldReturnNewWorkGroupWithListRandom() {
        assertThat(suggestNewNameUtils.suggestNewName(LIST_RANDOM, SuggestNewNameUtils.SuggestNameType.WORKGROUP))
            .isEqualTo(NEW_WORKGROUP_1)
    }
}
