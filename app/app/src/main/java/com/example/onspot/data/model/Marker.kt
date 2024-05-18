package com.example.onspot.data.model

data class Marker(
    val uuid: String,
    val longitude: Double,
    val latitude: Double,
    val startTime: Long,
    val endTime: Long,
    val parkingSpotId: String
)
