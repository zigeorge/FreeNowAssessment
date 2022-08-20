package com.george.freenowassessment.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.george.freenowassessment.R
import com.george.freenowassessment.databinding.ActivityMainBinding
import com.george.freenowassessment.other.collectLifeCycleFlow
import com.george.freenowassessment.other.connectivity.ConnectivityObserver
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: VehicleListViewModel

    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration

    @Inject lateinit var connectivityObserver: ConnectivityObserver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)[VehicleListViewModel::class.java]
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment = supportFragmentManager.findFragmentById(
            R.id.nav_host_fragment
        ) as NavHostFragment
        navController = navHostFragment.navController

        // Setup the bottom navigation view with navController
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_nav)
        bottomNavigationView.setupWithNavController(navController)

        // Setup the ActionBar with navController and 3 top level destinations
        appBarConfiguration = AppBarConfiguration(
            setOf(R.id.vehicleFragment, R.id.mapsFragment)
        )
        setupActionBarWithNavController(navController, appBarConfiguration)

        /** check for selected vehicle */
        collectLifeCycleFlow(viewModel.vehicleSelected) { vehicleMarker ->
            vehicleMarker?.let {
                binding.bottomNav.selectedItemId = R.id.mapsFragment
            }
        }

        /** check for network */
        collectLifeCycleFlow(connectivityObserver.observe()) {
            if(it == ConnectivityObserver.Status.Available) {
                viewModel.loadVehicles()
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration)
    }
}