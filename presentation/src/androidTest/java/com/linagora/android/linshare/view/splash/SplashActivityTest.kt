package com.linagora.android.linshare.view.splash

import android.app.Instrumentation
import android.content.Context
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.google.common.truth.Truth.assertThat
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SplashActivityTest {

    private val context: Context = InstrumentationRegistry.getInstrumentation().targetContext
    private val instrumentation: Instrumentation = InstrumentationRegistry.getInstrumentation()

    @Test
    fun testLaunchActivityIsSplashActivity() {
        val monitor = Instrumentation.ActivityMonitor(
            SplashActivity::class.qualifiedName, null, false)

        instrumentation.addMonitor(monitor)

        val launchIntent = context.packageManager
            .getLaunchIntentForPackage(context.packageName)
        instrumentation.startActivitySync(launchIntent)

        val activity = monitor.waitForActivity()

        assertThat(activity.localClassName)
            .contains(SplashActivity::class.simpleName)
    }
}
