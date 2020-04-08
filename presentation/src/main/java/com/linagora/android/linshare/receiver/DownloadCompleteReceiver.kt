package com.linagora.android.linshare.receiver

import android.app.DownloadManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.database.Cursor
import arrow.core.singleOrNone
import com.linagora.android.linshare.R
import com.linagora.android.linshare.domain.model.download.DownloadingTask
import com.linagora.android.linshare.domain.repository.download.DownloadingRepository
import com.linagora.android.linshare.model.download.DownloadStatus
import com.linagora.android.linshare.model.download.DownloadStatus.DownloadFailed
import com.linagora.android.linshare.model.download.DownloadStatus.DownloadSuccess
import com.linagora.android.linshare.notification.BaseNotification.Companion.FINISHED_NOTIFICATION
import com.linagora.android.linshare.notification.SystemNotifier
import com.linagora.android.linshare.notification.UploadAndDownloadNotification
import com.linagora.android.linshare.notification.setAutoCancel
import com.linagora.android.linshare.util.CoroutinesDispatcherProvider
import com.linagora.android.linshare.util.FileSize
import com.linagora.android.linshare.util.FileSize.SizeFormat.SHORT
import com.linagora.android.linshare.view.OpenDownloadViewRequestCode
import dagger.android.DaggerBroadcastReceiver
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.slf4j.LoggerFactory
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DownloadCompleteReceiver : DaggerBroadcastReceiver() {

    companion object {
        private val LOGGER = LoggerFactory.getLogger(DownloadCompleteReceiver::class.java)

        private const val UNKNOWN_DOWNLOAD_ID = -1L
    }

    @Inject
    lateinit var dispatcherProvider: CoroutinesDispatcherProvider

    @Inject
    lateinit var downloadingRepository: DownloadingRepository

    @Inject
    lateinit var downloadNotification: UploadAndDownloadNotification

    @Inject
    lateinit var systemNotifier: SystemNotifier

    override fun onReceive(context: Context?, intent: Intent?) {
        super.onReceive(context, intent)
        LOGGER.info("onReceive() $intent")
        GlobalScope.launch(dispatcherProvider.io) {
            val downloadId = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, UNKNOWN_DOWNLOAD_ID)
            downloadingRepository.getAllTasks()
                .singleOrNone { task -> task.enqueuedDownloadId.value == downloadId }
                .map { launch { handleCompletedTask(context, it) } }
        }
    }

    private suspend fun handleCompletedTask(context: Context?, downloadingTask: DownloadingTask) {
        val downloadManager = context?.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val query = DownloadManager.Query().setFilterById(downloadingTask.enqueuedDownloadId.value)
        downloadManager.query(query).use { cursor ->
            cursor.moveToFirst()
            when (getDownloadStatus(cursor)) {
                DownloadSuccess -> notifyDownloadSuccess(context, downloadingTask)
                DownloadFailed -> notifyDownloadFailed(context, cursor, downloadingTask)
            }
        }.also { downloadingRepository.removeTask(downloadingTask) }
    }

    private fun getDownloadStatus(cursor: Cursor): DownloadStatus {
        return when (cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))) {
            DownloadManager.STATUS_SUCCESSFUL -> DownloadSuccess
            else -> DownloadFailed
        }
    }

    private fun notifyDownloadSuccess(context: Context, downloadingTask: DownloadingTask) {
        downloadNotification.notify(systemNotifier.generateNotificationId()) {
            this.setContentTitle(context?.getString(R.string.download_success))
                .setContentText(downloadingTask.downloadName)
                .setSubText(getSuccessSubText(context, downloadingTask))
                .setContentIntent(createPendingOpenFileExplorerIntent(context))
                .setOngoing(FINISHED_NOTIFICATION)
                .build()
                .setAutoCancel()
        }
    }

    private fun notifyDownloadFailed(
        context: Context,
        cursor: Cursor,
        downloadingTask: DownloadingTask
    ) {
        downloadNotification.notify(systemNotifier.generateNotificationId()) {
            this.setContentTitle(downloadingTask.downloadName)
                .setContentText(getFailedReasonMessage(context, cursor))
                .setOngoing(FINISHED_NOTIFICATION)
                .build()
        }
    }

    private fun getSuccessSubText(context: Context, downloadingTask: DownloadingTask): String {
        return context.getString(R.string.downloaded, FileSize(downloadingTask.downloadSize).format(SHORT))
    }

    private fun getFailedReasonMessage(context: Context, cursor: Cursor): String {
        val stringId = when (cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_REASON))) {
            DownloadManager.ERROR_CANNOT_RESUME -> R.string.error_can_not_resume_download
            DownloadManager.ERROR_INSUFFICIENT_SPACE -> R.string.error_insufficient_space
            else -> R.string.download_failed
        }
        return context.getString(stringId)
    }

    private fun createPendingOpenFileExplorerIntent(context: Context): PendingIntent {
        val openDownloadFileLocation = Intent(DownloadManager.ACTION_VIEW_DOWNLOADS)
        return PendingIntent.getActivity(
            context,
            OpenDownloadViewRequestCode.code,
            openDownloadFileLocation,
            PendingIntent.FLAG_ONE_SHOT
        )
    }
}
