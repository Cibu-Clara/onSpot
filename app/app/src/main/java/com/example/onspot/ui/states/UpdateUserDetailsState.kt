package com.example.onspot.ui.states

data class UpdateUserDetailsState (
    val isLoading: Boolean = false,
    val isSuccess: String? = "",
    val isError: String? = ""
)