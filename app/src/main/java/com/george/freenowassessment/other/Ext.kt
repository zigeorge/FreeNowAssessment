package com.george.freenowassessment.other

import android.content.DialogInterface
import android.graphics.Color
import android.location.Geocoder
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.george.freenowassessment.data.local.Vehicle
import com.george.freenowassessment.data.remote.responses.Coordinate
import com.george.freenowassessment.ui.vo.Address
import com.george.freenowassessment.ui.vo.SingleVehicle
import com.george.freenowassessment.ui.vo.VehicleMarker
import com.google.android.gms.maps.model.LatLng
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

/** function to get [Address] from [Coordinate]
 * @param geocoder is a given android APi to get address from latitude and longitude */
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

fun Coordinate.latLng(): LatLng {
    return LatLng(latitude, longitude)
}

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

fun AppCompatActivity.showDialog(
    message: String,
    positiveText: String,
    negativeText: String,
    positiveAction: (DialogInterface, Int) -> Unit,
    negativeAction: (DialogInterface, Int) -> Unit
) {
    val alertDialogBuilder = AlertDialog.Builder(this)
    alertDialogBuilder.setMessage(message)
    alertDialogBuilder.setPositiveButton(positiveText, positiveAction)
    alertDialogBuilder.setNegativeButton(negativeText, negativeAction)
    alertDialogBuilder.create().show()
}

fun SingleVehicle.stateColor(): Int {
    return if (state == "ACTIVE") {
        Color.parseColor("#008577")
    } else Color.parseColor("#D81054")
}