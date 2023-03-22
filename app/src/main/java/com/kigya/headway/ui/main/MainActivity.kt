package com.kigya.headway.ui.main

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.kigya.headway.R
import com.kigya.headway.data.remote.ConnectivityObserver
import com.kigya.headway.data.remote.NetworkConnectivityObserver
import com.kigya.headway.databinding.ActivityMainBinding
import com.kigya.headway.utils.extensions.collectLatestLifecycleFlow
import com.kigya.headway.utils.extensions.gone
import com.kigya.headway.utils.extensions.showToast
import com.kigya.headway.utils.extensions.visible
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity(R.layout.activity_main) {

    private val viewBinding by viewBinding(ActivityMainBinding::bind)

    @Inject
    lateinit var networkConnectivityObserver: NetworkConnectivityObserver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding.bottomNavigationView.setupWithNavController(getNavController())
        observeNetworkState()
    }

    private fun observeNetworkState() =
        collectLatestLifecycleFlow(networkConnectivityObserver.observe()) { status ->
            when (status) {
                ConnectivityObserver.Status.Losing ->
                    showToast(getString(R.string.internet_connection_is_unstable))

                ConnectivityObserver.Status.Lost ->
                    showToast(getString(R.string.internet_connection_is_lost))

                else -> Unit
            }
        }

    private fun getNavController(): NavController {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.newsNavHostFragment) as NavHostFragment
        return navHostFragment.navController
    }
}
