package com.example.onspot.ui.states

data class RejectReservationState(
    val isLoading: Boolean = false,
    val isSuccess: String? = "",
    val isError: String? = "",
)
