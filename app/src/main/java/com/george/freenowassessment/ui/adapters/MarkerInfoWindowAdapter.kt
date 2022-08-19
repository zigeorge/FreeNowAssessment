package com.george.freenowassessment.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import com.george.freenowassessment.databinding.MarkerInfoContentsBinding
import com.george.freenowassessment.ui.vo.VehicleMarker
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker



/**
 * this adapter helps googleMap markers to show contents from [VehicleMarker]
 * */
class MarkerInfoWindowAdapter(
    private val context: Context
) : GoogleMap.InfoWindowAdapter {

    override fun getInfoContents(marker: Marker): View? {
        val vehicleMarker = marker.tag as? VehicleMarker ?: return null

        val binding = MarkerInfoContentsBinding.inflate(LayoutInflater.from(context))

        binding.type.text = vehicleMarker.type
        binding.address.text = vehicleMarker.address
        binding.state.text = vehicleMarker.state

        return binding.root
    }

    override fun getInfoWindow(marker: Marker): View? {
        return null
    }
}