package com.example.onspot.data.repository

import com.example.onspot.data.model.Reservation
import com.example.onspot.utils.Resource
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await

class ReservationRepositoryImpl : ReservationRepository {
    private val reservationsCollection: CollectionReference =
        FirebaseFirestore.getInstance().collection("reservations")

    override fun createReservation(reservation: Reservation): Flow<Resource<Void?>> = flow {
        try {
            emit(Resource.Loading())
            reservationsCollection
                .document(reservation.uuid)
                .set(reservation)
                .await()
            emit(Resource.Success(null))
        }
        catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Failed to create reservation"))
        }
    }

    override fun getAllReservations(): Flow<Resource<List<Reservation>>> = flow {
        try {
            emit(Resource.Loading())
            val snapshot = reservationsCollection
                .get()
                .await()
            val reservations = snapshot.documents.mapNotNull { it.toObject(Reservation::class.java) }
            emit(Resource.Success(reservations))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Failed to fetch reservations"))
        }
    }
}