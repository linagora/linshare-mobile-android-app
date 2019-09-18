package com.linagora.android.linshare.inject

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
        ActivityBindingModule::class,
        ViewModelModule::class
    ]
)
interface AppComponent : AndroidInjector<LinShareApplication> {
    @Component.Factory
    interface Factory {
        fun create(@BindsInstance application: LinShareApplication): AppComponent
    }
}