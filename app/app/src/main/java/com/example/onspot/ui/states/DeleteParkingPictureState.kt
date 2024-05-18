package com.example.onspot.ui.states

data class DeleteParkingPictureState(
    val isLoading: Boolean = false,
    val isSuccess: String? = "",
    val isError: String? = "",
    val photoUrl: String? = null,
    val localFileName: String? = null
)