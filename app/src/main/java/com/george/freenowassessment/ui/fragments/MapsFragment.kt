package com.george.freenowassessment.ui.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
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
import com.george.freenowassessment.other.Constants.coordinate1
import com.george.freenowassessment.other.Constants.coordinate2
import com.george.freenowassessment.other.addMarkers
import com.george.freenowassessment.other.latLng
import com.george.freenowassessment.ui.VehicleListViewModel
import com.george.freenowassessment.ui.adapters.MarkerInfoWindowAdapter
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.Marker
import com.google.maps.android.ktx.awaitMap
import com.google.maps.android.ktx.awaitMapLoad
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MapsFragment : Fragment() {

    private val viewModel by activityViewModels<VehicleListViewModel>()

    private var _binding: FragmentMapsBinding? = null

    private var googleMap: GoogleMap? = null

    private val markersMap = HashMap<Long, Marker?>()

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
                setMapToShowSelectedVehicles()
            }
            launch(Dispatchers.IO) {
                checkMarkersIfExists()
            }
        }
    }

    /**
     * collect allVehicles from [VehicleListViewModel] and show them in [GoogleMap]
     * */
    private suspend fun setAllVehiclesInMap() {
        viewModel.allVehicles.collectLatest { vehicleMarkers ->
            if(vehicleMarkers.isEmpty()) {
                googleMap?.clear()
                markersMap.clear()
            } else {
                Log.e("SIZE-MARKERS", markersMap.size.toString())
                googleMap?.addMarkers(vehicleMarkers, markersMap, carIcon)
                    ?.let { markersMap.putAll(it) }
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
    private fun setLatLngInBound() {
        val bounds = LatLngBounds.builder()
        bounds.include(coordinate1.latLng())
        bounds.include(coordinate2.latLng())
        googleMap?.animateCamera(
            CameraUpdateFactory.newLatLngZoom(
                bounds.build().center, 11.5f
            )
        )
    }

    /**
     * try to collect selectedVehicle from [VehicleListViewModel] if a specific
     * vehicle is selected from [VehicleFragment]
     * */
    private suspend fun setMapToShowSelectedVehicles() {
        viewModel.vehicleSelected.collect {
            it?.let {
                // zooming the camera to selected vehicle
                googleMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(it.latLng, 20f))
            } ?: run {
                setLatLngInBound()
            }
        }
    }

    /**
     * a function to check if @property markersMap contains valid markers
    * */
    private suspend fun checkMarkersIfExists() {
        while (true) {
            delay(2000)
            val removables = viewModel.getRemovableMarkers(markersMap.keys)
            lifecycleScope.launch(Dispatchers.Main) {
                removables.forEach {
                    markersMap[it]?.remove()
                    markersMap.remove(it)
                }
            }
        }
    }

}