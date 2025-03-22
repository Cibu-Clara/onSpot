package com.example.onspot.data.repository

import com.example.onspot.data.model.Marker
import com.example.onspot.data.model.ParkingSpot
import com.example.onspot.utils.Resource
import kotlinx.coroutines.flow.Flow

interface MarkerRepository {
    fun createMarker(marker: Marker): Flow<Resource<Void?>>
    fun getAllMarkers(): Flow<Resource<List<Marker>>>
    fun getMarkerById(markerId: String): Flow<Resource<Marker>>
    fun deleteMarkers(expiredMarkers: List<Marker>): Flow<Resource<Void?>>
    fun deleteMarker(markerId: String): Flow<Resource<Void?>>
}