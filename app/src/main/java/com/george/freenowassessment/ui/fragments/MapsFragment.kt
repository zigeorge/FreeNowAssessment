package com.george.freenowassessment.ui.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.george.freenowassessment.R
import com.george.freenowassessment.databinding.FragmentMapsBinding
import com.george.freenowassessment.other.BitmapHelper
import com.george.freenowassessment.other.addMarkers
import com.george.freenowassessment.ui.VehicleListViewModel
import com.george.freenowassessment.ui.adapters.MarkerInfoWindowAdapter
import com.george.freenowassessment.ui.vo.VehicleMarker
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.android.ktx.awaitMap
import com.google.maps.android.ktx.awaitMapLoad
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MapsFragment : Fragment() {

    private val viewModel by activityViewModels<VehicleListViewModel>()

    private var _binding: FragmentMapsBinding? = null

    private var googleMap: GoogleMap? = null

    private var bounds = LatLngBounds.Builder()

    private var selectedVehicle: VehicleMarker? = null

    /**
     * The icon to use for each cluster item
     */
    private val carIcon: BitmapDescriptor by lazy {
        val color = ContextCompat.getColor(requireContext(), R.color.colorPrimary)
        BitmapHelper.vectorToBitmap(requireContext(), R.drawable.ic_car_marker, color)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMapsBinding.inflate(inflater, container, false)
        return _binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lifecycleScope.launchWhenCreated {
            googleMap = configureGoogleMap()
        }

        lifecycleScope.launchWhenResumed {
            launch {
                setAllVehiclesInMap()
            }
            launch {
                setMapToShowSelectedVehicle()
            }
        }
    }

    /**
     * collect vehicleMarkers from [VehicleListViewModel]
     * */
    private suspend fun setAllVehiclesInMap() {
        viewModel.vehicleMarkers.collectLatest { vehicleMarkers ->
            if(vehicleMarkers.isEmpty()) {
                googleMap?.clear()
                bounds = LatLngBounds.builder()
            } else {
                googleMap?.addMarkers(vehicleMarkers, carIcon)
                setLatLngInBound(vehicleMarkers)
            }
        }
    }

    /**
    * initialize [GoogleMap] from [SupportMapFragment] & configure with [MarkerInfoWindowAdapter]
    * */
    @SuppressLint("PotentialBehaviorOverride")
    private suspend fun configureGoogleMap(): GoogleMap? {
        val mapFragment: SupportMapFragment? =
            childFragmentManager.findFragmentById(R.id.map) as? SupportMapFragment
        googleMap = mapFragment?.awaitMap()
        googleMap?.awaitMapLoad()
        googleMap?.setInfoWindowAdapter(MarkerInfoWindowAdapter(requireContext()))
        return googleMap
    }

    /**
     * bound the map within available vehicleMarkers
     * */
    private fun setLatLngInBound(vehicleMarkers: List<VehicleMarker>) {
        vehicleMarkers.forEach {
            bounds.include(it.latLng)
        }
        updateCamera()
    }

    /**
     * try to collect selectedVehicle from [VehicleListViewModel] if a specific
     * vehicle is selected from [VehicleFragment]
     * */
    private suspend fun setMapToShowSelectedVehicle() {
        viewModel.selectedVehicle.collect {
            selectedVehicle = it
            it?.let {
                // zooming the camera to selected vehicle
                googleMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(it.latLng, 20f))
            }
        }
    }

    /**
     * update the camera when no vehicle is selected from [VehicleFragment]
     * */
    private fun updateCamera() {
        if(selectedVehicle == null) {
            googleMap?.animateCamera(
                CameraUpdateFactory.newLatLngBounds(
                    bounds.build(), 0
                )
            )
        }
    }

}