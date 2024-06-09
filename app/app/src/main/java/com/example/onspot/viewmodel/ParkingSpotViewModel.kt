package com.example.onspot.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.onspot.data.model.ParkingSpot
import com.example.onspot.data.repository.ParkingSpotRepository
import com.example.onspot.data.repository.ParkingSpotRepositoryImpl
import com.example.onspot.ui.states.AddParkingPictureState
import com.example.onspot.ui.states.AddParkingSpotState
import com.example.onspot.ui.states.DeleteParkingPictureState
import com.example.onspot.ui.states.DeleteParkingSpotState
import com.example.onspot.ui.states.DeletePdfState
import com.example.onspot.ui.states.EditParkingPictureState
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

    private val _deleteParkingSpotState = Channel<DeleteParkingSpotState>()
    val deleteParkingSpotState = _deleteParkingSpotState.receiveAsFlow()

    private val _addParkingPictureState = Channel<AddParkingPictureState>()
    val addParkingPictureState = _addParkingPictureState.receiveAsFlow()

    private val _deleteParkingPictureState = Channel<DeleteParkingPictureState>()
    val deleteParkingPictureState = _deleteParkingPictureState.receiveAsFlow()

    private val _editParkingPictureState = Channel<EditParkingPictureState>()
    val editParkingPictureState = _editParkingPictureState.receiveAsFlow()

    private val _uploadDocumentState = Channel<UploadDocumentState>()
    val uploadDocumentState = _uploadDocumentState.receiveAsFlow()

    private val _deletePdfState = Channel<DeletePdfState>()
    val deletePdfState = _deletePdfState.receiveAsFlow()

    private val _editPdfState = Channel<EditPdfState>()
    val editPdfState = _editPdfState.receiveAsFlow()

    fun fetchParkingSpotDetails(parkingSpotId: String) = viewModelScope.launch {
        parkingSpotRepository.getParkingSpotById(parkingSpotId).collect { parkingSpotDetailsResource ->
            _parkingSpotDetails.value = parkingSpotDetailsResource
        }
    }

    fun addParkingSpot(id: String, country: String, city: String, address: String, bayNumber: Int,
                       additionalInfo: String, photoUrl: String, documentUrl: String) = viewModelScope.launch {
        val parkingSpot = ParkingSpot(
            uuid = id,
            country = country,
            city = city,
            address = address,
            bayNumber = bayNumber,
            additionalInfo = additionalInfo,
            photoUrl = photoUrl,
            documentUrl = documentUrl,
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

    fun deleteParkingSpot(id: String) = viewModelScope.launch{
        parkingSpotRepository.deleteParkingSpot(id).collect { result ->
            when(result) {
                is Resource.Loading -> { _deleteParkingSpotState.send(DeleteParkingSpotState(isLoading = true)) }
                is Resource.Success -> { _deleteParkingSpotState.send(DeleteParkingSpotState(isSuccess = "Parking spot successfully deleted")) }
                is Resource.Error -> { _deleteParkingSpotState.send(DeleteParkingSpotState(isError = result.message)) }
            }
        }
    }

    fun addParkingSpotPicture(id: String, imageUri: Uri, originalFileName: String) = viewModelScope.launch {
        val localFileName = "$id.jpg"
        parkingSpotRepository.addParkingSpotPicture(id, imageUri, originalFileName).collect { result ->
            when (result) {
                is Resource.Loading -> { _addParkingPictureState.send(AddParkingPictureState(isLoading = true)) }
                is Resource.Success -> {
                    val parkingSpotPictureUrl = result.data
                    if (parkingSpotPictureUrl != null) {
                        _addParkingPictureState.send(
                            AddParkingPictureState(
                                isSuccess = "Image successfully uploaded",
                                photoUrl = parkingSpotPictureUrl,
                                localFileName = localFileName
                            )
                        )
                    } else {
                        _addParkingPictureState.send(AddParkingPictureState(isError = "Cannot upload image"))
                    }
                }
                is Resource.Error -> { _addParkingPictureState.send(AddParkingPictureState(isError = result.message)) }
            }
        }
    }

    fun deleteParkingSpotPicture(id: String) = viewModelScope.launch {
        parkingSpotRepository.deleteParkingSpotPicture(id).collect { result ->
            when (result) {
                is Resource.Loading -> { _deleteParkingPictureState.send(DeleteParkingPictureState(isLoading = true)) }
                is Resource.Success -> { _deleteParkingPictureState.send(DeleteParkingPictureState(isSuccess = "Image successfully deleted"))}
                is Resource.Error -> { _deleteParkingPictureState.send(DeleteParkingPictureState(isError = result.message))}
            }
        }
    }

    fun editParkingSpotPicture(id: String, imageUri: Uri, originalFileName: String) = viewModelScope.launch {
        val localFileName = "$id.jpg"
        parkingSpotRepository.editPicture(id, imageUri, originalFileName).collect { result ->
            when(result) {
                is Resource.Loading -> { _editParkingPictureState.send(EditParkingPictureState(isLoading = true)) }
                is Resource.Success -> {
                    val photoUrl = result.data
                    if (photoUrl != null) {
                        _editParkingPictureState.send(
                            EditParkingPictureState(
                                isSuccess = "Image edited successfully",
                                photoUrl = photoUrl,
                                localFileName = localFileName
                            )
                        )
                    } else {
                        _editParkingPictureState.send(EditParkingPictureState(isError = "Cannot edit image"))
                    }
                }
                is Resource.Error -> { _editParkingPictureState.send(EditParkingPictureState(isError = result.message)) }
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