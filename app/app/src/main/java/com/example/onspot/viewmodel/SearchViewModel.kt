package com.example.onspot.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.onspot.data.model.Marker
import com.example.onspot.data.model.ParkingSpot
import com.example.onspot.data.model.Vehicle
import com.example.onspot.data.repository.MarkerRepository
import com.example.onspot.data.repository.MarkerRepositoryImpl
import com.example.onspot.data.repository.ParkingSpotRepository
import com.example.onspot.data.repository.ParkingSpotRepositoryImpl
import com.example.onspot.data.repository.VehicleRepository
import com.example.onspot.data.repository.VehicleRepositoryImpl
import com.example.onspot.utils.Resource
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.PlacesClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class SearchViewModel : ViewModel() {
    private val vehicleRepository: VehicleRepository = VehicleRepositoryImpl()
    private val markerRepository: MarkerRepository = MarkerRepositoryImpl()

    private val _vehicles = MutableStateFlow<Resource<List<Vehicle>>>(Resource.Loading())
    val vehicles: StateFlow<Resource<List<Vehicle>>> = _vehicles.asStateFlow()

    private val _markers = MutableStateFlow<Resource<List<Marker>>>(Resource.Loading())
    val markers: StateFlow<Resource<List<Marker>>> = _markers.asStateFlow()

    private val _suggestions = MutableStateFlow<List<AutocompletePrediction>>(emptyList())
    val suggestions: StateFlow<List<AutocompletePrediction>> = _suggestions.asStateFlow()

    init {
        fetchMarkers()
        fetchVehicles()
    }

    private fun fetchMarkers() = viewModelScope.launch {
        markerRepository.getAllMarkers().collect { markersResource ->
            _markers.value = markersResource
        }
    }

    private fun fetchVehicles() = viewModelScope.launch {
        vehicleRepository.getVehicles().collect { vehiclesResource ->
            _vehicles.value = vehiclesResource
        }
    }

    fun fetchPlaces(query: String, placesClient: PlacesClient) = viewModelScope.launch {
        val request = FindAutocompletePredictionsRequest.builder()
            .setQuery(query)
            .build()
        try {
            val response = placesClient.findAutocompletePredictions(request).await()
            _suggestions.value = response.autocompletePredictions ?: emptyList()
        } catch (e: Exception) {
            _suggestions.value = emptyList()
        }
    }

    suspend fun getPlaceLatLng(placeId: String, placesClient: PlacesClient): LatLng {
        val request = FetchPlaceRequest.builder(placeId, listOf(Place.Field.LAT_LNG)).build()
        val response = placesClient.fetchPlace(request).await()
        return response.place.latLng ?: LatLng(0.0, 0.0)
    }
}