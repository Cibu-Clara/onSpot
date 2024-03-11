package com.example.parkingspots.data.model

data class User(
    val uuid: String,
    val firstName: String,
    val lastName: String,
    val email: String,
    val isAdmin: Boolean
){
    constructor() : this("", "", "", "", false)
}
