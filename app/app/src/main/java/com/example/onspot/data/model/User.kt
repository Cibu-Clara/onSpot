package com.example.onspot.data.model

data class User(
    val uuid: String,
    val firstName: String,
    val lastName: String,
    val email: String,
    val isAdmin: Boolean,
    val creationTimestamp: Long,
    val profilePictureUrl: String = ""
){
    constructor() : this("", "", "", "", false, 0L, "")
}
