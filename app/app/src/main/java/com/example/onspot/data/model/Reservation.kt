package com.example.onspot.data.model

data class Reservation(
    val uuid: String,
    val status: String,
    val startDate: String,
    val startTime: String,
    val endDate: String,
    val endTime: String,
    val userId: String,
    val markerId: String,
    val vehicleId: String
) {
    constructor() : this("", "", "", "", "", "", "", "", "")
}
