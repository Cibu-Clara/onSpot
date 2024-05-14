package com.example.onspot.ui.states

data class EditPdfState(
    val isLoading: Boolean = false,
    val isSuccess: String? = "",
    val isError: String? = "",
    val documentUrl: String? = null,
    val localFileName: String? = null
)
