package com.example.onspot.ui.screens.main

import android.annotation.SuppressLint
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.onspot.data.model.ParkingSpot
import com.example.onspot.ui.components.BottomNavigationBar
import com.example.onspot.ui.components.CustomTopBar
import com.example.onspot.ui.components.OfferBox
import com.example.onspot.ui.components.ParkingMapOffer
import com.example.onspot.utils.Resource
import com.example.onspot.viewmodel.OfferViewModel

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun OfferScreen(
    navController: NavController,
    offerViewModel: OfferViewModel = viewModel()
) {
    var selectedItemIndex by rememberSaveable { mutableStateOf(0) }
    val showOfferBox = rememberSaveable { mutableStateOf(false) }
    val showMap = rememberSaveable { mutableStateOf(false) }
    var showConfirmation by rememberSaveable { mutableStateOf(false) }

    val parkingSpots by offerViewModel.parkingSpots.collectAsState()
    lateinit var parkingSpotsList : List<ParkingSpot>

    val marker = offerViewModel.markerData.observeAsState()

    val context = LocalContext.current

    when (parkingSpots) {
        is Resource.Loading -> {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                CircularProgressIndicator()
            }
        }
        is Resource.Success -> {
            parkingSpotsList = parkingSpots.data!!
            showOfferBox.value = true
        }
        is Resource.Error -> {
            LaunchedEffect(key1 = true) {
                Toast.makeText(context, "Error fetching parking spots", Toast.LENGTH_LONG).show()
            }
        }
    }
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
            },
            backgroundColor = Color.LightGray
        ) { paddingValues ->
            if (showOfferBox.value) {
                Box(
                    modifier = Modifier
                        .padding(paddingValues)
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    OfferBox(parkingSpotsList, showOfferBox, showMap, offerViewModel)
                }
            }
            if (showMap.value) {
                ParkingMapOffer(offerViewModel = offerViewModel, modifier = Modifier.padding(paddingValues))
            }
        }
    }
}
