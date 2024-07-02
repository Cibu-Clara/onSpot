package com.example.onspot.data.model

data class Review(
    val uuid: String,
    val reviewerId: String,
    val reviewedUserId: String,
    val rating: Float,
    val comment: String,
    val timestamp: Long,
    val reservationId: String
) {
    constructor() : this("", "", "", 0F, "", 0L, "")
}
