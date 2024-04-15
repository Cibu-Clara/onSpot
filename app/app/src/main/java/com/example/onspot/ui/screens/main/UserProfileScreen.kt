package com.example.onspot.ui.screens.main

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Environment
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.Scaffold
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.Surface
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import android.Manifest
import android.widget.Toast
import com.example.onspot.ui.components.AboutYouTab
import com.example.onspot.ui.components.BottomNavigationBar
import com.example.onspot.ui.components.CustomTabView
import com.example.onspot.ui.components.CustomTopBar
import com.example.onspot.ui.components.DeletePhotoBottomSheet
import com.example.onspot.ui.components.ImageOptionsBottomSheet
import com.example.onspot.ui.components.SettingsTab
import com.example.onspot.viewmodel.UserProfileViewModel
import kotlinx.coroutines.launch
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun UserProfileScreen(
    navController: NavController,
    userProfileViewModel: UserProfileViewModel = viewModel()
) {
    var selectedItemIndex by rememberSaveable { mutableStateOf(4) }
    var selectedTabIndex by rememberSaveable { mutableStateOf(0) }
    var showOptionsBottomSheet by rememberSaveable { mutableStateOf(false) }
    var showDeleteBottomSheet by rememberSaveable { mutableStateOf(false) }
    val optionsSheetState = rememberModalBottomSheetState()
    val deleteSheetState = rememberModalBottomSheetState()

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val uri = rememberSaveable { mutableStateOf<Uri?>(null) }

    val takePictureLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { isSuccess ->
        if (isSuccess) {
            // The image was saved at the Uri provided, update the profile picture
            uri.value?.let { savedUri ->
                userProfileViewModel.updateUserProfilePictureUrl(savedUri)
            }
        }
    }

    // Function to launch the camera
    fun takePhoto() {
        // Get the external files directory for saving the picture
        val photoFile = File(
            context.getExternalFilesDir(Environment.DIRECTORY_PICTURES),
            "profile_picture_${System.currentTimeMillis()}.jpg"
        ).apply {
            createNewFile()
        }
        val photoUri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.provider",
            photoFile
        )
        uri.value = photoUri
        takePictureLauncher.launch(photoUri)
    }

    val requestCameraPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            // Permission was granted, you can proceed with taking a photo.
            takePhoto()
        } else {
            // Permission was denied. Handle the denial properly, e.g., show an explanation.
            Toast.makeText(context, "Camera permission is needed to take photos", Toast.LENGTH_SHORT).show()
        }
    }

    val openGalleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { galleryUri: Uri? ->
            galleryUri?.let {
                userProfileViewModel.updateUserProfilePictureUrl(galleryUri)
            }
        }
    )
    
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Scaffold(
            topBar = { CustomTopBar(title = "Your profile") },
            bottomBar = {
                BottomNavigationBar(
                    navController = navController,
                    selectedItemIndex = selectedItemIndex,
                    onItemSelected = { selectedItemIndex = it }
                )
            }
        ) {
            Column {
                CustomTabView(
                    tabs = listOf("About you", "Settings"),
                    selectedTabIndex = selectedTabIndex,
                    onTabSelected = { selectedTabIndex = it }
                )
                when (selectedTabIndex) {
                    0 -> {
                        AboutYouTab(
                            navController,
                            userProfileViewModel
                        ) {
                            coroutineScope.launch {
                                optionsSheetState.show()
                                showOptionsBottomSheet = true
                            }
                        }
                    }
                    1 -> { SettingsTab(navController, userProfileViewModel) }
                }
            }
        }

        if (showOptionsBottomSheet) {
            ImageOptionsBottomSheet(
                profilePictureUrl = userProfileViewModel.userDetails.value.data?.profilePictureUrl ?: "",
                sheetState = optionsSheetState,
                onDismiss = { showOptionsBottomSheet = false },
                onTakePhoto = {
                    when (PackageManager.PERMISSION_GRANTED) {
                        ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) -> {
                            // Permission is already granted; proceed with taking a photo.
                            takePhoto()
                        }
                        else -> {
                            // Permission is not granted; request it.
                            requestCameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                        }
                    }
                    coroutineScope.launch {
                        optionsSheetState.hide()
                        showOptionsBottomSheet = false
                    }
                },
                onChooseFromGallery = {
                    openGalleryLauncher.launch("image/*")
                    coroutineScope.launch {
                        optionsSheetState.hide()
                        showOptionsBottomSheet = false
                    }
                },
                onDeletePhoto = {
                    coroutineScope.launch {
                        optionsSheetState.hide()
                        showOptionsBottomSheet = false
                    }
                    coroutineScope.launch {
                        deleteSheetState.show()
                        showDeleteBottomSheet = true
                    }
                }
            )
        }
        if (showDeleteBottomSheet) {
            DeletePhotoBottomSheet(
                sheetState = deleteSheetState,
                onDismiss = { showDeleteBottomSheet = false },
                onDeletePhoto = {
                    userProfileViewModel.deleteUserProfilePictureUrl()
                    coroutineScope.launch {
                        deleteSheetState.hide()
                        showDeleteBottomSheet = false
                    }
                },
                onCancel = {
                    coroutineScope.launch {
                        deleteSheetState.hide()
                        showDeleteBottomSheet = false
                    }
                }
            )
        }
    }

}