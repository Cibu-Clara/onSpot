package com.example.onspot.data.repository

import android.net.Uri
import com.example.onspot.data.model.ParkingSpot
import com.example.onspot.data.model.Vehicle
import com.example.onspot.utils.Resource
import kotlinx.coroutines.flow.Flow

interface VehicleRepository {
    fun getVehicleById(vehicleId: String): Flow<Resource<Vehicle>>
    fun addVehicle(vehicle: Vehicle): Flow<Resource<Void?>>
    fun getVehicles(): Flow<Resource<List<Vehicle>>>
    fun deleteVehicle(vehicleId: String): Flow<Resource<Void?>>
}