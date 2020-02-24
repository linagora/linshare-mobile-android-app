package com.linagora.android.linshare.data.database.downloading

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.google.common.truth.Truth.assertThat
import com.linagora.android.linshare.data.database.LinShareDatabase
import com.linagora.android.testshared.TestFixtures.DownloadingTasks.CONFLICT_DOWNLOADING_TASK_ENTITY
import com.linagora.android.testshared.TestFixtures.DownloadingTasks.DOWNLOADING_TASK_ENTITY
import com.linagora.android.testshared.TestFixtures.DownloadingTasks.DOWNLOADING_TASK_ENTITY_2
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DownloadingTaskDaoTest {

    private lateinit var downloadingTaskDao: DownloadingTaskDao

    @Before
    fun setUp() {
        downloadingTaskDao = LinShareDatabase
            .buildDatabase(InstrumentationRegistry.getInstrumentation().targetContext)
            .downloadingTaskDao()
    }

    @Test
    fun storeTaskShouldSuccessWhenTableEmpty() {
        runBlocking {
            downloadingTaskDao.storeTask(DOWNLOADING_TASK_ENTITY)

            val documents = downloadingTaskDao.getAllTasks()
            assertThat(documents).hasSize(1)
            assertThat(documents).containsExactly(DOWNLOADING_TASK_ENTITY)
        }
    }

    @Test
    fun storeTaskShouldSuccessWhenTableHasOtherTask() {
        runBlocking {
            downloadingTaskDao.storeTask(DOWNLOADING_TASK_ENTITY)
            downloadingTaskDao.storeTask(DOWNLOADING_TASK_ENTITY_2)

            val documents = downloadingTaskDao.getAllTasks()
            assertThat(documents).hasSize(2)
            assertThat(documents).contains(DOWNLOADING_TASK_ENTITY_2)
        }
    }

    @Test
    fun storeTaskShouldNothingWhenTableAlreadyHasTask() {
        runBlocking {
            downloadingTaskDao.storeTask(DOWNLOADING_TASK_ENTITY)
            downloadingTaskDao.storeTask(DOWNLOADING_TASK_ENTITY)

            val documents = downloadingTaskDao.getAllTasks()
            assertThat(documents).hasSize(1)
            assertThat(documents).contains(DOWNLOADING_TASK_ENTITY)
        }
    }

    @Test
    fun storeTaskShouldUpdateWhenConflictOccurred() {
        runBlocking {
            downloadingTaskDao.storeTask(DOWNLOADING_TASK_ENTITY)
            downloadingTaskDao.storeTask(CONFLICT_DOWNLOADING_TASK_ENTITY)

            val documents = downloadingTaskDao.getAllTasks()
            assertThat(documents).hasSize(1)
            assertThat(documents).containsExactly(CONFLICT_DOWNLOADING_TASK_ENTITY)
        }
    }

    @Test
    fun removeTaskShouldSuccessWhenTableHasNoTask() {
        runBlocking {
            downloadingTaskDao.removeTask(DOWNLOADING_TASK_ENTITY)

            val documents = downloadingTaskDao.getAllTasks()
            assertThat(documents).hasSize(0)
        }
    }

    @Test
    fun removeTaskShouldSuccessWhenTableHasTask() {
        runBlocking {
            downloadingTaskDao.storeTask(DOWNLOADING_TASK_ENTITY)
            downloadingTaskDao.removeTask(DOWNLOADING_TASK_ENTITY)

            val documents = downloadingTaskDao.getAllTasks()
            assertThat(documents).hasSize(0)
        }
    }

    @Test
    fun removeTaskShouldDoNothingWhenRemoveNotMatchTask() {
        runBlocking {
            downloadingTaskDao.storeTask(DOWNLOADING_TASK_ENTITY)
            downloadingTaskDao.removeTask(DOWNLOADING_TASK_ENTITY_2)

            val documents = downloadingTaskDao.getAllTasks()
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
