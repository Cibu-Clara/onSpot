package com.example.onspot.data.repository

import android.net.Uri
import com.example.onspot.data.model.ParkingSpot
import com.example.onspot.utils.Resource
import kotlinx.coroutines.flow.Flow

interface ParkingSpotRepository {
    fun getParkingSpotById(parkingSpotId: String): Flow<Resource<ParkingSpot>>
    fun addParkingSpot(parkingSpot: ParkingSpot): Flow<Resource<Void?>>
    fun getParkingSpots(): Flow<Resource<List<ParkingSpot>>>
    fun deleteParkingSpot(parkingSpotId: String): Flow<Resource<Void?>>
    fun uploadDocument(id: String, documentUri: Uri, originalFileName: String): Flow<Resource<String>>
    fun deletePdfDocument(parkingSpotId: String): Flow<Resource<Void?>>
    fun editDocument(parkingSpotId: String, documentUri: Uri, originalFileName: String): Flow<Resource<String>>
    fun addParkingSpotPicture(parkingSpotId: String, imageUri: Uri, originalFileName: String): Flow<Resource<String>>
    fun deleteParkingSpotPicture(parkingSpotId: String): Flow<Resource<Void?>>
    fun editPicture(parkingSpotId: String, imageUri: Uri, originalFileName: String): Flow<Resource<String>>
}