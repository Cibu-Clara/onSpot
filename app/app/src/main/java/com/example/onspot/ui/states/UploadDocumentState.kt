package com.example.onspot.ui.states

data class UploadDocumentState(
    val isLoading: Boolean = false,
    val isSuccess: String? = "",
    val isError: String? = "",
    val documentUrl: String? = null
)
