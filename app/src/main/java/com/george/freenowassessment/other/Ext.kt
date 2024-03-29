package com.george.freenowassessment.other

import android.graphics.Color
import android.location.Geocoder
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.george.freenowassessment.data.local.Vehicle
import com.george.freenowassessment.data.remote.responses.Coordinate
import com.george.freenowassessment.data.remote.responses.VehicleData
import com.george.freenowassessment.ui.vo.Address
import com.george.freenowassessment.ui.vo.SingleVehicle
import com.george.freenowassessment.ui.vo.VehicleMarker
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.io.IOException

/** function to convert Json string to [Address]*/
fun String.toAddress(): Address {
    return Gson().fromJson(this, Address::class.java)
}

/** function to convert [Any] object to Json string*/
fun Any.toJson(): String {
    return Gson().toJson(this)
}

/** function to enable [AppCompatActivity] to collect any flow using lifecycle*/
fun <T> AppCompatActivity.collectLifeCycleFlow(flow: Flow<T>, collect: suspend (T) -> Unit) {
    lifecycleScope.launch {
        repeatOnLifecycle(Lifecycle.State.STARTED) {
            flow.collectLatest(collect)
        }
    }
}

/**
 *  function to get [Address] from [Coordinate]
 * @param geocoder is a given android APi to get address from latitude and longitude
 * */
fun Coordinate.address(geocoder: Geocoder): Address {
    return try {
        val addresses = geocoder.getFromLocation(
            latitude,
            longitude, 1
        )
        Address(addresses[0].featureName, addresses[0].getAddressLine(0))
    } catch (ex: IOException) {
        Address("Unknown", "Unknown")
    } catch (ex: Exception) {
        Address("Unknown", "Unknown")
    }
}

/**
 * function to get [LatLng] from [Coordinate]
 * */
fun Coordinate.latLng(): LatLng {
    return LatLng(latitude, longitude)
}

/**
 * create [VehicleMarker] from db entity [Vehicle]
 * */
fun Vehicle.toVehicleMarker(): VehicleMarker {
    return VehicleMarker(
        vehicleId,
        address.toAddress().name,
        type,
        LatLng(latitude, longitude),
        address.toAddress().addressLine,
        heading,
        state
    )
}

/**
 * defining color corresponding to [SingleVehicle] state
 * */
fun SingleVehicle.stateColor(): Int {
    return if (state == "ACTIVE") {
        Color.parseColor("#008577")
    } else Color.parseColor("#D81054")
}

/**
 * add [VehicleMarker] in [GoogleMap]
 * @param vehicleMarkers represents list of [VehicleMarker] obtained from vehicles table
 * @param markersMap contains all markers added
 * @param carIcon represents a marker icon
 * this function also check if marker already exists and remove and add the marker again to
 * show update in map
 * */
fun GoogleMap.addMarkers(
    vehicleMarkers: List<VehicleMarker>,
    markersMap: HashMap<Long, Marker?>,
    carIcon: BitmapDescriptor
): HashMap<Long, Marker?> {
    val map = HashMap<Long, Marker?>()
    vehicleMarkers.forEach {
        if(markersMap.containsKey(it.id)) {
            val aMarker = markersMap[it.id]
            aMarker?.remove()
        }
        val marker = addMarker(it.getMarker(carIcon))
        marker?.tag = it
        map[it.id] = marker
    }
    return map
}

/**
 * create [MarkerOptions] from [VehicleMarker]
 * @param carIcon represents a marker icon
 * */
fun VehicleMarker.getMarker(carIcon: BitmapDescriptor): MarkerOptions {
    return MarkerOptions().title(type)
        .position(latLng)
        .icon(carIcon)
        .rotation(heading.toFloat())
}

/**
 * create [Vehicle] from [VehicleData]
 * */
fun VehicleData.getVehicle(geocoder: Geocoder, bound: String): Vehicle {
    return Vehicle(
        id,
        coordinate.latitude,
        coordinate.longitude,
        coordinate.address(geocoder).toJson(),
        fleetType,
        state,
        bound,
        heading
    )
}