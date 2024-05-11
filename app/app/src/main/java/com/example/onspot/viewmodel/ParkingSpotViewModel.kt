package com.example.onspot.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.onspot.data.model.ParkingSpot
import com.example.onspot.data.repository.ParkingSpotRepository
import com.example.onspot.data.repository.ParkingSpotRepositoryImpl
import com.example.onspot.ui.states.AddParkingSpotState
import com.example.onspot.ui.states.UploadDocumentState
import com.example.onspot.utils.Resource
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.util.UUID

class ParkingSpotViewModel : ViewModel() {
    private val parkingSpotRepository: ParkingSpotRepository = ParkingSpotRepositoryImpl()

    private val _addParkingSpotState = Channel<AddParkingSpotState>()
    val addParkingSpotState = _addParkingSpotState.receiveAsFlow()

    private val _uploadDocumentState = Channel<UploadDocumentState>()
    val uploadDocumentState = _uploadDocumentState.receiveAsFlow()

    fun addParkingSpot(address: String, number: Int, documentUrl: String) = viewModelScope.launch {
        val parkingSpot = ParkingSpot(
            uuid = UUID.randomUUID().toString(),
            address = address,
            number = number,
            documentUrl = documentUrl,
            isApproved = false,
            isReserved = false,
            userId = ""
        )
        parkingSpotRepository.addParkingSpot(parkingSpot).collect { result ->
            when(result) {
                is Resource.Loading -> { _addParkingSpotState.send(AddParkingSpotState(isLoading = true)) }
                is Resource.Success -> { _addParkingSpotState.send(AddParkingSpotState(isSuccess = "Parking spot successfully added")) }
                is Resource.Error -> { _addParkingSpotState.send(AddParkingSpotState(isError = result.message)) }
            }
        }
    }
    fun uploadDocument(documentUri: Uri) = viewModelScope.launch {
        parkingSpotRepository.uploadDocument(documentUri).collect { result ->
            when(result) {
                is Resource.Loading -> { _uploadDocumentState.send(UploadDocumentState(isLoading = true)) }
                is Resource.Success -> {
                    val documentUrl = result.data
                    if (documentUrl != null) {
                        _uploadDocumentState.send(
                            UploadDocumentState(
                                isSuccess = "PDF document uploaded successfully",
                                documentUrl = documentUrl
                            )
                        )
                    } else {
                        _uploadDocumentState.send(UploadDocumentState(isError = "Cannot upload document"))
                    }
                }
                is Resource.Error -> { _uploadDocumentState.send(UploadDocumentState(isError = result.message)) }
            }
        }
    }

}