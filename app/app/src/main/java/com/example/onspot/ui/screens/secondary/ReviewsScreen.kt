package com.example.onspot.ui.screens.secondary

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.example.onspot.ui.components.CustomTopBar

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun ReviewsScreen(
    navController: NavController
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Scaffold(
            topBar = {
                CustomTopBar(
                    title = "Your reviews",
                    onBackClick = { navController.popBackStack() }
                )
            }
        ) {
        }
    }
}