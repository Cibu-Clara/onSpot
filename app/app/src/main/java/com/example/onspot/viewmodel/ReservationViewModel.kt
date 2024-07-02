package com.example.onspot.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.onspot.data.model.ListingDetails
import com.example.onspot.data.model.RequestDetails
import com.example.onspot.data.model.Reservation
import com.example.onspot.data.model.ReservationDetails
import com.example.onspot.data.repository.ReservationRepository
import com.example.onspot.data.repository.ReservationRepositoryImpl
import com.example.onspot.ui.states.AddReservationState
import com.example.onspot.ui.states.DeleteReservationState
import com.example.onspot.utils.Resource
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class ReservationViewModel : ViewModel() {
    private val reservationRepository : ReservationRepository = ReservationRepositoryImpl()

    private val _addReservationState = Channel<AddReservationState>()
    val addReservationState = _addReservationState.receiveAsFlow()

    private val _deleteReservationState = Channel<DeleteReservationState>()
    val deleteReservationState = _deleteReservationState.receiveAsFlow()

    private val _reservationsDetails = MutableStateFlow<Resource<List<ReservationDetails>>>(Resource.Loading())
    val reservationsDetails: StateFlow<Resource<List<ReservationDetails>>> = _reservationsDetails.asStateFlow()

    private val _listingsDetails = MutableStateFlow<Resource<List<ListingDetails>>>(Resource.Loading())
    val listingsDetails: StateFlow<Resource<List<ListingDetails>>> = _listingsDetails.asStateFlow()

    private val _requestsDetails = MutableStateFlow<Resource<List<RequestDetails>>>(Resource.Loading())
    val requestsDetails: StateFlow<Resource<List<RequestDetails>>> = _requestsDetails.asStateFlow()

    private val _reservations = MutableStateFlow<Resource<List<Reservation>>>(Resource.Loading())
    val reservations: StateFlow<Resource<List<Reservation>>> = _reservations.asStateFlow()

    private val _selectedRequest = MutableStateFlow<RequestDetails?>(null)
    val selectedRequest: StateFlow<RequestDetails?> = _selectedRequest.asStateFlow()

    private val _hasReviewed = MutableStateFlow<Boolean?>(null)
    val hasReviewed: StateFlow<Boolean?> = _hasReviewed.asStateFlow()

    init {
        fetchReservations()
        fetchReservationsWithDetails()
        fetchListingsWithDetails()
    }

    private fun fetchReservationsWithDetails() = viewModelScope.launch {
        reservationRepository.getReservationsCurrentUser().collect { resource ->
            _reservationsDetails.value = resource
        }
    }

    private fun fetchListingsWithDetails() = viewModelScope.launch {
        reservationRepository.getListingsCurrentUser().collect { resource ->
            _listingsDetails.value = resource
        }
    }

    fun fetchRequestsWithDetails(markerId: String) = viewModelScope.launch {
        reservationRepository.getRequests(markerId).collect { resource ->
            _requestsDetails.value = resource
        }
    }

    private fun fetchReservations() = viewModelScope.launch {
        reservationRepository.getAllReservations().collect { reservationsResource ->
            _reservations.value = reservationsResource
        }
    }

    fun selectRequest(request: RequestDetails) {
        _selectedRequest.value = request
    }

//    fun rejectRequest(requestId: String) = viewModelScope.launch {
//        reservationRepository.updateRequestStatus(requestId, "rejected").collect()
//        fetchRequestsWithDetails(requestId) // Refresh requests after rejection
//    }
//
//    fun acceptRequest(requestId: String, markerId: String) = viewModelScope.launch {
//        reservationRepository.updateRequestStatus(requestId, "accepted").collect()
//        // Update the marker to reflect the reservation
//        reservationRepository.updateMarkerReserved(markerId, true).collect()
//        // Refresh requests and listings
//        fetchRequestsWithDetails(markerId)
//        fetchListingsWithDetails()
//    }

    fun addReservation(id: String, status: String, startDate: String, startTime: String, endDate: String, endTime: String, userId: String, markerId: String, parkingSpotId: String, vehicleId: String)
    = viewModelScope.launch {
        val reservation = Reservation(
            uuid = id,
            status = status,
            startDate = startDate,
            startTime = startTime,
            endDate = endDate,
            endTime = endTime,
            userId = userId,
            markerId = markerId,
            parkingSpotId = parkingSpotId,
            vehicleId = vehicleId
        )
        reservationRepository.createReservation(reservation).collect { result ->
            when(result) {
                is Resource.Loading -> { _addReservationState.send(AddReservationState(isLoading = true)) }
                is Resource.Success -> {
                    _addReservationState.send(AddReservationState(isSuccess = "Reservation successfully requested"))
                    fetchReservations()
                }
                is Resource.Error -> { _addReservationState.send(AddReservationState(isError = result.message)) }
            }
        }
    }

    fun checkAlreadyReviewed(reservationId: String) = viewModelScope.launch {
        _hasReviewed.value = reservationRepository.checkAlreadyReviewed(reservationId)
    }

    fun deleteReservation(id: String) = viewModelScope.launch {
        reservationRepository.deleteReservation(id).collect { result ->
            when(result) {
                is Resource.Loading -> { _deleteReservationState.send(DeleteReservationState(isLoading = true)) }
                is Resource.Success -> {
                    _deleteReservationState.send(DeleteReservationState(isSuccess = "Reservation canceled successfully"))
                    fetchReservationsWithDetails()
                }
                is Resource.Error -> { _deleteReservationState.send(DeleteReservationState(isError = result.message)) }
            }
        }
    }
}