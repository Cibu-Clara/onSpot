package com.example.onspot.ui.states

data class UpdateStatusState(
    val isLoading: Boolean = false,
    val isSuccess: String? = "",
    val isError: String? = ""
)
