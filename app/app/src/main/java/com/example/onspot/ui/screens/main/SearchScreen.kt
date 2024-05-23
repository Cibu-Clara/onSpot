package com.example.onspot.ui.screens.main

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.onspot.ui.components.BottomNavigationBar
import com.example.onspot.ui.components.CustomTopBar
import com.example.onspot.ui.components.ParkingMapSearch
import com.example.onspot.viewmodel.OfferViewModel
import com.google.android.libraries.places.api.net.PlacesClient

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun SearchScreen(
    navController: NavController,
    placesClient: PlacesClient,
    offerViewModel: OfferViewModel = viewModel()
) {
    var selectedItemIndex by rememberSaveable { mutableStateOf(1) }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Scaffold(
            topBar = {
                CustomTopBar(title = "Search parking spot")
            },
            bottomBar = {
                BottomNavigationBar(
                    navController = navController,
                    selectedItemIndex = selectedItemIndex,
                    onItemSelected = { selectedItemIndex = it }
                )
            }
        ) { innerPadding ->
            ParkingMapSearch(
                offerViewModel = offerViewModel,
                placesClient = placesClient,
                modifier = Modifier.padding(innerPadding)
            )
        }
    }
}