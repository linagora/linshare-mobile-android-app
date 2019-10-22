package com.linagora.android.linshare.view

import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.linagora.android.linshare.R
import com.linagora.android.linshare.util.getViewModel
import com.linagora.android.linshare.view.base.BaseActivity

class MainActivity : BaseActivity(), NavigationHost {

    companion object {
        private val EMPTY_DRAWER: DrawerLayout? = null

        private val TOP_LEVEL_DESTINATIONS = setOf(
            R.id.wizardFragment
        )
    }

    private lateinit var navController: NavController
    private lateinit var viewModel: MainActivityViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = getViewModel(viewModelFactory)

        setContentView(R.layout.activity_main)

        retrieveNavigationController()
    }

    private fun retrieveNavigationController() {
        navController = findNavController(R.id.nav_host_fragment)
    }

    override fun registerToolbarWithNavigation(toolbar: Toolbar) {
        val appBarConfiguration = AppBarConfiguration(TOP_LEVEL_DESTINATIONS, EMPTY_DRAWER)
        toolbar.setupWithNavController(navController, appBarConfiguration)
    }
}
