package com.example.onspot.data.repository

import com.example.onspot.data.model.ParkingSpot
import com.example.onspot.utils.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await

class ParkingSpotRepositoryImpl : ParkingSpotRepository {
    private val parkingSpotsCollection: CollectionReference =
        FirebaseFirestore.getInstance().collection("parkingSpots")
    private val currentUserId = FirebaseAuth.getInstance().currentUser?.uid

    override fun addParkingSpot(parkingSpot: ParkingSpot): Flow<Resource<Void?>> = flow {
        try {
            emit(Resource.Loading())
            parkingSpotsCollection
                .document(parkingSpot.uuid)
                .set(parkingSpot.copy(userId = currentUserId!!))
                .await()
            emit(Resource.Success(null))
        } catch (e: NullPointerException) {
            emit(Resource.Error(e.message ?: "User not logged in"))
        }
        catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Failed to add parking spot"))
        }
    }
}