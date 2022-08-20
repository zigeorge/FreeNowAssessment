package com.george.freenowassessment.ui

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import app.cash.turbine.test
import com.george.freenowassessment.TestDispatchers
import com.george.freenowassessment.repositories.VehicleListRepositoryTest
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class VehicleListViewModelTest {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var viewModel: VehicleListViewModel

    private val dispatcher: TestCoroutineDispatcher = TestCoroutineDispatcher()

    @Before
    fun setUp() {
        viewModel = VehicleListViewModel(VehicleListRepositoryTest())
        Dispatchers.setMain(dispatcher)
    }

    @After
    fun tearDownDispatcher() {
        Dispatchers.resetMain()
        dispatcher.cleanupTestCoroutines()
    }

    @Test
    fun `vehicle makers sharedFlow has data on init`() = runBlocking {
        val job = launch {
            viewModel.allVehicles.test {
                val vehicleMakers = awaitItem()
                assertThat(vehicleMakers.size).isEqualTo(100)
                cancelAndConsumeRemainingEvents()
            }
        }
        viewModel.loadVehicles()
        job.join()
        job.cancel()

    }
}