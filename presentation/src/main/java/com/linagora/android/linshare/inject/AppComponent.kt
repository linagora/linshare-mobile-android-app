package com.linagora.android.linshare.inject

import com.linagora.android.linshare.inject.worker.WorkerBindingModule
import com.linagora.android.linshare.inject.worker.WorkerFactoryModule
import com.linagora.android.linshare.view.LinShareApplication
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        AndroidSupportInjectionModule::class,
        AppModule::class,
        DatabaseModule::class,
        DocumentModule::class,
        ActivityBindingModule::class,
        ViewModelModule::class,
        WorkerFactoryModule::class,
        WorkerBindingModule::class,
        NetworkModule::class
    ]
)
interface AppComponent : AndroidInjector<LinShareApplication> {
    @Component.Factory
    interface Factory {
        fun create(@BindsInstance application: LinShareApplication): AppComponent
    }
}
