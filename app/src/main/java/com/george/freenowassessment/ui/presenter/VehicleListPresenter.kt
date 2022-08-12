package com.george.freenowassessment.ui.presenter

import android.location.Geocoder
import com.george.freenowassessment.data.remote.responses.Vehicle
import java.util.ArrayList
import java.util.HashMap
import javax.inject.Inject

/** [VehicleListPresenter] representing available [Vehicle] from server */
class VehicleListPresenter(vehicles: List<Vehicle>) {

    /**
     * list of [SingleVehicle]
     */
    val list: MutableList<SingleVehicle> = ArrayList()

    init {
        // Add vehicles in list
        for (vehicle in vehicles) {
            addVehicle(SingleVehicle(vehicle))
        }
    }

    private fun addVehicle(vehicle: SingleVehicle) {
        list.add(vehicle)
    }

    /**
     * [SingleVehicle] representing information of a [Vehicle]
     */
    class SingleVehicle(val vehicle: Vehicle) {
        var type: String = vehicle.fleetType

        @Inject lateinit var geocoder: Geocoder

        val location: String
        get() {
            val addresses = geocoder.getFromLocation(vehicle.coordinate.latitude,
                vehicle.coordinate.longitude, 1)
            return "${addresses[0].getAddressLine(0)}, " +
                    "${addresses[0].locality}, ${addresses[0].adminArea}, " +
                    "${addresses[0].countryName}, ${addresses[0].postalCode}, " +
                    addresses[0].featureName
        }

        /*fun Geocoder.getAddress(): String {
            val addresses = this.getFromLocation(vehicle.coordinate.latitude,
                vehicle.coordinate.longitude, 1)
            return "${addresses[0].getAddressLine(0)}, " +
                    "${addresses[0].locality}, ${addresses[0].adminArea}, " +
                    "${addresses[0].countryName}, ${addresses[0].postalCode}, " +
                    addresses[0].featureName
        }*/

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
}