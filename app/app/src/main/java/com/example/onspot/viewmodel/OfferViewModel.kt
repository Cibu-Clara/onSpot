package com.example.onspot.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.onspot.data.model.Marker
import com.example.onspot.data.model.ParkingSpot
import com.example.onspot.data.model.User
import com.example.onspot.data.repository.MarkerRepository
import com.example.onspot.data.repository.MarkerRepositoryImpl
import com.example.onspot.data.repository.ParkingSpotRepository
import com.example.onspot.data.repository.ParkingSpotRepositoryImpl
import com.example.onspot.ui.states.AddMarkerState
import com.example.onspot.ui.states.AddParkingSpotState
import com.example.onspot.utils.Resource
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class OfferViewModel : ViewModel() {
    private val parkingSpotRepository: ParkingSpotRepository = ParkingSpotRepositoryImpl()
    private val markerRepository: MarkerRepository = MarkerRepositoryImpl()

    private val _parkingSpots = MutableStateFlow<Resource<List<ParkingSpot>>>(Resource.Loading())
    val parkingSpots: StateFlow<Resource<List<ParkingSpot>>> = _parkingSpots.asStateFlow()

    private val _addMarkerState = Channel<AddMarkerState>()
    val addMarkerState = _addMarkerState.receiveAsFlow()

    private val _markerData = MutableLiveData<Marker>()
    val markerData: LiveData<Marker> = _markerData

    private var tempMarker = Marker()

    init {
        fetchParkingSpots()
    }

    private fun fetchParkingSpots() = viewModelScope.launch {
        parkingSpotRepository.getParkingSpots().collect { parkingSpotsResource ->
            _parkingSpots.value = parkingSpotsResource
        }
    }

    fun createMarker(markerId: String, startDate: String, startTime: String, endDate: String, endTime: String, parkingSpotId: String) {
        tempMarker = tempMarker.copy(uuid = markerId, startDate = startDate, startTime = startTime, endDate = endDate, endTime = endTime, parkingSpotId = parkingSpotId)
    }

    fun finalizeMarker(latitude: Double, longitude: Double) = viewModelScope.launch {
        tempMarker = tempMarker.copy(latitude = latitude, longitude = longitude)
        _markerData.value = tempMarker

        markerRepository.createMarker(_markerData.value!!).collect { result ->
            when(result) {
                is Resource.Loading -> { _addMarkerState.send(AddMarkerState(isLoading = true)) }
                is Resource.Success -> { _addMarkerState.send(AddMarkerState(isSuccess = "Marker successfully created")) }
                is Resource.Error -> { _addMarkerState.send(AddMarkerState(isError = result.message)) }
            }
        }
    }
}