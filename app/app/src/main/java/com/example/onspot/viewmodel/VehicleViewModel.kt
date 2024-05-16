package com.example.onspot.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.onspot.data.model.Vehicle
import com.example.onspot.data.repository.VehicleRepository
import com.example.onspot.data.repository.VehicleRepositoryImpl
import com.example.onspot.ui.states.AddVehicleState
import com.example.onspot.ui.states.DeleteVehicleState
import com.example.onspot.utils.Resource
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class VehicleViewModel : ViewModel() {
    private val vehicleRepository: VehicleRepository = VehicleRepositoryImpl()

    private val _vehicleDetails = MutableStateFlow<Resource<Vehicle>>(Resource.Loading())
    val vehicleDetails: StateFlow<Resource<Vehicle>> = _vehicleDetails.asStateFlow()

    private val _addVehicleState = Channel<AddVehicleState>()
    val addVehicleState = _addVehicleState.receiveAsFlow()

    private val _deleteVehicleState = Channel<DeleteVehicleState>()
    val deleteVehicleState = _deleteVehicleState.receiveAsFlow()

    fun fetchVehicleDetails(vehicleId: String) = viewModelScope.launch {
        vehicleRepository.getVehicleById(vehicleId).collect { vehicleDetailsResource ->
            _vehicleDetails.value = vehicleDetailsResource
        }
    }

    fun addVehicle(id: String, licensePlate: String, country: String, make: String, model: String, year: Int, color: String) = viewModelScope.launch {
        val vehicle = Vehicle(
            uuid = id,
            licensePlate = licensePlate,
            country = country,
            make = make,
            model = model,
            year = year,
            color = color,
            userId = ""
        )
        vehicleRepository.addVehicle(vehicle).collect { result ->
            when(result) {
                is Resource.Loading -> { _addVehicleState.send(AddVehicleState(isLoading = true)) }
                is Resource.Success -> { _addVehicleState.send(AddVehicleState(isSuccess = "Vehicle successfully added")) }
                is Resource.Error -> { _addVehicleState.send(AddVehicleState(isError = result.message)) }
            }
        }
    }

    fun deleteVehicle(id: String) = viewModelScope.launch {
        vehicleRepository.deleteVehicle(id).collect { result ->
            when(result) {
                is Resource.Loading -> { _deleteVehicleState.send(DeleteVehicleState(isLoading = true)) }
                is Resource.Success -> { _deleteVehicleState.send(DeleteVehicleState(isSuccess = "Vehicle successfully deleted")) }
                is Resource.Error -> { _deleteVehicleState.send(DeleteVehicleState(isError = result.message)) }
            }
        }
    }
}