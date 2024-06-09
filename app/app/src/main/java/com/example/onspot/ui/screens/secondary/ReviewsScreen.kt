package com.example.onspot.ui.screens.secondary

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.onspot.ui.components.CustomTopBar
import com.example.onspot.viewmodel.ReviewViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun ReviewsScreen(
    navController: NavController,
    userId: String,
    reviewViewModel: ReviewViewModel = viewModel()
) {
    val currentUserId = Firebase.auth.currentUser?.uid

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Scaffold(
            topBar = {
                CustomTopBar(
                    title = if (userId == currentUserId) "Your reviews" else "Someone's reviews",
                    onBackClick = { navController.popBackStack() }
                )
            }
        ) {
        }
    }
}