package com.example.onspot.data.repository

import com.example.onspot.data.model.Marker
import com.example.onspot.utils.Resource
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await

class MarkerRepositoryImpl : MarkerRepository {
    private val markersCollection: CollectionReference =
        FirebaseFirestore.getInstance().collection("markers")
    override fun createMarker(marker: Marker): Flow<Resource<Void?>> = flow {
        try {
            emit(Resource.Loading())
            markersCollection
                .document(marker.uuid)
                .set(marker)
                .await()
            emit(Resource.Success(null))
        }
        catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Failed to create marker"))
        }
    }
}