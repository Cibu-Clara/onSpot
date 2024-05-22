package com.example.onspot.ui.screens.secondary

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Photo
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.onspot.navigation.Screens
import com.example.onspot.ui.components.CustomButton
import com.example.onspot.ui.components.CustomTextField
import com.example.onspot.ui.components.CustomTopBar
import com.example.onspot.ui.components.ImageOptionsBottomSheet
import com.example.onspot.ui.components.PDFFilePicker
import com.example.onspot.ui.theme.RegularFont
import com.example.onspot.utils.PhotoHandler
import com.example.onspot.utils.openPdf
import com.example.onspot.viewmodel.ParkingSpotViewModel
import kotlinx.coroutines.launch
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun AddParkingSpotScreen(
    navController: NavController,
    parkingSpotViewModel: ParkingSpotViewModel = viewModel()
) {
    var country by rememberSaveable { mutableStateOf("") }
    var city by rememberSaveable { mutableStateOf("") }
    var address by rememberSaveable { mutableStateOf("") }
    var bayNumber by rememberSaveable { mutableStateOf("") }
    var additionalInfo by rememberSaveable { mutableStateOf("") }
    var photoUrl by rememberSaveable { mutableStateOf("") }
    var originalFileNameJPG by rememberSaveable { mutableStateOf("") }
    var localFileNameJPG by rememberSaveable { mutableStateOf("") }
    var documentUrl by rememberSaveable { mutableStateOf("") }
    var originalFileNamePDF by rememberSaveable { mutableStateOf("") }
    var localFileNamePDF by rememberSaveable { mutableStateOf("") }
    val id by rememberSaveable { mutableStateOf(UUID.randomUUID()) }

    val isAddButtonEnabled = country.isNotBlank() && city.isNotBlank() && address.isNotBlank()
            && bayNumber.isNotBlank() && photoUrl.isNotBlank() && documentUrl.isNotBlank()
    val isUploadPhotoButtonEnabled = photoUrl.isBlank()
    var isViewPhotoButtonEnabled = photoUrl.isNotBlank()
    val isUploadPDFButtonEnabled = documentUrl.isBlank()
    var isViewPDFButtonEnabled = documentUrl.isNotBlank()
    var showOptionsBottomSheet by rememberSaveable { mutableStateOf(false) }
    val optionsSheetState = rememberModalBottomSheetState()

    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    fun clearFocus() { focusManager.clearFocus() }
    val photoHandler = remember { PhotoHandler(context) }

    val addParkingSpotState = parkingSpotViewModel.addParkingSpotState.collectAsState(initial = null)
    val uploadDocumentState = parkingSpotViewModel.uploadDocumentState.collectAsState(initial = null)
    val deletePdfState = parkingSpotViewModel.deletePdfState.collectAsState(initial = null)
    val addParkingPictureState = parkingSpotViewModel.addParkingPictureState.collectAsState(initial = null)
    val deleteParkingPictureState = parkingSpotViewModel.deleteParkingPictureState.collectAsState(initial = null)

    val takePictureLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { isSuccess ->
        if (isSuccess) {
            photoHandler.photoUri?.let { savedUri ->
                originalFileNameJPG = savedUri.lastPathSegment ?: "unknown.jpg"
                parkingSpotViewModel.addParkingSpotPicture(id.toString(), savedUri, originalFileNameJPG)
            }
        }
    }

    val requestCameraPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            photoHandler.takePhoto(takePictureLauncher)
        } else {
            Toast.makeText(context, "Camera permission is needed to take photos", Toast.LENGTH_SHORT).show()
        }
    }

    val openGalleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { galleryUri: Uri? ->
            galleryUri?.let {
                originalFileNameJPG = it.lastPathSegment ?: "unknown.jpg"
                parkingSpotViewModel.addParkingSpotPicture(id.toString(), galleryUri, originalFileNameJPG)
            }
        }
    )

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Scaffold(
            topBar = { CustomTopBar(
                title = "Add a parking spot",
                onBackClick = {
                    navController.popBackStack()
                    if (documentUrl.isNotBlank()) {
                        parkingSpotViewModel.deletePdfDocument(id.toString())
                        parkingSpotViewModel.deleteParkingSpotPicture(id.toString())
                    }
                })
            },
            bottomBar = {
                CustomButton(
                    onClick = {
                        scope.launch {
                            parkingSpotViewModel.addParkingSpot(
                                id = id.toString(),
                                country = country,
                                city = city,
                                address = address,
                                bayNumber = bayNumber.toInt(),
                                additionalInfo = additionalInfo,
                                photoUrl = photoUrl,
                                documentUrl = documentUrl
                            )
                        }
                    },
                    buttonText = "Add",
                    enabled = isAddButtonEnabled,
                    modifier = Modifier.padding(bottom = 30.dp)
                )
            }
        ) { paddingValues ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable(onClick = { clearFocus() })
                    .padding(paddingValues)
            ) {
                item {
                    Text(
                        text = "Please provide the required details to identify your parking spot, including a picture of it and a document to attest" +
                                " your ownership or legal right to use the space, such as a rental agreement or purchase contract. ",
                        modifier = Modifier
                            .padding(horizontal = 30.dp, vertical = 20.dp),
                        fontFamily = RegularFont,
                        color = Color.Gray,
                        fontSize = 16.sp
                    )
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 30.dp)
                    ) {
                        CustomTextField(
                            value = country,
                            onValueChange = { country = it },
                            label = "Country/State",
                            maxLines = 1
                        )
                        CustomTextField(
                            value = city,
                            onValueChange = { city = it },
                            label = "City/Town",
                            maxLines = 1,
                            modifier = Modifier.padding(top = 10.dp)
                        )
                        CustomTextField(
                            value = address,
                            onValueChange = { address = it },
                            label = "Address",
                            maxLines = 1,
                            modifier = Modifier.padding(top = 10.dp)
                        )
                        CustomTextField(
                            value = bayNumber,
                            onValueChange = { bayNumber = it },
                            label = "Number of the parking spot",
                            maxLines = 1,
                            keyboardType = KeyboardType.Number,
                            modifier = Modifier.padding(top = 10.dp)
                        )
                        CustomTextField(
                            value = additionalInfo,
                            onValueChange = { additionalInfo = it },
                            label = "Any other details you want to share with drivers so that they can find " +
                                    "and access your parking spot(e.g., entrance code, specific instructions)",
                            modifier = Modifier.padding(top = 10.dp)
                        )
                        Button(
                            enabled = isUploadPhotoButtonEnabled,
                            modifier = Modifier.padding(top = 10.dp),
                            onClick = {
                                scope.launch {
                                    optionsSheetState.show()
                                    showOptionsBottomSheet = true
                                }
                            }
                        ) {
                            Text("Upload/Take photo")
                        }
                        Row {
                            AssistChip(
                                enabled = isViewPhotoButtonEnabled,
                                onClick = { photoHandler.openJpg(context, photoUrl, localFileNameJPG)  },
                                label = {
                                    Text(text = if (isViewPhotoButtonEnabled) originalFileNameJPG else "No photo uploaded")
                                },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Photo,
                                        contentDescription = "Open Photo",
                                        tint = Color(0xFF054BC5)
                                    )
                                }
                            )
                            IconButton(
                                enabled = isViewPhotoButtonEnabled,
                                onClick = {
                                    parkingSpotViewModel.deleteParkingSpotPicture(id.toString())
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Delete Photo"
                                )
                            }
                        }
                        PDFFilePicker(
                            isButtonEnabled = isUploadPDFButtonEnabled,
                            onFilePicked = { documentUri ->
                                documentUri?.let {
                                    originalFileNamePDF = it.lastPathSegment ?: "unknown.pdf"
                                    parkingSpotViewModel.uploadDocument(id.toString(), documentUri, originalFileNamePDF)
                                }
                            },
                            modifier = Modifier.padding(top = 10.dp)
                        )
                        Row {
                            AssistChip(
                                enabled = isViewPDFButtonEnabled,
                                onClick = { openPdf(context, documentUrl, localFileNamePDF) },
                                label = {
                                    Text(text = if (isViewPDFButtonEnabled) originalFileNamePDF else "No document uploaded")
                                },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.PictureAsPdf,
                                        contentDescription = "Open PDF",
                                        tint = Color(0xFF9E1B1B)
                                    )
                                }
                            )
                            IconButton(
                                enabled = isViewPDFButtonEnabled,
                                onClick = {
                                    parkingSpotViewModel.deletePdfDocument(id.toString())
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Delete PDF"
                                )
                            }
                        }
                    }
                }
            }
        }
        if (showOptionsBottomSheet) {
            ImageOptionsBottomSheet(
                pictureUrl = photoUrl,
                sheetState = optionsSheetState,
                onDismiss = { showOptionsBottomSheet = false },
                onTakePhoto = {
                    when (PackageManager.PERMISSION_GRANTED) {
                        ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) -> {
                            // Permission is already granted; proceed with taking a photo.
                            photoHandler.takePhoto(takePictureLauncher)
                        }
                        else -> {
                            // Permission is not granted; request it.
                            requestCameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                        }
                    }
                    scope.launch {
                        optionsSheetState.hide()
                        showOptionsBottomSheet = false
                    }
                },
                onChooseFromGallery = {
                    openGalleryLauncher.launch("image/*")
                    scope.launch {
                        optionsSheetState.hide()
                        showOptionsBottomSheet = false
                    }
                },
                onDeletePhoto = {}
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            if (addParkingSpotState.value?.isLoading == true || uploadDocumentState.value?.isLoading == true) {
                CircularProgressIndicator()
            }
        }
        LaunchedEffect(key1 = addParkingSpotState.value?.isSuccess) {
            scope.launch {
                if (addParkingSpotState.value?.isSuccess?.isNotEmpty() == true) {
                    val success = addParkingSpotState.value?.isSuccess
                    Toast.makeText(context, "$success", Toast.LENGTH_LONG).show()
                    navController.navigate(Screens.UserProfileScreen.route)
                }
            }
        }
        LaunchedEffect(key1 = addParkingSpotState.value?.isError) {
            scope.launch {
                if (addParkingSpotState.value?.isError?.isNotEmpty() == true) {
                    val error = addParkingSpotState.value?.isError
                    Toast.makeText(context, "$error", Toast.LENGTH_LONG).show()
                }
            }
        }
        LaunchedEffect(key1 = uploadDocumentState.value?.isSuccess) {
            scope.launch {
                if (uploadDocumentState.value?.isSuccess?.isNotEmpty() == true) {
                    val success = uploadDocumentState.value?.isSuccess
                    Toast.makeText(context, "$success", Toast.LENGTH_LONG).show()
                    documentUrl = uploadDocumentState.value!!.documentUrl.toString()
                    localFileNamePDF = uploadDocumentState.value!!.localFileName.toString()
                    isViewPDFButtonEnabled = true
                }
            }
        }
        LaunchedEffect(key1 = uploadDocumentState.value?.isError) {
            scope.launch {
                if (uploadDocumentState.value?.isError?.isNotEmpty() == true) {
                    val error = uploadDocumentState.value?.isError
                    Toast.makeText(context, "$error", Toast.LENGTH_LONG).show()
                }
            }
        }
        LaunchedEffect(key1 = deletePdfState.value?.isSuccess) {
            scope.launch {
                if (deletePdfState.value?.isSuccess?.isNotEmpty() == true) {
                    val success = deletePdfState.value?.isSuccess
                    Log.i("ParkingSpotDetailsScreen", "Success: $success")
                    isViewPDFButtonEnabled = false
                    documentUrl = ""
                    originalFileNamePDF = ""
                    localFileNamePDF = ""
                }
            }
        }
        LaunchedEffect(key1 = deletePdfState.value?.isError) {
            scope.launch {
                if (deletePdfState.value?.isError?.isNotEmpty() == true) {
                    val error = deletePdfState.value?.isError
                    Log.e("ParkingSpotDetailsScreen", "Error: $error")
                }
            }
        }
        LaunchedEffect(key1 = addParkingPictureState.value?.isSuccess) {
            scope.launch {
                if (addParkingPictureState.value?.isSuccess?.isNotEmpty() == true) {
                    val success = addParkingPictureState.value?.isSuccess
                    Toast.makeText(context, "$success", Toast.LENGTH_LONG).show()
                    photoUrl = addParkingPictureState.value!!.photoUrl.toString()
                    localFileNameJPG = addParkingPictureState.value!!.localFileName.toString()
                    isViewPhotoButtonEnabled = true
                }
            }
        }
        LaunchedEffect(key1 = addParkingPictureState.value?.isError) {
            scope.launch {
                if (addParkingPictureState.value?.isError?.isNotEmpty() == true) {
                    val error = addParkingPictureState.value?.isError
                    Toast.makeText(context, "$error", Toast.LENGTH_LONG).show()
                }
            }
        }
        LaunchedEffect(key1 = deleteParkingPictureState.value?.isSuccess) {
            scope.launch {
                if (deleteParkingPictureState.value?.isSuccess?.isNotEmpty() == true) {
                    val success = deleteParkingPictureState.value?.isSuccess
                    Log.i("ParkingSpotDetailsScreen", "Success: $success")
                    isViewPhotoButtonEnabled = false
                    photoUrl = ""
                    originalFileNameJPG = ""
                    localFileNameJPG = ""
                }
            }
        }
        LaunchedEffect(key1 = deleteParkingPictureState.value?.isError) {
            scope.launch {
                if (deleteParkingPictureState.value?.isError?.isNotEmpty() == true) {
                    val error = deleteParkingPictureState.value?.isError
                    Log.e("ParkingSpotDetailsScreen", "Error: $error")
                }
            }
        }
    }
}