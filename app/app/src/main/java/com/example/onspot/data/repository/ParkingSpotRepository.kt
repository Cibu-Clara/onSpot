package com.example.onspot.data.repository

import com.example.onspot.data.model.ParkingSpot
import com.example.onspot.utils.Resource
import kotlinx.coroutines.flow.Flow

interface ParkingSpotRepository {
    fun addParkingSpot(parkingSpot: ParkingSpot): Flow<Resource<Void?>>
}