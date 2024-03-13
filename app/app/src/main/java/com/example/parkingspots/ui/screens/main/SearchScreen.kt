package com.example.parkingspots.ui.screens.main

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.example.parkingspots.ui.components.BottomNavigationBar

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun SearchScreen(
    navController: NavController
) {
    var selectedItemIndex by rememberSaveable { mutableStateOf(2) }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Scaffold(
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