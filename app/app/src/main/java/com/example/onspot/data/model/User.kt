package com.example.onspot.data.model

data class User(
    val uuid: String,
    val firstName: String,
    val lastName: String,
    val email: String,
    val creationTimestamp: Long,
    val profilePictureUrl: String = "",
    val rating: Float = 0F,
    val ratingCount: Int = 0
) {
    constructor() : this("", "", "", "", 0L, "", 0F, 0)
}
