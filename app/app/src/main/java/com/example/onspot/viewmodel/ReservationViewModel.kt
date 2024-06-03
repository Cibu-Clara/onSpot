package com.example.onspot.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.onspot.data.model.Reservation
import com.example.onspot.data.repository.ReservationRepository
import com.example.onspot.data.repository.ReservationRepositoryImpl
import com.example.onspot.ui.states.AddReservationState
import com.example.onspot.utils.Resource
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class ReservationViewModel : ViewModel() {
    private val reservationRepository : ReservationRepository = ReservationRepositoryImpl()

    private val _addReservationState = Channel<AddReservationState>()
    val addReservationState = _addReservationState.receiveAsFlow()

    private val _reservations = MutableStateFlow<Resource<List<Reservation>>>(Resource.Loading())
    val reservations: StateFlow<Resource<List<Reservation>>> = _reservations.asStateFlow()

    init {
        fetchReservations()
    }

    private fun fetchReservations() = viewModelScope.launch {
        reservationRepository.getAllReservations().collect { reservationsResource ->
            _reservations.value = reservationsResource
        }
    }

    fun addReservation(id: String, status: String, startDate: String, startTime: String, endDate: String, endTime: String, userId: String, markerId: String, vehicleId: String) = viewModelScope.launch {
        val reservation = Reservation(
            uuid = id,
            status = status,
            startDate = startDate,
            startTime = startTime,
            endDate = endDate,
            endTime = endTime,
            userId = userId,
            markerId = markerId,
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

}