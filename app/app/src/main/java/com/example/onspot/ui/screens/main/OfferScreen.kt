package com.example.onspot.ui.screens.main

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.fillMaxSize
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.Scaffold
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.Surface
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.example.onspot.ui.components.BottomNavigationBar
import com.example.onspot.ui.components.CustomTopBar

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun OfferScreen(
    navController: NavController
) {
    var selectedItemIndex by rememberSaveable { mutableStateOf(1) }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Scaffold(
            topBar = {
                CustomTopBar(title = "Offer your parking spot",)
            },
            bottomBar = {
                BottomNavigationBar(
                    navController = navController,
                    selectedItemIndex = selectedItemIndex,
                    onItemSelected = { selectedItemIndex = it}
                )
            }
        ) {
        }
    }
}