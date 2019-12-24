package com.linagora.android.linshare.view

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import arrow.core.Either
import com.linagora.android.linshare.R
import com.linagora.android.linshare.domain.model.properties.UserStoragePermissionHistory.DENIED
import com.linagora.android.linshare.model.properties.StoragePermissionRequest
import com.linagora.android.linshare.model.properties.StoragePermissionRequest.SHOULD_NOT_SHOW
import com.linagora.android.linshare.model.properties.StoragePermissionRequest.SHOULD_SHOW
import com.linagora.android.linshare.util.Constant.LINSHARE_APPLICATION_ID
import com.linagora.android.linshare.util.Constant.UPLOAD_URI_BUNDLE_KEY
import com.linagora.android.linshare.util.getViewModel
import com.linagora.android.linshare.view.base.BaseActivity
import com.linagora.android.linshare.view.dialog.ReadStorageExplanationPermissionDialog
import org.slf4j.LoggerFactory

class MainActivity : BaseActivity(), NavigationHost {

    companion object {
        private val LOGGER = LoggerFactory.getLogger(MainActivity::class.java)

        private val EMPTY_DRAWER: DrawerLayout? = null

        private val TOP_LEVEL_DESTINATIONS = setOf(
            R.id.wizardFragment,
            R.id.uploadFragment
        )

        val INIT_DESTINATIONS = setOf(
            R.id.mainFragment
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
            Intent.ACTION_SEND -> handleSendAction(intent)
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
                SHOULD_SHOW -> {
                    ActivityCompat.requestPermissions(
                        this,
                        arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                        ReadExternalPermissionRequestCode.code
                    )
                }
                SHOULD_NOT_SHOW -> {
                    showExplanationMessage()
                }
            }
        })
    }

    private fun requestReadStoragePermission() {
        LOGGER.info("requestReadStoragePermission")
        viewModel.shouldShowPermissionRequest(
            systemShouldShowRequestPermissionRationale())
    }

    private fun systemShouldShowRequestPermissionRationale(): StoragePermissionRequest {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            return SHOULD_SHOW
        }
        return SHOULD_NOT_SHOW
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
        viewModel.setUserStoragePermissionRequest(DENIED)
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
}
