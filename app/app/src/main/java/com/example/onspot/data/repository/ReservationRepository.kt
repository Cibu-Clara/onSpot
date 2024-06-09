package com.example.onspot.data.repository

import com.example.onspot.data.model.ListingDetails
import com.example.onspot.data.model.RequestDetails
import com.example.onspot.data.model.Reservation
import com.example.onspot.data.model.ReservationDetails
import com.example.onspot.utils.Resource
import kotlinx.coroutines.flow.Flow

interface ReservationRepository {
    fun createReservation(reservation: Reservation): Flow<Resource<Void?>>
    fun deleteReservation(reservationId: String): Flow<Resource<Void?>>
    fun getAllReservations(): Flow<Resource<List<Reservation>>>
    fun getReservationsCurrentUser(): Flow<Resource<List<ReservationDetails>>>
    fun getListingsCurrentUser(): Flow<Resource<List<ListingDetails>>>
    fun getRequests(markerId: String): Flow<Resource<List<RequestDetails>>>
    fun updateRequestStatus(requestId: String, status: String): Flow<Resource<Void?>>
    fun updateMarkerReserved(markerId: String, reserved: Boolean): Flow<Resource<Void?>>
}