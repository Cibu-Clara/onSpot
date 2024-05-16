package com.example.onspot.data.repository

import com.example.onspot.data.model.Vehicle
import com.example.onspot.utils.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await

class VehicleRepositoryImpl : VehicleRepository {
    private val vehiclesCollection: CollectionReference =
        FirebaseFirestore.getInstance().collection("vehicles")
    private val currentUserId = FirebaseAuth.getInstance().currentUser?.uid

    override fun getVehicleById(vehicleId: String): Flow<Resource<Vehicle>> = flow {
        emit(Resource.Loading())
        val vehicleDocument = vehiclesCollection
            .document(vehicleId)
            .get()
            .await()
        val vehicle = vehicleDocument.toObject(Vehicle::class.java)
        if (vehicle != null) {
            emit(Resource.Success(vehicle))
        } else {
            emit(Resource.Error("Vehicle not found"))
        }
    }.catch { e ->
        emit(Resource.Error(e.message ?: "Failed to fetch vehicle details"))
    }

    override fun addVehicle(vehicle: Vehicle): Flow<Resource<Void?>> = flow {
        try {
            emit(Resource.Loading())
            vehiclesCollection
                .document(vehicle.uuid)
                .set(vehicle.copy(userId = currentUserId!!))
                .await()
            emit(Resource.Success(null))
        } catch (e: NullPointerException) {
            emit(Resource.Error(e.message ?: "User not logged in"))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Failed to add vehicle"))
        }
    }

    override fun getVehicles(): Flow<Resource<List<Vehicle>>> = flow {
        try {
            emit(Resource.Loading())
            val snapshot = vehiclesCollection
                .whereEqualTo("userId", currentUserId)
                .get()
                .await()
            val vehicles = snapshot.documents.mapNotNull { it.toObject(Vehicle::class.java) }
            emit(Resource.Success(vehicles))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Failed to fetch vehicles"))
        }
    }

    override fun deleteVehicle(vehicleId: String): Flow<Resource<Void?>> = flow {
        try {
            emit(Resource.Loading())

            vehiclesCollection
                .document(vehicleId)
                .delete()
                .await()
            emit(Resource.Success(null))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Failed to delete vehicle"))
        }
    }

}