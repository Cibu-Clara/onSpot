package com.example.onspot.data.model

data class Marker(
    val uuid: String,
    val longitude: Double,
    val latitude: Double,
    val startDate: String,
    val startTime: String,
    val endDate: String,
    val endTime: String,
    val parkingSpotId: String
) {
    constructor() : this("", 0.0, 0.0, "","", "", "","")
}
