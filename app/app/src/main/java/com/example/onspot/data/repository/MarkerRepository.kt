package com.example.onspot.data.repository

import com.example.onspot.data.model.Marker
import com.example.onspot.utils.Resource
import kotlinx.coroutines.flow.Flow

interface MarkerRepository {
    fun createMarker(marker: Marker): Flow<Resource<Void?>>
}