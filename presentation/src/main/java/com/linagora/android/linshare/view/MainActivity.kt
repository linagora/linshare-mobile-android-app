/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 *
 * Copyright (C) 2020 LINAGORA
 *
 * This program is free software: you can redistribute it and/or modify it under the
 * terms of the GNU Affero General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later version,
 * provided you comply with the Additional Terms applicable for LinShare software by
 * Linagora pursuant to Section 7 of the GNU Affero General Public License,
 * subsections (b), (c), and (e), pursuant to which you must notably (i) retain the
 * display in the interface of the “LinShare™” trademark/logo, the "Libre & Free" mention,
 * the words “You are using the Free and Open Source version of LinShare™, powered by
 * Linagora © 2009–2020. Contribute to Linshare R&D by subscribing to an Enterprise
 * offer!”. You must also retain the latter notice in all asynchronous messages such as
 * e-mails sent with the Program, (ii) retain all hypertext links between LinShare and
 * http://www.linshare.org, between linagora.com and Linagora, and (iii) refrain from
 * infringing Linagora intellectual property rights over its trademarks and commercial
 * brands. Other Additional Terms apply, see
 * <http://www.linshare.org/licenses/LinShare-License_AfferoGPL-v3.pdf>
 * for more details.
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for
 * more details.
 * You should have received a copy of the GNU Affero General Public License and its
 * applicable Additional Terms for LinShare along with this program. If not, see
 * <http://www.gnu.org/licenses/> for the GNU Affero General Public License version
 *  3 and <http://www.linshare.org/licenses/LinShare-License_AfferoGPL-v3.pdf> for
 *  the Additional Terms applicable to LinShare software.
 */

package com.linagora.android.linshare.view

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavOptions
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.linagora.android.linshare.R
import com.linagora.android.linshare.databinding.ActivityMainBinding
import com.linagora.android.linshare.domain.model.functionality.FunctionalityIdentifier
import com.linagora.android.linshare.domain.utils.NoOp
import com.linagora.android.linshare.model.mapper.toParcelable
import com.linagora.android.linshare.model.properties.RuntimePermissionRequest.Initial
import com.linagora.android.linshare.model.properties.RuntimePermissionRequest.ShouldNotShowReadContact
import com.linagora.android.linshare.model.properties.RuntimePermissionRequest.ShouldNotShowWriteStorage
import com.linagora.android.linshare.model.properties.RuntimePermissionRequest.ShouldShowReadContact
import com.linagora.android.linshare.model.resources.MenuId
import com.linagora.android.linshare.model.resources.MenuResource
import com.linagora.android.linshare.model.resources.ViewId
import com.linagora.android.linshare.util.Constant.LINSHARE_APPLICATION_ID
import com.linagora.android.linshare.util.getViewModel
import com.linagora.android.linshare.view.base.BaseActivity
import com.linagora.android.linshare.view.dialog.WriteStorageExplanationPermissionDialog
import com.linagora.android.linshare.view.menu.MenuVisible
import com.linagora.android.linshare.view.menu.SideMenuDrawer
import com.linagora.android.linshare.view.upload.UploadFragmentArgs
import org.slf4j.LoggerFactory

class MainActivity : BaseActivity(), NavigationHost {

    companion object {
        private val LOGGER = LoggerFactory.getLogger(MainActivity::class.java)

        private val TOP_LEVEL_DESTINATIONS = setOf(
            R.id.navigation_my_space,
            R.id.navigation_received_shares,
            R.id.navigation_shared_space,
            R.id.navigation_shared_spaced_document
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
        extractSendAction(intent)
    }

    private fun handleStoragePermissionRequest() {
        viewModel.shouldShowPermissionRequestState.observe(this, Observer {
            when (it) {
                Initial, ShouldShowReadContact, ShouldNotShowReadContact -> { NoOp }
                ShouldNotShowWriteStorage -> showWriteStoragePermissionExplanation()
                else -> handleIntent(intent)
            }
        })
    }

    private fun extractSendAction(intent: Intent) {
        intent.getParcelableExtra<Uri>(Intent.EXTRA_STREAM)
            ?.let {
                val bundle = UploadFragmentArgs(Navigation.UploadType.OUTSIDE_APP, it)
                    .toBundle()
                navigationController.navigate(
                    R.id.uploadFragment,
                    bundle,
                    NavOptions.Builder()
                        .setPopUpTo(R.id.navigation_graph, true)
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

    private fun showWriteStoragePermissionExplanation() {
        WriteStorageExplanationPermissionDialog(
            negativeText = getString(R.string.cancel),
            positiveText = getString(R.string.go_to_setting),
            onPositiveCallback = { gotoSystemSettings() }
        ).show(supportFragmentManager, "write_storage_permission_explanation")
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

        viewModel.functionalityObserver.allFunctionality.observe(this, Observer { functionalities ->
            val menuVisible = functionalities.takeIf { it.isNotEmpty() }
                ?.first { functionality -> functionality.identifier == FunctionalityIdentifier.WORK_GROUP }?.enable
                ?.let { enable -> if (enable) MenuVisible.VISIBLE else MenuVisible.GONE }
                ?: MenuVisible.GONE

            sideMenuDrawer.visibleGroupMenu(R.id.navigation_shared_space, menuVisible)
        })
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
        viewModel.currentAuthentication.value
            ?.let { authenticationInfo ->
                val bundle = bundleOf("credential" to authenticationInfo.credential.toParcelable())
                navigationController.navigate(R.id.navigation_account_details, bundle)
            }
    }
}
