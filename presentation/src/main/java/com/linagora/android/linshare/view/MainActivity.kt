package com.linagora.android.linshare.view

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavOptions
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import arrow.core.Either
import com.linagora.android.linshare.R
import com.linagora.android.linshare.databinding.ActivityMainBinding
import com.linagora.android.linshare.domain.model.properties.RecentUserPermissionAction.DENIED
import com.linagora.android.linshare.model.mapper.toParcelable
import com.linagora.android.linshare.model.properties.RuntimePermissionRequest
import com.linagora.android.linshare.model.properties.RuntimePermissionRequest.ShouldNotShowReadStorage
import com.linagora.android.linshare.model.properties.RuntimePermissionRequest.ShouldShowReadStorage
import com.linagora.android.linshare.model.resources.MenuId
import com.linagora.android.linshare.model.resources.MenuResource
import com.linagora.android.linshare.model.resources.ViewId
import com.linagora.android.linshare.util.Constant.LINSHARE_APPLICATION_ID
import com.linagora.android.linshare.util.Constant.UPLOAD_URI_BUNDLE_KEY
import com.linagora.android.linshare.util.getViewModel
import com.linagora.android.linshare.view.base.BaseActivity
import com.linagora.android.linshare.view.dialog.ReadStorageExplanationPermissionDialog
import com.linagora.android.linshare.view.menu.SideMenuDrawer
import org.slf4j.LoggerFactory

class MainActivity : BaseActivity(), NavigationHost {

    companion object {
        private val LOGGER = LoggerFactory.getLogger(MainActivity::class.java)

        private val TOP_LEVEL_DESTINATIONS = setOf(
            R.id.navigation_my_space
        )

        val INIT_DESTINATIONS = setOf(
            R.id.mainFragment
        )
    }

    private lateinit var navigationController: NavController
    private lateinit var viewModel: MainActivityViewModel
    private lateinit var rootView: View
    private lateinit var sideMenuDrawer: SideMenuDrawer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        LOGGER.info("onCreate()")

        viewModel = getViewModel(viewModelFactory)

        rootView = DataBindingUtil
            .setContentView<ActivityMainBinding>(
                this,
                R.layout.activity_main)
            .root

        retrieveNavigationController()

        setUpSideMenu()

        handleStoragePermissionRequest()

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
                setIntent(intent)
                handleSendAction(intent)
            }
        }
    }

    private fun handleSendAction(intent: Intent) {
        when (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            PackageManager.PERMISSION_GRANTED -> { extractSendAction(intent) }
            else -> { requestReadStoragePermission() }
        }
    }

    private fun handleStoragePermissionRequest() {
        viewModel.shouldShowPermissionRequestState.observe(this, Observer {
            when (it) {
                ShouldShowReadStorage -> {
                    ActivityCompat.requestPermissions(
                        this,
                        arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                        ReadExternalPermissionRequestCode.code
                    )
                }
                ShouldNotShowReadStorage -> {
                    showExplanationMessage()
                }
            }
        })
    }

    private fun requestReadStoragePermission() {
        LOGGER.info("requestReadStoragePermission")
        viewModel.shouldShowReadStoragePermissionRequest(
            systemShouldShowReadExternalRequestPermissionRationale())
    }

    private fun systemShouldShowReadExternalRequestPermissionRationale(): RuntimePermissionRequest {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            return ShouldShowReadStorage
        }
        return ShouldNotShowReadStorage
    }

    private fun extractSendAction(intent: Intent) {
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

    private fun retrieveNavigationController() {
        navigationController = findNavController(R.id.nav_host_fragment)
        navigationController.addOnDestinationChangedListener { _, destination, _ ->
            LOGGER.info("onDestinationChagned ${destination.id}")
            setUpSideMenuLockMode(destination)
            destination.id.takeIf { needToCheckSignedIn(it) }
                ?.let { viewModel.checkSignedIn() }
        }
    }

    private fun needToCheckSignedIn(destinationId: Int): Boolean {
        return INIT_DESTINATIONS.contains(destinationId)
    }

    override fun registerToolbarWithNavigation(toolbar: Toolbar) {
        val appBarConfiguration = AppBarConfiguration(TOP_LEVEL_DESTINATIONS, sideMenuDrawer.drawerLayout)
        toolbar.setupWithNavController(navigationController, appBarConfiguration)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            ReadExternalPermissionRequestCode.code -> {
                Either.cond(
                    test = grantResults.all { grantResult -> grantResult == PackageManager.PERMISSION_GRANTED },
                    ifTrue = { handleIntent(intent) },
                    ifFalse = { handleUserDenied() }
                )
            }
        }
    }

    private fun handleUserDenied() {
        viewModel.setActionForReadStoragePermissionRequest(DENIED)
        onBackPressed()
        finish()
    }

    private fun showExplanationMessage() {
        ReadStorageExplanationPermissionDialog(
            negativeText = getString(R.string.cancel),
            positiveText = getString(R.string.go_to_setting),
            onNegativeCallback = {
                onBackPressed()
                finish()
            },
            onPositiveCallback = { gotoSystemSettings() }
        ).show(supportFragmentManager, "read_storage_permission_explanation")
    }

    private fun gotoSystemSettings() {
        runCatching {
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            intent.data = Uri.parse("package:$LINSHARE_APPLICATION_ID")
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            finish()
            startActivity(intent)
        }.getOrElse {
            onBackPressed()
        }
    }

    private fun setUpSideMenu() {
        sideMenuDrawer = SideMenuDrawer(
            rootView = rootView,
            drawerLayoutId = ViewId(R.id.drawer_layout),
            navigationViewId = ViewId(R.id.side_menu),
            sideMenuResource = MenuResource(R.menu.main_side_menu)
        )

        sideMenuDrawer.apply {
            setupWithNavController(navigationController)

            setOnMenuItemClick(
                menuId = MenuId(R.id.navigation_account_details),
                menuItemClickListener = MenuItem.OnMenuItemClickListener {
                    navigateToAccountDetails()
                    closeDrawer()
                    true
                }
            )
        }
    }

    private fun setUpSideMenuLockMode(destination: NavDestination) {
        if (::sideMenuDrawer.isInitialized) {
            sideMenuDrawer.setSideMenuLockMode(
                destination = destination,
                topLevelDestinationIds = TOP_LEVEL_DESTINATIONS
            )
        }
    }

    private fun navigateToAccountDetails() {
        viewModel.currentCredential.value
            ?.let {
                val bundle = bundleOf("credential" to it.toParcelable())
                navigationController.navigate(R.id.navigation_account_details, bundle)
            }
    }
}
