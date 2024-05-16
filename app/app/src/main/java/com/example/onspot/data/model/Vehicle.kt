package com.example.onspot.data.model

data class Vehicle(
    val uuid: String,
    val licensePlate: String,
    val country: String,
    val make: String,
    val model: String,
    val year: Int,
    val color: String,
    val isChosen: Boolean = false,
    val userId: String
) {
    constructor() : this("", "", "", "", "", 0, "", false, "")
}
