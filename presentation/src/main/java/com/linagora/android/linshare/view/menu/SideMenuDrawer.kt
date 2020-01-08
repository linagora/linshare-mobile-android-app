package com.linagora.android.linshare.view.menu

import android.view.MenuItem.OnMenuItemClickListener
import android.view.View
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.navigation.NavigationView
import com.linagora.android.linshare.model.resources.MenuId
import com.linagora.android.linshare.model.resources.MenuResource
import com.linagora.android.linshare.model.resources.ViewId

class SideMenuDrawer(
    rootView: View,
    drawerLayoutId: ViewId,
    navigationViewId: ViewId,
    sideMenuResource: MenuResource
) {
    private val navigationView: NavigationView = rootView.findViewById(navigationViewId.viewResId)

    val drawerLayout: DrawerLayout = rootView.findViewById(drawerLayoutId.viewResId)

    init {
        navigationView.inflateMenu(sideMenuResource.menuRes)
    }

    fun setupWithNavController(navigationController: NavController) {
        navigationView.setupWithNavController(navigationController)
    }

    fun setOnMenuItemClick(menuId: MenuId, menuItemClickListener: OnMenuItemClickListener) {
        navigationView.menu.findItem(menuId.value)
            ?.setOnMenuItemClickListener(menuItemClickListener)
    }

    fun closeDrawer() {
        drawerLayout.closeDrawer(GravityCompat.START)
    }
}
