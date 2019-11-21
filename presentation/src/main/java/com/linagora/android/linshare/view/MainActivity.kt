package com.linagora.android.linshare.view

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.linagora.android.linshare.R
import com.linagora.android.linshare.util.Constant.UPLOAD_URI_BUNDLE_KEY
import com.linagora.android.linshare.util.getViewModel
import com.linagora.android.linshare.view.base.BaseActivity
import org.slf4j.LoggerFactory

class MainActivity : BaseActivity(), NavigationHost {

    companion object {
        private val LOGGER = LoggerFactory.getLogger(MainActivity::class.java)

        private val EMPTY_DRAWER: DrawerLayout? = null

        private val TOP_LEVEL_DESTINATIONS = setOf(
            R.id.wizardFragment
        )

        val INIT_DESTINATIONS = setOf(
            R.id.mainFragment,
            R.id.uploadFragment
        )
    }

    private lateinit var navigationController: NavController
    private lateinit var viewModel: MainActivityViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        LOGGER.info("onCreate()")

        viewModel = getViewModel(viewModelFactory)

        setContentView(R.layout.activity_main)

        retrieveNavigationController()

        handleIntent(intent)
    }

    override fun onNewIntent(intent: Intent?) {
        LOGGER.info("onNewIntent()")
        super.onNewIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent?) {
        LOGGER.info("handleIntent()")
        when (intent?.action) {
            Intent.ACTION_SEND -> {
                intent.getParcelableExtra<Uri>(Intent.EXTRA_STREAM)
                    ?.let {
                        val bundle = Bundle()
                        bundle.putParcelable(UPLOAD_URI_BUNDLE_KEY, it)
                        navigationController.navigate(
                            R.id.uploadFragment,
                            bundle,
                            NavOptions.Builder()
                                .setPopUpTo(R.id.mainFragment, true)
                                .build()
                        )
                    }
            }
        }
    }

    private fun retrieveNavigationController() {
        navigationController = findNavController(R.id.nav_host_fragment)
        navigationController.addOnDestinationChangedListener { _, destination, _ ->
            LOGGER.info("onDestinationChagned $destination")
            destination.id.takeIf { needToCheckSignedIn(it) }
                ?.let { viewModel.checkSignedIn() }
        }
    }

    private fun needToCheckSignedIn(destinationId: Int): Boolean {
        return INIT_DESTINATIONS.contains(destinationId)
    }

    override fun registerToolbarWithNavigation(toolbar: Toolbar) {
        val appBarConfiguration = AppBarConfiguration(TOP_LEVEL_DESTINATIONS, EMPTY_DRAWER)
        toolbar.setupWithNavController(navigationController, appBarConfiguration)
    }
}
