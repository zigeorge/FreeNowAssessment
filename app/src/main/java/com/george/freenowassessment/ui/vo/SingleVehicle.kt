package com.george.freenowassessment.ui.vo

import android.location.Geocoder
import com.george.freenowassessment.data.local.Vehicle
import com.george.freenowassessment.data.remote.responses.Coordinate
import com.george.freenowassessment.other.Constants.coordinate1
import java.util.ArrayList
import javax.inject.Inject

/**
 * [SingleVehicle] representing information of a [Vehicle]
 */
class SingleVehicle(val vehicle: Vehicle) {
    val type: String = vehicle.type
    val state: String = vehicle.state
    val address: String = vehicle.address


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