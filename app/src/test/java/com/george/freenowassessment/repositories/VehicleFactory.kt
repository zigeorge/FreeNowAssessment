package com.george.freenowassessment.repositories

import com.george.freenowassessment.data.local.Vehicle
import com.google.common.util.concurrent.AtomicDouble
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicLong

class VehicleFactory {
    private val counter = AtomicInteger(0)
    private val vehicleCounter = AtomicLong(-1074560513)
    private val latCounter = AtomicDouble(53.69486514253647)
    private val lngCounter = AtomicDouble(9.75758975758913)
    private val headingCounter = AtomicDouble(0.75758975758913)

    private val delta = 0.05
    private val headingDelta = 3.000222006

    private fun createVehicle(): Vehicle {
        val id = counter.incrementAndGet()
        val vehicleId = vehicleCounter.incrementAndGet()
        val latitude = latCounter.addAndGet(delta)
        val longitude = lngCounter.addAndGet(delta)
        val heading = headingCounter.addAndGet(headingDelta)
        return Vehicle(
            vehicleId,
            latitude,
            longitude,
            "Hamburg",
            "TAXI",
            "ACTIVE",
            "53.694865_9.757589_53.394655_10.099891",
            heading,
            id
        )
    }

    fun fakeVehicles(): List<Vehicle> {
        val list = ArrayList<Vehicle>()
        repeat(100) {
            list.add(createVehicle())
        }
        return list
    }
}