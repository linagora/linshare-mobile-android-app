package com.linagora.android.linshare.inject.worker

import androidx.work.WorkerFactory
import dagger.Binds
import dagger.Module

@Module
abstract class WorkerFactoryModule {

    @Binds
    internal abstract fun bindWorkerFactory(factory: LinShareWorkerFactory): WorkerFactory
}
