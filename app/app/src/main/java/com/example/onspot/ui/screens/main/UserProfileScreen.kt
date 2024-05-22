package com.example.onspot.ui.screens.main

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.net.Uri
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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import android.Manifest
import android.widget.Toast
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.remember
import com.example.onspot.ui.components.AboutYouTab
import com.example.onspot.ui.components.BottomNavigationBar
import com.example.onspot.ui.components.CustomTabView
import com.example.onspot.ui.components.CustomTopBar
import com.example.onspot.ui.components.DeletePhotoBottomSheet
import com.example.onspot.ui.components.ImageOptionsBottomSheet
import com.example.onspot.ui.components.SettingsTab
import com.example.onspot.utils.PhotoHandler
import com.example.onspot.viewmodel.UserProfileViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun UserProfileScreen(
    navController: NavController,
    userProfileViewModel: UserProfileViewModel = viewModel()
) {
    var selectedItemIndex by rememberSaveable { mutableStateOf(3) }
    var selectedTabIndex by rememberSaveable { mutableStateOf(0) }
    var showOptionsBottomSheet by rememberSaveable { mutableStateOf(false) }
    var showDeleteBottomSheet by rememberSaveable { mutableStateOf(false) }
    val optionsSheetState = rememberModalBottomSheetState()
    val deleteSheetState = rememberModalBottomSheetState()

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val photoHandler = remember { PhotoHandler(context) }

    val takePictureLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { isSuccess ->
        if (isSuccess) {
            photoHandler.photoUri?.let { savedUri ->
                userProfileViewModel.updateUserProfilePictureUrl(savedUri)
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
                userProfileViewModel.updateUserProfilePictureUrl(galleryUri)
            }
        }
    )
    
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.tertiaryContainer
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
            Column(modifier = Modifier.padding(it)) {
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
                pictureUrl = userProfileViewModel.userDetails.value.data?.profilePictureUrl ?: "",
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