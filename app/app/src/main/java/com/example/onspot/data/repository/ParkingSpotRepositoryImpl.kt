package com.example.onspot.data.repository

import android.net.Uri
import com.example.onspot.data.model.ParkingSpot
import com.example.onspot.utils.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import java.util.UUID

class ParkingSpotRepositoryImpl : ParkingSpotRepository {
    private val parkingSpotsCollection: CollectionReference =
        FirebaseFirestore.getInstance().collection("parkingSpots")
    private val storageReference = FirebaseStorage.getInstance().reference
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

    override fun getParkingSpots(): Flow<Resource<List<ParkingSpot>>> = flow {
        try {
            emit(Resource.Loading())
            val snapshot = parkingSpotsCollection
                .whereEqualTo("userId", currentUserId)
                .get()
                .await()
            val parkingSpots = snapshot.documents.mapNotNull { it.toObject(ParkingSpot::class.java) }
            emit(Resource.Success(parkingSpots))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Failed to fetch parking spots"))
        }
    }

    override fun uploadDocument(documentUri: Uri): Flow<Resource<String>> = flow {
        emit(Resource.Loading())
        val documentRef = storageReference.child("documents/${UUID.randomUUID()}")

        val uploadTaskSnapshot = documentRef.putFile(documentUri).await()
        val downloadUri = uploadTaskSnapshot
            .storage
            .downloadUrl
            .await()
        emit(Resource.Success(downloadUri.toString()))
    }.catch { e ->
        emit(Resource.Error(e.message ?: "Failed to upload document"))
    }
}