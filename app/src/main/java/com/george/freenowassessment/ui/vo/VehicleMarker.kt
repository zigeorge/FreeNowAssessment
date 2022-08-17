package com.george.freenowassessment.ui.vo

import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.clustering.ClusterItem

data class VehicleMarker(
    val id: Long,
    val name: String,
    val type: String,
    val latLng: LatLng,
    val address: String,
    val heading: Double,
    val state: String
): ClusterItem {
    override fun getPosition(): LatLng =
        latLng

    override fun getTitle(): String =
        name

    override fun getSnippet(): String =
        address

}