package com.example.onspot.data.model

data class ParkingSpot(
    val uuid: String,
    val address: String,
    val number: Int,
    val additionalInfo: String = "",
    val documentUrl: String = "",
    val isApproved: Boolean = false,
    val isReserved: Boolean = false,
    val userId: String
) {
    constructor() : this("", "", 0, "", "", false, false, "")
}
