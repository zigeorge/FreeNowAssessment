package com.george.freenowassessment.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.*
import com.george.freenowassessment.data.local.Vehicle
import com.george.freenowassessment.other.Constants.MAX_SIZE
import com.george.freenowassessment.other.Constants.PAGE_SIZE
import com.george.freenowassessment.other.Constants.coordinate1
import com.george.freenowassessment.other.Constants.coordinate2
import com.george.freenowassessment.other.connectivity.ConnectivityObserver
import com.george.freenowassessment.other.exceptions.UnableToUpdateException
import com.george.freenowassessment.other.toVehicleMarker
import com.george.freenowassessment.repositories.VehicleListRepository
import com.george.freenowassessment.other.exceptions.ErrorState
import com.george.freenowassessment.ui.vo.SingleVehicle
import com.george.freenowassessment.ui.vo.VehicleMarker
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VehicleListViewModel @Inject constructor(
    private val repository: VehicleListRepository,
    private val connectivityObserver: ConnectivityObserver
) : ViewModel() {

    init {
        loadVehicles()
        getAllVehiclesToShowMarkerInMap()
    }

    //TODO: check for network connectivity
    private val _allVehicles = MutableSharedFlow<List<VehicleMarker>>(replay = 5)
    val allVehicles = _allVehicles.asSharedFlow()

    private val _vehicleSelected = MutableStateFlow<VehicleMarker?>(null)
    val vehicleSelected = _vehicleSelected.asStateFlow()

    private val _shouldShowError = MutableSharedFlow<ErrorState>()
    val shouldShowError = _shouldShowError.asSharedFlow()

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
        viewModelScope.launch {
            try {
                repository.loadVehicleList(coordinate1, coordinate2)
            } catch (ex: Exception) {
                when (ex) {
                    UnableToUpdateException::class.java ->
                        _shouldShowError.emit(ErrorState.unableToUpdate)
                    else -> _shouldShowError.emit(ErrorState.unableToLoad)
                }
            }
        }
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
