package com.george.freenowassessment.ui

import android.os.Bundle
import android.view.View
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
import com.george.freenowassessment.other.showDialog
import com.george.freenowassessment.other.exceptions.ErrorState
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

    private var networkState: ConnectivityObserver.Status = ConnectivityObserver.Status.Available

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

        binding.retry.setOnClickListener {
            binding.errorLayout.visibility = View.GONE
            viewModel.loadVehicles()
        }

        /** check for selected vehicle */
        collectLifeCycleFlow(viewModel.vehicleSelected) { vehicleMarker ->
            vehicleMarker?.let {
                binding.bottomNav.selectedItemId = R.id.mapsFragment
            }
        }

        /** check for error to handle */
        collectLifeCycleFlow(viewModel.shouldShowError) {
            handleErrorState(it)
        }

        /** check for network state change */
        collectLifeCycleFlow(connectivityObserver.observe()) {
            networkState = it
        }
    }

    /**
     * handle [ErrorState] returned from [VehicleListViewModel]
     * */
    private fun handleErrorState(errorState: ErrorState) {
        when (errorState) {
            ErrorState.unableToLoad -> errorDialog()
            else -> {
                binding.errorLayout.visibility = View.VISIBLE
                if (networkState == ConnectivityObserver.Status.Unavailable) {
                    binding.errorText.text = getString(R.string.failed_to_connect)
                }
            }
        }
    }

    /**
     * Shows a dialog when error occurred and no data to display
     * */
    private fun errorDialog() {
        when (networkState) {
            ConnectivityObserver.Status.Available -> showDialog(
                getString(R.string.failed_to_load),
                positiveText = getString(R.string.retry),
                negativeText = getString(R.string.exit),
                positiveAction = { _, _ ->
                    viewModel.loadVehicles()
                },
                negativeAction = { _, _ ->
                    finish()
                }
            )
            else -> showDialog(
                getString(R.string.failed_to_connect),
                positiveText = getString(R.string.retry),
                positiveAction = { _, _ ->
                    viewModel.loadVehicles()
                }
            )
        }

    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration)
    }
}