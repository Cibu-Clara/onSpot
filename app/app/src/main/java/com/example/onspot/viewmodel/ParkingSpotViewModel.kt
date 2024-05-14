package com.example.onspot.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.onspot.data.model.ParkingSpot
import com.example.onspot.data.repository.ParkingSpotRepository
import com.example.onspot.data.repository.ParkingSpotRepositoryImpl
import com.example.onspot.ui.states.AddParkingSpotState
import com.example.onspot.ui.states.DeleteParkingSpotState
import com.example.onspot.ui.states.DeletePdfState
import com.example.onspot.ui.states.EditPdfState
import com.example.onspot.ui.states.UploadDocumentState
import com.example.onspot.utils.Resource
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class ParkingSpotViewModel : ViewModel() {
    private val parkingSpotRepository: ParkingSpotRepository = ParkingSpotRepositoryImpl()

    private val _parkingSpotDetails = MutableStateFlow<Resource<ParkingSpot>>(Resource.Loading())
    val parkingSpotDetails: StateFlow<Resource<ParkingSpot>> = _parkingSpotDetails.asStateFlow()

    private val _addParkingSpotState = Channel<AddParkingSpotState>()
    val addParkingSpotState = _addParkingSpotState.receiveAsFlow()

    private val _uploadDocumentState = Channel<UploadDocumentState>()
    val uploadDocumentState = _uploadDocumentState.receiveAsFlow()

    private val _deleteParkingSpotState = Channel<DeleteParkingSpotState>()
    val deleteParkingSpotState = _deleteParkingSpotState.receiveAsFlow()

    private val _deletePdfState = Channel<DeletePdfState>()
    val deletePdfState = _deletePdfState.receiveAsFlow()

    private val _editPdfState = Channel<EditPdfState>()
    val editPdfState = _editPdfState.receiveAsFlow()

    fun fetchParkingSpotDetails(parkingSpotId: String) = viewModelScope.launch {
        parkingSpotRepository.getParkingSpotById(parkingSpotId).collect { parkingSpotDetailsResource ->
            _parkingSpotDetails.value = parkingSpotDetailsResource
        }
    }

    fun addParkingSpot(id: String, address: String, number: Int, additionalInfo: String, documentUrl: String) = viewModelScope.launch {
        val parkingSpot = ParkingSpot(
            uuid = id,
            address = address,
            number = number,
            additionalInfo = additionalInfo,
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
    fun uploadDocument(id: String, documentUri: Uri, originalFileName: String) = viewModelScope.launch {
        val localFileName = "$id.pdf"
        parkingSpotRepository.uploadDocument(id, documentUri, originalFileName).collect { result ->
            when(result) {
                is Resource.Loading -> { _uploadDocumentState.send(UploadDocumentState(isLoading = true)) }
                is Resource.Success -> {
                    val documentUrl = result.data
                    if (documentUrl != null) {
                        _uploadDocumentState.send(
                            UploadDocumentState(
                                isSuccess = "PDF document uploaded successfully",
                                documentUrl = documentUrl,
                                localFileName = localFileName
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

    fun deleteParkingSpot(id: String) = viewModelScope.launch{
        parkingSpotRepository.deleteParkingSpot(id).collect { result ->
            when(result) {
                is Resource.Loading -> { _deleteParkingSpotState.send(DeleteParkingSpotState(isLoading = true)) }
                is Resource.Success -> { _deleteParkingSpotState.send(DeleteParkingSpotState(isSuccess = "Parking spot successfully deleted")) }
                is Resource.Error -> { _deleteParkingSpotState.send(DeleteParkingSpotState(isError = result.message)) }
            }
        }
    }

    fun deletePdfDocument(id: String) = viewModelScope.launch {
        parkingSpotRepository.deletePdfDocument(id).collect { result ->
             when (result) {
                is Resource.Loading -> { _deletePdfState.send(DeletePdfState(isLoading = true)) }
                is Resource.Success -> { _deletePdfState.send(DeletePdfState(isSuccess = "PDF document successfully deleted"))}
                is Resource.Error -> { _deletePdfState.send(DeletePdfState(isError = result.message))}
            }
        }
    }

    fun editPdfDocument(id: String, documentUri: Uri, originalFileName: String) = viewModelScope.launch {
        val localFileName = "$id.pdf"
        parkingSpotRepository.editDocument(id, documentUri, originalFileName).collect { result ->
            when(result) {
                is Resource.Loading -> { _editPdfState.send(EditPdfState(isLoading = true)) }
                is Resource.Success -> {
                    val documentUrl = result.data
                    if (documentUrl != null) {
                        _editPdfState.send(
                            EditPdfState(
                                isSuccess = "PDF document edited successfully",
                                documentUrl = documentUrl,
                                localFileName = localFileName
                            )
                        )
                    } else {
                        _editPdfState.send(EditPdfState(isError = "Cannot upload document"))
                    }
                }
                is Resource.Error -> { _editPdfState.send(EditPdfState(isError = result.message)) }
            }
        }
    }
}