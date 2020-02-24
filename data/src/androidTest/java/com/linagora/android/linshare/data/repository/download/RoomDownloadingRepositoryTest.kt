package com.linagora.android.linshare.data.repository.download

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.google.common.truth.Truth.assertThat
import com.linagora.android.linshare.data.database.LinShareDatabase
import com.linagora.android.linshare.data.database.downloading.DownloadingTaskDao
import com.linagora.android.linshare.data.database.downloading.toDownloadingTask
import com.linagora.android.testshared.TestFixtures.DownloadingTasks.CONFLICT_DOWNLOADING_TASK_ENTITY
import com.linagora.android.testshared.TestFixtures.DownloadingTasks.DOWNLOADING_TASK_ENTITY
import com.linagora.android.testshared.TestFixtures.DownloadingTasks.DOWNLOADING_TASK_ENTITY_2
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RoomDownloadingRepositoryTest {

    private lateinit var downloadingTaskDao: DownloadingTaskDao

    private lateinit var repository: RoomDownloadingRepository

    @Before
    fun setUp() {
        downloadingTaskDao = LinShareDatabase
            .buildDatabase(InstrumentationRegistry.getInstrumentation().targetContext)
            .downloadingTaskDao()
        repository = RoomDownloadingRepository(downloadingTaskDao)
    }

    @Test
    fun storeTaskShouldSuccessWhenTableEmpty() {
        runBlocking {
            repository.storeTask(DOWNLOADING_TASK_ENTITY.toDownloadingTask())

            val documents = repository.getAllTasks()
            assertThat(documents).hasSize(1)
            assertThat(documents)
                .containsExactly(DOWNLOADING_TASK_ENTITY.toDownloadingTask())
        }
    }

    @Test
    fun storeTaskShouldSuccessWhenTableHasOtherTask() {
        runBlocking {
            repository.storeTask(DOWNLOADING_TASK_ENTITY.toDownloadingTask())
            repository.storeTask(DOWNLOADING_TASK_ENTITY_2.toDownloadingTask())

            val documents = repository.getAllTasks()
            assertThat(documents).hasSize(2)
            assertThat(documents)
                .contains(DOWNLOADING_TASK_ENTITY_2.toDownloadingTask())
        }
    }

    @Test
    fun storeTaskShouldNothingWhenTableAlreadyHasTask() {
        runBlocking {
            repository.storeTask(DOWNLOADING_TASK_ENTITY.toDownloadingTask())
            repository.storeTask(DOWNLOADING_TASK_ENTITY.toDownloadingTask())

            val documents = repository.getAllTasks()
            assertThat(documents).hasSize(1)
            assertThat(documents)
                .contains(DOWNLOADING_TASK_ENTITY.toDownloadingTask())
        }
    }

    @Test
    fun storeTaskShouldUpdateWhenConflictOccurred() {
        runBlocking {
            repository.storeTask(DOWNLOADING_TASK_ENTITY.toDownloadingTask())
            repository.storeTask(CONFLICT_DOWNLOADING_TASK_ENTITY.toDownloadingTask())

            val documents = repository.getAllTasks()
            assertThat(documents).hasSize(1)
            assertThat(documents)
                .contains(CONFLICT_DOWNLOADING_TASK_ENTITY.toDownloadingTask())
        }
    }

    @Test
    fun removeTaskShouldSuccessWhenTableHasNoTask() {
        runBlocking {
            repository.removeTask(DOWNLOADING_TASK_ENTITY.toDownloadingTask())

            val documents = repository.getAllTasks()
            assertThat(documents).hasSize(0)
        }
    }

    @Test
    fun removeTaskShouldSuccessWhenTableHasTask() {
        runBlocking {
            repository.storeTask(DOWNLOADING_TASK_ENTITY.toDownloadingTask())
            repository.removeTask(DOWNLOADING_TASK_ENTITY.toDownloadingTask())

            val documents = repository.getAllTasks()
            assertThat(documents).hasSize(0)
        }
    }

    @Test
    fun removeTaskShouldDoNothingWhenRemoveNotMatchTask() {
        runBlocking {
            repository.storeTask(DOWNLOADING_TASK_ENTITY.toDownloadingTask())
            repository.removeTask(DOWNLOADING_TASK_ENTITY_2.toDownloadingTask())

            val documents = repository.getAllTasks()
            assertThat(documents).hasSize(1)
        }
    }

    @After
    fun tearDown() {
        runBlocking {
            downloadingTaskDao.removeAll()
        }
    }
}
