package com.example.onspot.data.repository

import android.util.Log
import com.example.onspot.data.model.Marker
import com.example.onspot.data.model.ParkingSpot
import com.example.onspot.utils.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await

class MarkerRepositoryImpl : MarkerRepository {
    private val markersCollection: CollectionReference =
        FirebaseFirestore.getInstance().collection("markers")
    private val currentUserId = FirebaseAuth.getInstance().currentUser?.uid

    override fun createMarker(marker: Marker): Flow<Resource<Void?>> = flow {
        try {
            emit(Resource.Loading())
            markersCollection
                .document(marker.uuid)
                .set(marker.copy(userId = currentUserId!!))
                .await()
            emit(Resource.Success(null))
        }
        catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Failed to create marker"))
        }
    }

    override fun getAllMarkers(): Flow<Resource<List<Marker>>> = flow {
        try {
            emit(Resource.Loading())
            val snapshot = markersCollection
                .get()
                .await()
            val markers = snapshot.documents.mapNotNull { it.toObject(Marker::class.java) }
            emit(Resource.Success(markers))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Failed to fetch markers"))
        }
    }

    override fun getMarkerById(markerId: String): Flow<Resource<Marker>> = flow {
        emit(Resource.Loading())
        val markerDocument = markersCollection
            .document(markerId)
            .get()
            .await()
        val marker = markerDocument.toObject(Marker::class.java)
        if (marker != null) {
            emit(Resource.Success(marker))
        } else {
            emit(Resource.Error("Marker not found"))
        }
    }.catch { e ->
        emit(Resource.Error(e.message ?: "Failed to fetch marker details"))
    }

    override fun deleteMarkers(expiredMarkers: List<Marker>): Flow<Resource<Void?>> = flow {
        try {
            emit(Resource.Loading())
            for (marker in expiredMarkers) {
                markersCollection.document(marker.uuid).delete().await()
            }
            emit(Resource.Success(null))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Failed to delete markers"))
        }
    }

    override fun deleteMarker(markerId: String): Flow<Resource<Void?>> = flow {
        try {
            emit(Resource.Loading())

            markersCollection
                .document(markerId)
                .delete()
                .await()
            emit(Resource.Success(null))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Failed to delete marker"))
        }
    }
}