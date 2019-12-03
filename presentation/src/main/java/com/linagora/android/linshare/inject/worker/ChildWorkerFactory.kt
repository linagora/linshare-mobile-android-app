package com.linagora.android.linshare.inject.worker

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerParameters

interface ChildWorkerFactory {

    fun create(applicationContext: Context, params: WorkerParameters): ListenableWorker
}
