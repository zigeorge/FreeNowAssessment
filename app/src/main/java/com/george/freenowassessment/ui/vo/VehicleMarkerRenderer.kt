package com.george.freenowassessment.ui.vo

import android.content.Context
import androidx.core.content.ContextCompat
import com.george.freenowassessment.R
import com.george.freenowassessment.other.BitmapHelper
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.maps.android.clustering.ClusterManager
import com.google.maps.android.clustering.view.DefaultClusterRenderer

class VehicleMarkerRenderer(
    private val context: Context,
    map: GoogleMap,
    clusterManager: ClusterManager<VehicleMarker>
) : DefaultClusterRenderer<VehicleMarker>(context, map, clusterManager) {

    /**
     * The icon to use for each cluster item
     */
    private val carIcon: BitmapDescriptor by lazy {
        val color = ContextCompat.getColor(context, R.color.colorPrimary)
        BitmapHelper.vectorToBitmap(context, R.drawable.ic_car_marker, color)
    }

    /**
     * Method called before the cluster item (the marker) is rendered.
     * This is where marker options should be set.
     */
    override fun onBeforeClusterItemRendered(
        item: VehicleMarker,
        markerOptions: MarkerOptions
    ) {
        val marker = markerOptions.title(item.type)
            .position(item.latLng)
            .icon(carIcon)
        marker.rotation(item.heading.toFloat())
    }

    /**
     * Method called right after the cluster item (the marker) is rendered.
     * This is where properties for the Marker object should be set.
     */
    override fun onClusterItemRendered(clusterItem: VehicleMarker, marker: Marker) {
        marker.tag = clusterItem
    }
}