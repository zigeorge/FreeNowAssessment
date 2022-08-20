package com.george.freenowassessment.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.filters.SmallTest
import app.cash.turbine.test
import com.george.freenowassessment.VehicleFactoryAndroidTest
import com.george.freenowassessment.VehicleFactoryAndroidTest.Companion.BOUND
import com.google.common.truth.Truth.assertThat
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject
import javax.inject.Named

@ExperimentalCoroutinesApi
@SmallTest
@HiltAndroidTest
class VehicleDaoTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Inject
    @Named("test_vehicle_db")
    lateinit var database: VehicleDB
    private lateinit var dao: VehicleDao

    private lateinit var vehicleFactory: VehicleFactoryAndroidTest

    @Before
    fun setUp() {
        hiltRule.inject()
        dao = database.vehicleDao()
        vehicleFactory = VehicleFactoryAndroidTest()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun insertVehicle() = runTest {
        val vehicle = vehicleFactory.createVehicle()

        dao.insert(vehicle)

        dao.vehiclesInFlow(vehicle.bound).test {
            val emission = awaitItem()
            assertThat(vehicle).isIn(emission)
            cancelAndConsumeRemainingEvents()
        }

    }

    @Test
    fun checkAllVehiclesAreEmittedFromDBProperly() = runTest {
        val vehicles = vehicleFactory.fakeVehicles()
        vehicles.forEach {
            dao.insert(it)
        }
        val job = launch {
            dao.vehiclesInFlow(BOUND).test {
                val emission = awaitItem()
                assertThat(emission.size).isEqualTo(100)
                cancelAndConsumeRemainingEvents()
            }
        }

        job.join()
        job.cancel()
    }

    @Test
    fun testCount() = runTest {
        val vehicles = vehicleFactory.fakeVehicles()
        vehicles.forEach {
            dao.insert(it)
        }
        val count = dao.count(BOUND)
        assertThat(count).isEqualTo(100)
    }

    @Test
    fun verifyUpdateUpdatesAVehicle() = runTest {
        val vehicle = vehicleFactory.createVehicle()
        dao.insert(vehicle)

        vehicle.type = "POOLING"
        dao.updateVehicle(vehicle)

        val result = dao.getVehicle(vehicle.vehicleId)
        assertThat(result?.type).isEqualTo("POOLING")
    }

    @Test
    fun checkIfGetVehiclesReturnsAVehicle() = runTest {
        val vehicle = vehicleFactory.createVehicle()
        dao.insert(vehicle)

        val result = dao.getVehicle(vehicle.vehicleId)
        assertThat(result).isEqualTo(vehicle)
    }

    @Test
    fun deleteVehiclesDeletesAllVehicleNotListedInGivenIds() = runTest {
        val vehicles = vehicleFactory.fakeVehicles()
        val ids = ArrayList<Long>()
        var index = 0
        vehicles.forEach {
            dao.insert(it)
            if(index < 50) {
                ids.add(it.vehicleId)
                index++
            }

        }
        dao.deleteVehicles(ids.toTypedArray())

        val count = dao.count(BOUND)

        assertThat(count).isEqualTo(50)

    }

    @Test
    fun getAllIdsReturnsAllIds() = runTest {
        val vehicles = vehicleFactory.fakeVehicles()
        vehicles.forEach {
            dao.insert(it)
        }

        val result = dao.getAllIds(BOUND)

        val count = dao.count(BOUND)

        assertThat(result.size).isEqualTo(count)
    }

}