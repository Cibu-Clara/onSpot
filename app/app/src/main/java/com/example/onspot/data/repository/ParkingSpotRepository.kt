package com.example.onspot.data.repository

import android.net.Uri
import com.example.onspot.data.model.ParkingSpot
import com.example.onspot.utils.Resource
import kotlinx.coroutines.flow.Flow

interface ParkingSpotRepository {
    fun addParkingSpot(parkingSpot: ParkingSpot): Flow<Resource<Void?>>
    fun getParkingSpots(): Flow<Resource<List<ParkingSpot>>>
    fun uploadDocument(documentUri: Uri): Flow<Resource<String>>
}