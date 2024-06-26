package com.example.onspot.data.model

data class ParkingSpot(
    val uuid: String,
    val country: String,
    val city: String,
    val address: String,
    val bayNumber: Int,
    val additionalInfo: String = "",
    val photoUrl: String,
    val documentUrl: String = "",
    val userId: String
) {
    constructor() : this("", "", "", "",0, "", "","", "")
}
