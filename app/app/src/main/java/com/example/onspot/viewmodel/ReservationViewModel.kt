package com.example.onspot.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.onspot.data.model.ListingDetails
import com.example.onspot.data.model.RequestDetails
import com.example.onspot.data.model.Reservation
import com.example.onspot.data.model.ReservationDetails
import com.example.onspot.data.repository.MarkerRepository
import com.example.onspot.data.repository.MarkerRepositoryImpl
import com.example.onspot.data.repository.ReservationRepository
import com.example.onspot.data.repository.ReservationRepositoryImpl
import com.example.onspot.data.repository.VehicleRepository
import com.example.onspot.data.repository.VehicleRepositoryImpl
import com.example.onspot.ui.states.AcceptReservationState
import com.example.onspot.ui.states.AddReservationState
import com.example.onspot.ui.states.DeleteMarkerState
import com.example.onspot.ui.states.DeleteReservationState
import com.example.onspot.ui.states.RejectReservationState
import com.example.onspot.ui.states.ToggleMarkerReserved
import com.example.onspot.ui.states.UpdateStatusState
import com.example.onspot.utils.Resource
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.time.LocalDate

class ReservationViewModel : ViewModel() {
    private val reservationRepository : ReservationRepository = ReservationRepositoryImpl()
    private val markerRepository : MarkerRepository = MarkerRepositoryImpl()
    private val vehicleRepository : VehicleRepository = VehicleRepositoryImpl()

    private val _addReservationState = Channel<AddReservationState>()
    val addReservationState = _addReservationState.receiveAsFlow()

    private val _deleteReservationState = Channel<DeleteReservationState>()
    val deleteReservationState = _deleteReservationState.receiveAsFlow()

    private val _deleteMarkerState = Channel<DeleteMarkerState>()
    val deleteMarkerState = _deleteMarkerState.receiveAsFlow()

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

    private val _toggleMarkerReservedState = Channel<ToggleMarkerReserved>()

    private val _updateReservationStatus = Channel<UpdateStatusState>()
    val updateReservationStatus = _updateReservationStatus.receiveAsFlow()

    private val _acceptReservationState = Channel<AcceptReservationState>()
    val acceptReservationState = _acceptReservationState.receiveAsFlow()

    private val _rejectReservationState = Channel<RejectReservationState>()
    val rejectReservationState = _rejectReservationState.receiveAsFlow()

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

     fun fetchReservations() = viewModelScope.launch {
        reservationRepository.getAllReservations().collect { reservationsResource ->
            _reservations.value = reservationsResource
        }
    }

    fun selectRequest(request: RequestDetails) {
        _selectedRequest.value = request
    }

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
    fun deleteMarker(id: String) = viewModelScope.launch {
        markerRepository.deleteMarker(id).collect { result ->
            when(result) {
                is Resource.Loading -> { _deleteMarkerState.send(DeleteMarkerState(isLoading = true)) }
                is Resource.Success -> {
                    _deleteMarkerState.send(DeleteMarkerState(isSuccess = "Offer canceled successfully"))
                    fetchListingsWithDetails()
                }
                is Resource.Error -> { _deleteMarkerState.send(DeleteMarkerState(isError = result.message)) }
            }
        }
    }
    fun changeMarkerReserved(markerId: String, reserved: Boolean) = viewModelScope.launch {
        reservationRepository.changeMarkerReserved(markerId, reserved).collect { result ->
            when(result) {
                is Resource.Loading -> { _toggleMarkerReservedState.send(ToggleMarkerReserved(isLoading = true)) }
                is Resource.Success -> { _toggleMarkerReservedState.send(ToggleMarkerReserved(isSuccess = "Marker reserved status successfully changed")) }
                is Resource.Error -> { _toggleMarkerReservedState.send(ToggleMarkerReserved(isError = result.message)) }
            }
        }
    }

    fun updateRequestStatus(requestId: String, status: String) = viewModelScope.launch {
        reservationRepository.updateRequestStatus(requestId, status).collect { result ->
            when (result) {
                is Resource.Loading -> { _updateReservationStatus.send(UpdateStatusState(isLoading = true)) }
                is Resource.Success -> { _updateReservationStatus.send(UpdateStatusState(isSuccess = "Reservation status successfully updated")) }
                is Resource.Error -> { _updateReservationStatus.send(UpdateStatusState(isError = result.message)) }
            }
        }
    }

    fun rejectRequest(request: RequestDetails) = viewModelScope.launch {
        reservationRepository.updateRequestStatus(request.reservation.uuid, "Rejected").collect { updateResult ->
            when (updateResult) {
                is Resource.Loading -> { _rejectReservationState.send(RejectReservationState(isLoading = true)) }
                is Resource.Success -> {
                    fetchRequestsWithDetails(request.reservation.markerId)
                    _rejectReservationState.send(RejectReservationState(isSuccess = "Request successfully rejected"))
                }
                is Resource.Error -> {_rejectReservationState.send(RejectReservationState(isError = updateResult.message)) }
            }
        }
    }

    fun acceptRequest(request: RequestDetails) = viewModelScope.launch {
        reservationRepository.updateRequestStatus(request.reservation.uuid, "Accepted").collect { updateResult ->
            when (updateResult) {
                is Resource.Loading -> { _acceptReservationState.send(AcceptReservationState(isLoading = true)) }
                is Resource.Success -> {
                    fetchRequestsWithDetails(request.reservation.markerId)
                    _acceptReservationState.send(AcceptReservationState(isSuccess = "Request successfully accepted"))
                }
                is Resource.Error -> {
                    _acceptReservationState.send(AcceptReservationState(isError = updateResult.message))
                }
            }
        }
    }

    fun checkAndCompleteReservation(reservation: Reservation) = viewModelScope.launch {
        val currentDate = LocalDate.now()
        val endDate = LocalDate.parse(reservation.endDate)

        if (currentDate.isAfter(endDate) && reservation.status == "Accepted") {
            reservationRepository.updateRequestStatus(reservation.uuid, "Completed")
            changeMarkerReserved(reservation.markerId, false)
            vehicleRepository.changeVehicleChosen(reservation.vehicleId, false)
        }
    }
}