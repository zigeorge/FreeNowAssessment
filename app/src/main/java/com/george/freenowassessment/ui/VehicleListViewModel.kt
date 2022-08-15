package com.george.freenowassessment.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.*
import com.george.freenowassessment.data.local.Vehicle
import com.george.freenowassessment.other.Constants.MAX_SIZE
import com.george.freenowassessment.other.Constants.PAGE_SIZE
import com.george.freenowassessment.other.Constants.coordinate1
import com.george.freenowassessment.other.Constants.coordinate2
import com.george.freenowassessment.repositories.VehicleListRepository
import com.george.freenowassessment.ui.vo.SingleVehicle
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VehicleListViewModel @Inject constructor(
    private val repository: VehicleListRepository
): ViewModel() {

    private val _allVehicles = MutableSharedFlow<List<Vehicle>>()
    val allVehicles = _allVehicles.asSharedFlow()

    val vehicleList: Flow<PagingData<SingleVehicle>> = Pager(
        config = PagingConfig(
            pageSize = PAGE_SIZE,
            enablePlaceholders = true,
            maxSize = MAX_SIZE
        )
    ) {
        repository.getVehicleList(coordinate1, coordinate2)
    }.flow.map { pagingData ->
        pagingData
            // Map cheeses to common UI model.
            .map { vehicle ->
                SingleVehicle(vehicle)
            }
    }
        .cachedIn(viewModelScope)

    fun loadVehicles() {
        viewModelScope.launch {
            repository.loadVehicleList(coordinate1, coordinate2)
        }
    }

    fun getAllVehiclesToShowMarkerInMap() {
        viewModelScope.launch {
            _allVehicles.emitAll(repository.getAllVehicle(coordinate1, coordinate2))
        }
    }

}