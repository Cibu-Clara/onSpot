package com.example.onspot.data.repository

import android.util.Log
import com.example.onspot.data.model.ListingDetails
import com.example.onspot.data.model.Marker
import com.example.onspot.data.model.ParkingSpot
import com.example.onspot.data.model.RequestDetails
import com.example.onspot.data.model.Reservation
import com.example.onspot.data.model.ReservationDetails
import com.example.onspot.data.model.User
import com.example.onspot.data.model.Vehicle
import com.example.onspot.utils.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await

class ReservationRepositoryImpl : ReservationRepository {
    private val reservationsCollection: CollectionReference =
        FirebaseFirestore.getInstance().collection("reservations")
    private val markersCollection: CollectionReference =
        FirebaseFirestore.getInstance().collection("markers")
    private val parkingSpotsCollection: CollectionReference =
        FirebaseFirestore.getInstance().collection("parkingSpots")
    private val usersCollection: CollectionReference =
        FirebaseFirestore.getInstance().collection("users")
    private val vehiclesCollection: CollectionReference =
        FirebaseFirestore.getInstance().collection("vehicles")
    private val reviewsCollection: CollectionReference =
        FirebaseFirestore.getInstance().collection("reviews")
    private val currentUserId = FirebaseAuth.getInstance().currentUser?.uid

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

    override fun getListingsCurrentUser(): Flow<Resource<List<ListingDetails>>> = flow {
        emit(Resource.Loading())
        try {
            val markersSnapshot = markersCollection
                .whereEqualTo("userId", currentUserId)
                .get()
                .await()
            val markers = markersSnapshot.documents.mapNotNull { it.toObject(Marker::class.java) }

            var parkingSpots = emptyList<ParkingSpot>()
            val parkingSpotIds = markers.map { it.parkingSpotId }
            if (parkingSpotIds.isNotEmpty()) {
                val parkingSpotsSnapshot = parkingSpotsCollection
                    .whereIn("uuid", parkingSpotIds)
                    .get()
                    .await()
                parkingSpots = parkingSpotsSnapshot.documents.mapNotNull { it.toObject(ParkingSpot::class.java) }
            }
            val listingDetailsList = markers.map { marker ->
                val parkingSpot = parkingSpots.find { it.uuid == marker.parkingSpotId }
                    ?: throw IllegalStateException("Parking spot not found for marker ${marker.uuid}")
                ListingDetails(
                    marker = marker,
                    parkingSpot = parkingSpot
                )
            }
            emit(Resource.Success(listingDetailsList))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Failed to fetch listings"))
        }
    }

    override fun getRequests(markerId: String): Flow<Resource<List<RequestDetails>>> = flow {
        emit(Resource.Loading())
        try {
            val reservationsSnapshot = reservationsCollection
                .whereEqualTo("markerId", markerId)
                .get()
                .await()
            val reservations = reservationsSnapshot.documents.mapNotNull { it.toObject(Reservation::class.java) }

            var users = emptyList<User>()
            val userIds = reservations.map { it.userId }
            if (userIds.isNotEmpty()) {
                val usersSnapshot = usersCollection
                    .whereIn("uuid", userIds)
                    .get()
                    .await()
                users = usersSnapshot.documents.mapNotNull { it.toObject(User::class.java) }
            }

            var vehicles = emptyList<Vehicle>()
            val vehicleIds = reservations.map { it.vehicleId }
            if (vehicleIds.isNotEmpty()) {
                val vehiclesSnapshot = vehiclesCollection
                    .whereIn("uuid", vehicleIds)
                    .get()
                    .await()
                vehicles = vehiclesSnapshot.documents.mapNotNull { it.toObject(Vehicle::class.java) }
            }
            val requestDetailsList = reservations.map { reservation ->
                val user = users.find { it.uuid == reservation.userId }
                    ?: throw IllegalStateException("User not found for reservation ${reservation.uuid}")
                val vehicle = vehicles.find { it.uuid == reservation.vehicleId }
                    ?: throw IllegalStateException("Vehicle not found for reservation ${reservation.uuid}")
                RequestDetails(
                    reservation = reservation,
                    user = user,
                    vehicle = vehicle
                )
            }
            emit(Resource.Success(requestDetailsList))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Failed to fetch reservation requests"))
        }
    }

