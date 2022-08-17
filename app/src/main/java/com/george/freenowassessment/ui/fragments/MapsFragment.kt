package com.george.freenowassessment.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.george.freenowassessment.R
import com.george.freenowassessment.databinding.FragmentMapsBinding
import com.george.freenowassessment.other.Constants.coordinate1
import com.george.freenowassessment.other.Constants.coordinate2
import com.george.freenowassessment.other.latLng
import com.george.freenowassessment.ui.VehicleListViewModel
import com.george.freenowassessment.ui.adapters.MarkerInfoWindowAdapter
import com.george.freenowassessment.ui.vo.VehicleMarker
import com.george.freenowassessment.ui.vo.VehicleMarkerRenderer
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.android.clustering.ClusterManager
import com.google.maps.android.ktx.awaitMap
import com.google.maps.android.ktx.awaitMapLoad
import kotlinx.coroutines.flow.collectLatest

class MapsFragment : Fragment() {

    private val viewModel by activityViewModels<VehicleListViewModel>()

    private var _binding: FragmentMapsBinding? = null

    private var googleMap: GoogleMap? = null

    private var selectedVehicle: VehicleMarker? = null


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
            setMarkersForAllVehicles()
        }
        lifecycleScope.launchWhenResumed {
            setMapToShowSelectedVehicle()
        }
    }

    private suspend fun configureGoogleMap(): GoogleMap? {
        val mapFragment: SupportMapFragment? =
            childFragmentManager.findFragmentById(R.id.map) as? SupportMapFragment
        googleMap = mapFragment?.awaitMap()
        googleMap?.awaitMapLoad()
        return googleMap
    }

    private fun setLatLngBound() {
        lifecycleScope.launchWhenResumed {
            val bounds = LatLngBounds.builder()
            bounds.include(coordinate1.latLng())
            bounds.include(coordinate2.latLng())
            googleMap?.animateCamera(
                CameraUpdateFactory.newLatLngBounds(
                    bounds.build(), 10
                )
            )
        }
    }

    private suspend fun setMarkersForAllVehicles() {
        // Zooming the map to a bound then selected bound before clustering
        setLatLngBound()
        viewModel.allVehicles.collectLatest { vehicleMarkers ->
            addClusteredMarkers(vehicleMarkers)
        }
    }

    private suspend fun setMapToShowSelectedVehicle() {
        viewModel.vehicleSelected.collect {
            it?.let {
                selectedVehicle = it
            }
        }
    }

    /**
     * Adds markers to the map with clustering support.
     */
    private fun addClusteredMarkers(vehicleMarkers: List<VehicleMarker>) {
        // Create the ClusterManager class and set the custom renderer.
        val clusterManager = ClusterManager<VehicleMarker>(requireContext(), googleMap)
        googleMap?.let { map ->
            clusterManager.renderer =
                VehicleMarkerRenderer(
                    requireContext(),
                    map,
                    clusterManager
                )

            // Set custom info window adapter
            clusterManager.markerCollection.setInfoWindowAdapter(
                MarkerInfoWindowAdapter(
                    requireContext()
                )
            )

            // Add the places to the ClusterManager.
            clusterManager.addItems(vehicleMarkers)
            clusterManager.cluster()

            // Set ClusterManager as the OnCameraIdleListener so that it
            // can re-cluster when zooming in and out.
            map.setOnCameraIdleListener {
                // When the camera stops moving, change the alpha value back to opaque.
                clusterManager.markerCollection.markers.forEach { it.alpha = 1.0f }
                clusterManager.clusterMarkerCollection.markers.forEach { it.alpha = 1.0f }
                // Call clusterManager.onCameraIdle() when the camera stops moving so that re-clustering
                // can be performed when the camera stops moving.
                clusterManager.onCameraIdle()
            }
            // When the camera starts moving, change the alpha value of the marker to translucent.
            map.setOnCameraMoveStartedListener {
                clusterManager.markerCollection.markers.forEach { it.alpha = 0.3f }
                clusterManager.clusterMarkerCollection.markers.forEach { it.alpha = 0.3f }
            }
            selectedVehicle?.let { selectedVehicle->
                vehicleMarkers.forEach {
                    if (it.id == selectedVehicle.id) {
                        map.animateCamera(CameraUpdateFactory.newLatLngZoom(it.latLng, 20f))
                    }
                }
            }
        }
    }
}