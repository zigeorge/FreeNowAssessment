package com.george.freenowassessment.ui.vo

import com.george.freenowassessment.data.local.Vehicle
import com.george.freenowassessment.other.toAddress

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
}