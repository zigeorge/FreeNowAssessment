package com.george.freenowassessment.repositories

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.george.freenowassessment.other.Constants.coordinate1
import com.george.freenowassessment.other.Constants.coordinate2
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class VehicleDataStoreWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val vehicleDataSource: VehicleDataSource
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return try {
            vehicleDataSource.storeData(coordinate1, coordinate2)
            Result.success()
        } catch (ex: Exception) {
            Result.retry()
        }

    }

}