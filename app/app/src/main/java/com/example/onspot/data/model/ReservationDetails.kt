package com.example.onspot.data.model

data class ReservationDetails(
    val reservation: Reservation,
    val parkingSpot: ParkingSpot,
    val user: User,
    val vehicle: Vehicle
)

