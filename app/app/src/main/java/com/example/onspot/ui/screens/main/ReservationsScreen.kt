package com.example.onspot.ui.screens.main

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material3.CircularProgressIndicator
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
import com.example.onspot.data.model.Marker
import com.example.onspot.data.model.Reservation
import com.example.onspot.data.model.ReservationDetails
import com.example.onspot.ui.components.BottomNavigationBar
import com.example.onspot.ui.components.CustomTabView
import com.example.onspot.ui.components.CustomTopBar
import com.example.onspot.ui.components.ListingsTab
import com.example.onspot.ui.components.RequestsTab
import com.example.onspot.utils.Resource
import com.example.onspot.viewmodel.ParkingSpotViewModel
import com.example.onspot.viewmodel.ReservationViewModel

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun ReservationsScreen(
    navController: NavController,
    reservationViewModel: ReservationViewModel = viewModel()
) {
    var selectedItemIndex by rememberSaveable { mutableStateOf(2) }
    var selectedTabIndex by rememberSaveable { mutableStateOf(0) }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Scaffold(
            topBar = {
                CustomTopBar(title = "Reservations")
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
            Column(modifier = Modifier.padding(paddingValues)) {
                CustomTabView(
                    tabs = listOf("Your listings", "Your requests"),
                    selectedTabIndex = selectedTabIndex,
                    onTabSelected = { selectedTabIndex = it }
                )
                when (selectedTabIndex) {
                    0 -> {
                        ListingsTab(
                            reservationViewModel = reservationViewModel
                        )
                    }
                    1 -> {
                        RequestsTab(
                            reservationViewModel = reservationViewModel
                        )
                    }
                }
            }
        }
    }
}