    override fun getReservationsCurrentUser(): Flow<Resource<List<ReservationDetails>>> = flow {
        emit(Resource.Loading())
        try {
            val reservationsSnapshot = reservationsCollection
                .whereEqualTo("userId", currentUserId)
                .get()
                .await()
            val reservations = reservationsSnapshot.documents.mapNotNull { it.toObject(Reservation::class.java) }

            var parkingSpots = emptyList<ParkingSpot>()
            val parkingSpotIds = reservations.map { it.parkingSpotId }
            if (parkingSpotIds.isNotEmpty()) {
                val parkingSpotsSnapshot = parkingSpotsCollection
                    .whereIn("uuid", parkingSpotIds)
                    .get()
                    .await()
                parkingSpots = parkingSpotsSnapshot.documents.mapNotNull { it.toObject(ParkingSpot::class.java) }
            }

            var users = emptyList<User>()
            val userIds = parkingSpots.map { it.userId }
            if (userIds.isNotEmpty()) {
                val usersSnapshot = usersCollection
                    .whereIn("uuid", userIds)
                    .get()
                    .await()
                users = usersSnapshot.documents.mapNotNull { it.toObject(User::class.java) }
            }

            var vehicles = emptyList<Vehicle>()
            val vehicleIds = reservations.map { it.vehicleId }
            if (vehicleIds.isNotEmpty()) {
                val vehiclesSnapshot = vehiclesCollection
                    .whereIn("uuid", vehicleIds)
                    .get()
                    .await()
                vehicles = vehiclesSnapshot.documents.mapNotNull { it.toObject(Vehicle::class.java) }
            }

            val reservationDetailsList = reservations.map { reservation ->
                val parkingSpot = parkingSpots.find { it.uuid == reservation.parkingSpotId }
                    ?: throw IllegalStateException("Parking spot not found for reservation ${reservation.uuid}")
                val user = users.find { it.uuid == parkingSpot.userId }
                    ?: throw IllegalStateException("User not found for parking spot ${parkingSpot.uuid}")
                val vehicle = vehicles.find { it.uuid == reservation.vehicleId }
                    ?: throw IllegalStateException("Vehicle not found for reservation ${reservation.uuid}")
                ReservationDetails(
                    reservation = reservation,
                    parkingSpot = parkingSpot,
                    user = user,
                    vehicle = vehicle
                )
            }
            emit(Resource.Success(reservationDetailsList))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Failed to fetch reservations"))
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

    override fun deleteReservation(reservationId: String): Flow<Resource<Void?>> = flow {
        try {
            emit(Resource.Loading())
            reservationsCollection
                .document(reservationId)
                .delete()
                .await()
            emit(Resource.Success(null))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Failed to delete reservation"))
        }
    }

    override fun updateRequestStatus(requestId: String, status: String): Flow<Resource<Void?>> = flow {
        try {
            val reservationDocument = reservationsCollection
                .document(requestId)
                .get()
                .await()
            reservationDocument.toObject(Reservation::class.java) ?: throw IllegalStateException("Reservation not found")

            reservationsCollection
                .document(requestId)
                .update("status", status)
                .await()
            emit(Resource.Success(null))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Failed to update request status"))
            Log.e("UPDATE STATUS", "FAILED")
        }
    }

    override fun changeMarkerReserved(markerId: String, reserved: Boolean): Flow<Resource<Void?>> = flow {
        try {
            val markerDocument = markersCollection
                .document(markerId)
                .get()
                .await()
            val marker = markerDocument.toObject(Marker::class.java)

            if (marker != null) {
                markersCollection
                    .document(markerId)
                    .update("reserved", reserved)
                    .await()
                emit(Resource.Success(null))
            } else {
                emit(Resource.Error("Marker not found"))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Failed to change marker reserved status"))
        }
    }

    override suspend fun checkAlreadyReviewed(reservationId: String): Boolean {
        return try {
            val snapshot = reviewsCollection
                .whereEqualTo("reservationId", reservationId)
                .whereEqualTo("reviewerId", currentUserId)
                .get()
                .await()

            snapshot.documents.isNotEmpty()
        } catch (e: Exception) {
            false
        }
    }
}