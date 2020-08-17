package com.linagora.android.linshare.utils

import com.google.common.truth.Truth.assertThat
import com.linagora.android.linshare.domain.model.sharedspace.SharedSpaceOperationRole
import com.linagora.android.linshare.util.filterSharedSpaceDestinationByRole
import com.linagora.android.testshared.SharedSpaceFixtures.SHARED_SPACE_1
import com.linagora.android.testshared.SharedSpaceFixtures.SHARED_SPACE_2
import com.linagora.android.testshared.SharedSpaceFixtures.SHARED_SPACE_3
import com.linagora.android.testshared.SharedSpaceFixtures.SHARED_SPACE_4
import org.junit.Test

class SharedSpaceListExtensionsTest {

    companion object {

        private val SHARED_SPACE_LIST = listOf(SHARED_SPACE_1, SHARED_SPACE_2, SHARED_SPACE_3, SHARED_SPACE_4)

        private val LIST_SHARED_SPACE_UPLOAD_ROLE = listOf(SHARED_SPACE_1, SHARED_SPACE_3, SHARED_SPACE_4)

        private val LIST_SHARED_SPACE_DELETE_ROLE = listOf(SHARED_SPACE_1, SHARED_SPACE_4)

        private val LIST_SHARED_SPACE_ADD_MEMBER_ROLE = listOf(SHARED_SPACE_1)

        private val LIST_SHARED_SPACE_EDIT_MEMBER_ROLE = listOf(SHARED_SPACE_1)
    }

    @Test
    fun filterSharedSpaceListByUploadRoleShouldReturnSharedSpaceListWithUploadRole() {
        assertThat(SHARED_SPACE_LIST.filterSharedSpaceDestinationByRole(SharedSpaceOperationRole.UploadRoles))
            .isEqualTo(LIST_SHARED_SPACE_UPLOAD_ROLE)
    }

    @Test
    fun filterSharedSpaceListByDeleteRoleShouldReturnSharedSpaceListWithDeleteRole() {
        assertThat(SHARED_SPACE_LIST.filterSharedSpaceDestinationByRole(SharedSpaceOperationRole.DeleteRoles))
            .isEqualTo(LIST_SHARED_SPACE_DELETE_ROLE)
    }

    @Test
    fun filterSharedSpaceListByAddMemberRoleShouldReturnSharedSpaceListWithAddMemberRole() {
        assertThat(SHARED_SPACE_LIST.filterSharedSpaceDestinationByRole(SharedSpaceOperationRole.AddMembersRole))
            .isEqualTo(LIST_SHARED_SPACE_ADD_MEMBER_ROLE)
    }

    @Test
    fun filterSharedSpaceListByAddMemberRoleShouldReturnSharedSpdaceListWithAddMemberRole() {
        assertThat(SHARED_SPACE_LIST.filterSharedSpaceDestinationByRole(emptyList()))
            .isEqualTo(SHARED_SPACE_LIST)
    }

    @Test
    fun filterSharedSpaceListByEditMemberRoleShouldReturnSharedSpaceListWithEditMemberRole() {
        assertThat(SHARED_SPACE_LIST.filterSharedSpaceDestinationByRole(SharedSpaceOperationRole.EditWorkGroupMemberRole))
            .isEqualTo(LIST_SHARED_SPACE_EDIT_MEMBER_ROLE)
    }
}
