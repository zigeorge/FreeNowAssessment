package com.george.freenowassessment.ui.vo

import android.location.Geocoder
import com.george.freenowassessment.data.local.Vehicle
import com.george.freenowassessment.data.remote.responses.Coordinate
import com.george.freenowassessment.other.Constants.coordinate1
import com.google.gson.Gson
import java.util.ArrayList
import javax.inject.Inject

/**
 * [SingleVehicle] representing information of a [Vehicle]
 */
class SingleVehicle(val vehicle: Vehicle) {
    val type: String = vehicle.type
    val state: String = vehicle.state
    val address: String = vehicle.address.toAddress().addressLine


    /** matches an [other] object of [Any] type with [SingleVehicle] */
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other?.javaClass != javaClass) return false
        return other.hashCode() == hashCode()
    }

    override fun hashCode(): Int {
        return vehicle.hashCode()
    }

}

data class Address(val name: String, val addressLine: String) {

    override fun toString(): String {
        return "$name $addressLine"
    }

    fun toJson(): String {
        return Gson().toJson(this)
    }
}

fun String.toAddress(): Address {
    return Gson().fromJson(this, Address::class.java)
}