package com.linagora.android.linshare.data.repository.autocomplete

import com.google.common.truth.Truth.assertThat
import com.linagora.android.linshare.data.datasource.autocomplete.AutoCompleteDataSource
import com.linagora.android.linshare.domain.model.autocomplete.AutoCompletePattern
import com.linagora.android.linshare.domain.model.autocomplete.AutoCompleteType
import com.linagora.android.linshare.domain.model.autocomplete.ThreadMemberAutoCompleteResult
import com.linagora.android.linshare.domain.model.autocomplete.UserAutoCompleteResult
import com.linagora.android.linshare.domain.model.sharedspace.SharedSpaceId
import com.linagora.android.testshared.AutoCompleteFixtures.THREAD_MEMBER_AUTO_COMPLETE_RESULT_1
import com.linagora.android.testshared.AutoCompleteFixtures.THREAD_MEMBER_AUTO_COMPLETE_RESULT_2
import com.linagora.android.testshared.AutoCompleteFixtures.USER_AUTOCOMPLETE_1
import com.linagora.android.testshared.AutoCompleteFixtures.USER_AUTOCOMPLETE_2
import kotlinx.coroutines.test.runBlockingTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import java.util.UUID

class LinshareAutoCompleteRepositoryTest {

    @Mock
    lateinit var autoCompleteDataSource: AutoCompleteDataSource

    private lateinit var linshareAutoCompleteRepository: LinshareAutoCompleteRepository

    @BeforeEach
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        linshareAutoCompleteRepository = LinshareAutoCompleteRepository(autoCompleteDataSource)
    }

    @Test
    fun getAutoCompleteWithSharingTypeShouldReturnUserAutoCompleteResult() = runBlockingTest {
        `when`(autoCompleteDataSource.getAutoComplete(
            autoCompletePattern = AutoCompletePattern("user"),
            autoCompleteType = AutoCompleteType.SHARING
        )).thenAnswer { listOf(USER_AUTOCOMPLETE_1, USER_AUTOCOMPLETE_2) }

        val userAutoCompleteResult = linshareAutoCompleteRepository.getAutoComplete(AutoCompletePattern("user"), AutoCompleteType.SHARING)
        assertThat(userAutoCompleteResult).hasSize(2)
        assertThat(userAutoCompleteResult).containsExactly(USER_AUTOCOMPLETE_1, USER_AUTOCOMPLETE_2)
    }

    @Test
    fun getAutoCompleteWithSharingTypeShouldReturnEmptyListWhileNoMatchingPattern() = runBlockingTest {
        `when`(autoCompleteDataSource.getAutoComplete(
            autoCompletePattern = AutoCompletePattern("valid"),
            autoCompleteType = AutoCompleteType.SHARING
        )).thenAnswer { emptyList<UserAutoCompleteResult>() }

        val userAutoCompleteResult = linshareAutoCompleteRepository.getAutoComplete(AutoCompletePattern("valid"), AutoCompleteType.SHARING)
        assertThat(userAutoCompleteResult).isEmpty()
    }

    @Test
    fun getAutoCompleteWithThreadMemberTypeShouldReturnThreadMemberAutoCompleteResult() = runBlockingTest {
        val threadUuid = SharedSpaceId(UUID.fromString("49ac407c-df87-49b1-b961-90d7eafe4217"))
        `when`(autoCompleteDataSource.getAutoComplete(
            autoCompletePattern = AutoCompletePattern("user"),
            autoCompleteType = AutoCompleteType.THREAD_MEMBERS,
            threadUUID = threadUuid
        )).thenAnswer { listOf(THREAD_MEMBER_AUTO_COMPLETE_RESULT_1, THREAD_MEMBER_AUTO_COMPLETE_RESULT_2) }

        val threadMemberCompleteResult = linshareAutoCompleteRepository
            .getAutoComplete(AutoCompletePattern("user"), AutoCompleteType.THREAD_MEMBERS, threadUuid)
        assertThat(threadMemberCompleteResult).hasSize(2)
        assertThat(threadMemberCompleteResult)
            .containsExactly(THREAD_MEMBER_AUTO_COMPLETE_RESULT_1, THREAD_MEMBER_AUTO_COMPLETE_RESULT_2)
    }

    @Test
    fun getAutoCompleteWithThreadMemberTypeShouldReturnEmptyListWhileNoMatchingPattern() = runBlockingTest {
        val threadUuid = SharedSpaceId(UUID.fromString("49ac407c-df87-49b1-b961-90d7eafe4217"))
        `when`(autoCompleteDataSource.getAutoComplete(
            autoCompletePattern = AutoCompletePattern("valid"),
            autoCompleteType = AutoCompleteType.THREAD_MEMBERS,
            threadUUID = threadUuid
        )).thenAnswer { emptyList<ThreadMemberAutoCompleteResult>() }

        val userAutoCompleteResult = linshareAutoCompleteRepository
            .getAutoComplete(AutoCompletePattern("valid"), AutoCompleteType.THREAD_MEMBERS, threadUuid)
        assertThat(userAutoCompleteResult).isEmpty()
    }

    @Test
    fun getAutoCompleteWithThreadMemberTypeShouldThrowWhenNotProvideThreadId() = runBlockingTest {
        `when`(autoCompleteDataSource.getAutoComplete(
            autoCompletePattern = AutoCompletePattern("user"),
            autoCompleteType = AutoCompleteType.THREAD_MEMBERS
        )).thenThrow(IllegalArgumentException())

        assertThrows<IllegalArgumentException> { runBlockingTest {
            linshareAutoCompleteRepository.getAutoComplete(
                AutoCompletePattern("user"),
                AutoCompleteType.THREAD_MEMBERS
            )
        } }
    }
}
