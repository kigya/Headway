package com.kigya.headway.ui.main

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.SearchView
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI.setupWithNavController
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.kigya.headway.R
import com.kigya.headway.data.remote.ConnectivityObserver
import com.kigya.headway.data.remote.NetworkConnectivityObserver
import com.kigya.headway.databinding.ActivityMainBinding
import com.kigya.headway.utils.extensions.collectLatestLifecycleFlow
import com.kigya.headway.utils.extensions.showToast
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class MainActivity : AppCompatActivity(R.layout.activity_main), MenuHost {

    private val viewBinding by viewBinding(ActivityMainBinding::bind)

    @Inject
    lateinit var networkConnectivityObserver: NetworkConnectivityObserver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupBottomNavigationView()
        setupAppBar()
        observeNetworkState()
        val logoDrawable = AppCompatResources.getDrawable(this, R.drawable.icon_dots)
        supportActionBar?.setLogo(logoDrawable)
    }

    private fun setupBottomNavigationView() {
        viewBinding.bottomNavigationView.apply {
            setupWithNavController(getNavController())
        }
    }

    override fun onSupportNavigateUp() =
        getNavController().navigateUp() || super.onSupportNavigateUp()

    private fun setupAppBar() {
        val navController = getNavController()
        val appBarConfiguration = getAppBarConfiguration()
        setupActionBarWithNavController(getNavController(), appBarConfiguration)
        navController.apply {
            addOnNavControllerDestinationChangedListener()
            addActionBarMenuProvider()
        }
    }

    private fun addActionBarMenuProvider() = addMenuProvider(object : MenuProvider {
        override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) = Unit

        override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
            return when (menuItem.itemId) {
                android.R.id.home -> {
                    onBackPressedDispatcher.onBackPressed()
                    true
                }

                R.id.search -> {
                    invalidateOptionsMenu()
                    true
                }

                else -> true
            }
        }

        override fun onPrepareMenu(menu: Menu) {
            val searchItem = menu.findItem(R.id.search)
            val otherItems = listOf(R.id.filters, R.id.language)

            if (searchItem?.isActionViewExpanded == true) {
                otherItems.map { menu.findItem(it) }.filter { it?.isVisible == true }
                    .forEach { it.isVisible = false }
            } else {
                otherItems.map { menu.findItem(it) }.filter { it?.isVisible == false }
                    .forEach { it.isVisible = true }
            }
            return super.onPrepareMenu(menu)
        }
    })

    private fun NavController.addOnNavControllerDestinationChangedListener() {
        addOnDestinationChangedListener { _, destination, _ ->
            supportActionBar?.title = destination.label
            if (destination.id != R.id.homeFragment) invalidateOptionsMenu()
        }
    }

    private fun getAppBarConfiguration() = AppBarConfiguration(
        setOf(
            R.id.homeFragment, R.id.favoritesFragment
        )
    )

    private fun observeNetworkState() =
        collectLatestLifecycleFlow(networkConnectivityObserver.observe()) { status ->
            when (status) {
                ConnectivityObserver.Status.Losing -> showToast(getString(R.string.internet_connection_is_unstable))

                ConnectivityObserver.Status.Lost -> showToast(getString(R.string.internet_connection_is_lost))

                else -> Unit
            }
        }

    private fun getNavController(): NavController {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.newsNavHostFragment) as NavHostFragment
        return navHostFragment.navController
    }
}
