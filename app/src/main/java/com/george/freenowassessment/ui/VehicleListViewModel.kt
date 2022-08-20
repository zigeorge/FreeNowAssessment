package com.george.freenowassessment.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.*
import com.george.freenowassessment.data.local.Vehicle
import com.george.freenowassessment.other.Constants.MAX_SIZE
import com.george.freenowassessment.other.Constants.PAGE_SIZE
import com.george.freenowassessment.other.Constants.coordinate1
import com.george.freenowassessment.other.Constants.coordinate2
import com.george.freenowassessment.other.DispatcherProvider
import com.george.freenowassessment.other.toVehicleMarker
import com.george.freenowassessment.repositories.VehicleListRepository
import com.george.freenowassessment.ui.vo.SingleVehicle
import com.george.freenowassessment.ui.vo.VehicleMarker
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VehicleListViewModel @Inject constructor(
    private val repository: VehicleListRepository
) : ViewModel() {

    init {
        getAllVehiclesToShowMarkerInMap()
    }

    private val _allVehicles = MutableSharedFlow<List<VehicleMarker>>(replay = 5)
    val allVehicles = _allVehicles.asSharedFlow()

    private val _vehicleSelected = MutableStateFlow<VehicleMarker?>(null)
    val vehicleSelected = _vehicleSelected.asStateFlow()

    val vehicleList: SharedFlow<PagingData<SingleVehicle>> = Pager(
        config = PagingConfig(
            pageSize = PAGE_SIZE,
            enablePlaceholders = true,
            maxSize = MAX_SIZE
        )
    ) {
        repository.getVehicleList(coordinate1, coordinate2)
    }.flow.map { pagingData ->
        pagingData
            /** Map [Vehicle] to [SingleVehicle].*/
            .map { vehicle ->
                SingleVehicle(vehicle)
            }
    }
        .cachedIn(viewModelScope) as SharedFlow<PagingData<SingleVehicle>>


    fun loadVehicles() {
        repository.loadVehicleList(coordinate1, coordinate2)
    }

    private fun getAllVehiclesToShowMarkerInMap() {
        viewModelScope.launch {
            repository.getAllVehicle(coordinate1, coordinate2).collect {
                val vehicleMarkers = ArrayList<VehicleMarker>()
                it.forEach { vehicle ->
                    vehicleMarkers.add(vehicle.toVehicleMarker())
                }
                _allVehicles.emit(vehicleMarkers)
            }
        }
    }

    fun onVehicleSelected(singleVehicle: SingleVehicle) {
        viewModelScope.launch {
            singleVehicle.let {
                _vehicleSelected.value = it.vehicle.toVehicleMarker()
            }
        }
    }

    fun removeVehicleSelection() {
        _vehicleSelected.value = null
    }
}
