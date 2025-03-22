package com.example.onspot.ui.screens.main

import android.annotation.SuppressLint
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material3.CircularProgressIndicator
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
import com.example.onspot.data.model.Marker
import com.example.onspot.ui.components.BottomNavigationBar
import com.example.onspot.ui.components.CustomTopBar
import com.example.onspot.ui.components.ParkingMapSearch
import com.example.onspot.utils.Resource
import com.example.onspot.viewmodel.SearchViewModel
import com.google.android.libraries.places.api.net.PlacesClient

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun SearchScreen(
    navController: NavController,
    placesClient: PlacesClient,
    searchViewModel: SearchViewModel = viewModel()
) {
    var selectedItemIndex by rememberSaveable { mutableStateOf(1) }

    val markers by searchViewModel.markers.collectAsState()
    var markersList: List<Marker>? = null

    val context = LocalContext.current

    when (markers) {
        is Resource.Loading -> {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                CircularProgressIndicator()
            }
        }
        is Resource.Success -> {
            markersList = markers.data ?: emptyList()
        }
        is Resource.Error -> {
            LaunchedEffect(key1 = true) {
                Toast.makeText(context, "Error fetching markers", Toast.LENGTH_LONG).show()
            }
        }
    }

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
            },
            backgroundColor = Color.LightGray
        ) { paddingValues ->
            ParkingMapSearch(
                searchViewModel = searchViewModel,
                placesClient = placesClient,
                markersList = markersList ?: emptyList(),
                navController = navController,
                modifier = Modifier.padding(paddingValues)
            )
        }
    }
}