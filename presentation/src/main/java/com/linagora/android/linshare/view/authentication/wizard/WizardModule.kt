package com.linagora.android.linshare.view.authentication.wizard

import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
internal abstract class WizardModule {

    @ContributesAndroidInjector
    internal abstract fun contributedWizardFragment(): WizardFragment
}
