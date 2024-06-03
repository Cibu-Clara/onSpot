package com.example.onspot.ui.states

data class ToggleVehicleChosenState (
    val isLoading: Boolean = false,
    val isSuccess: String? = "",
    val isError: String? = ""
)