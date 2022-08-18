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

class MapsFragment : Fragment() {

    private val viewModel by activityViewModels<VehicleListViewModel>()

    private var _binding: FragmentMapsBinding? = null

    private var googleMap: GoogleMap? = null

    private var bounds = LatLngBounds.Builder()

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
            setAllVehiclesInMap()
        }
        lifecycleScope.launchWhenResumed {
            setMapToShowSelectedVehicle()
        }
    }

    private suspend fun setAllVehiclesInMap() {
        viewModel.allVehicles.collectLatest { vehicleMarkers ->
            googleMap?.addMarkers(vehicleMarkers, carIcon)
            setLatLngInBound(vehicleMarkers)
        }
    }

    @SuppressLint("PotentialBehaviorOverride")
    private suspend fun configureGoogleMap(): GoogleMap? {
        val mapFragment: SupportMapFragment? =
            childFragmentManager.findFragmentById(R.id.map) as? SupportMapFragment
        googleMap = mapFragment?.awaitMap()
        googleMap?.awaitMapLoad()
        googleMap?.setInfoWindowAdapter(MarkerInfoWindowAdapter(requireContext()))
        return googleMap
    }

    private fun setLatLngInBound(vehicleMarkers: List<VehicleMarker>) {
        vehicleMarkers.forEach {
            bounds.include(it.latLng)
        }
    }

    private suspend fun setMapToShowSelectedVehicle() {
        viewModel.vehicleSelected.collect {
            it?.let {
                // zooming the camera to selected vehicle
                googleMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(it.latLng, 20f))
            } ?: run {
                googleMap?.animateCamera(
                    CameraUpdateFactory.newLatLngBounds(
                        bounds.build(), 0
                    )
                )
            }
        }
    }

}