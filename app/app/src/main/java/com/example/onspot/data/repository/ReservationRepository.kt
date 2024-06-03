package com.example.onspot.data.repository

import com.example.onspot.data.model.Reservation
import com.example.onspot.utils.Resource
import kotlinx.coroutines.flow.Flow

interface ReservationRepository {
    fun createReservation(reservation: Reservation): Flow<Resource<Void?>>
    fun getAllReservations(): Flow<Resource<List<Reservation>>>
}