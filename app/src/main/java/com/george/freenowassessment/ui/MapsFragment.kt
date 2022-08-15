package com.george.freenowassessment.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.george.freenowassessment.R
import com.george.freenowassessment.data.local.Vehicle
import com.george.freenowassessment.data.remote.responses.Coordinate
import com.george.freenowassessment.databinding.FragmentMapsBinding
import com.george.freenowassessment.other.BitmapHelper
import com.george.freenowassessment.other.Constants.coordinate1
import com.george.freenowassessment.other.Constants.coordinate2
import com.george.freenowassessment.ui.vo.VehicleMarker
import com.george.freenowassessment.ui.vo.VehicleMarkerRenderer
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import com.google.maps.android.clustering.ClusterManager
import com.google.maps.android.ktx.awaitMap
import com.google.maps.android.ktx.awaitMapLoad
import kotlinx.coroutines.flow.collect

class MapsFragment : Fragment() {

    private val viewModel by activityViewModels<VehicleListViewModel>()

    private var _binding: FragmentMapsBinding? = null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMapsBinding.inflate(inflater, container, false)
        return _binding?.root
    }

    @SuppressLint("PotentialBehaviorOverride")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lifecycleScope.launchWhenCreated {
            val mapFragment: SupportMapFragment? =
                childFragmentManager.findFragmentById(R.id.map) as? SupportMapFragment
            val googleMap: GoogleMap? = mapFragment?.awaitMap()
            val bounds = LatLngBounds.builder()
            googleMap?.setInfoWindowAdapter(MarkerInfoWindowAdapter(requireContext()))
            viewModel.allVehicles.collect { vehicleMarkers ->
                googleMap?.let { map ->
                    addClusteredMarkers(map, vehicleMarkers)
                }

                vehicleMarkers.forEach {
                    bounds.include(it.latLng)
                }
                Log.e("SIZE", vehicleMarkers.size.toString())
                googleMap?.awaitMapLoad()
                googleMap?.moveCamera(
                    CameraUpdateFactory.newLatLngBounds(
                        bounds.build(), 100
                    )
                )
                viewModel.vehicleSelected.collect { selectedVehicle ->
                    googleMap?.moveCamera(
                        CameraUpdateFactory.newLatLngZoom(
                            selectedVehicle.latLng, 100f
                        )
                    )
                }
            }
        }
    }

    /**
     * Adds markers to the map with clustering support.
     */
    private fun addClusteredMarkers(googleMap: GoogleMap, vehicleMarkers: List<VehicleMarker>) {
        // Create the ClusterManager class and set the custom renderer.
        val clusterManager = ClusterManager<VehicleMarker>(requireContext(), googleMap)
        clusterManager.renderer =
            VehicleMarkerRenderer(
                requireContext(),
                googleMap,
                clusterManager
            )

        // Set custom info window adapter
        clusterManager.markerCollection.setInfoWindowAdapter(MarkerInfoWindowAdapter(requireContext()))

        // Add the places to the ClusterManager.
        clusterManager.addItems(vehicleMarkers)
        clusterManager.cluster()

        // Set ClusterManager as the OnCameraIdleListener so that it
        // can re-cluster when zooming in and out.
        googleMap.setOnCameraIdleListener {
            // When the camera stops moving, change the alpha value back to opaque.
            clusterManager.markerCollection.markers.forEach { it.alpha = 1.0f }
            clusterManager.clusterMarkerCollection.markers.forEach { it.alpha = 1.0f }
            // Call clusterManager.onCameraIdle() when the camera stops moving so that re-clustering
            // can be performed when the camera stops moving.
            clusterManager.onCameraIdle()
        }
        // When the camera starts moving, change the alpha value of the marker to translucent.
        googleMap.setOnCameraMoveStartedListener {
            clusterManager.markerCollection.markers.forEach { it.alpha = 0.3f }
            clusterManager.clusterMarkerCollection.markers.forEach { it.alpha = 0.3f }
        }
    }
}

fun Coordinate.latLng(): LatLng {
    return LatLng(latitude, longitude)
}