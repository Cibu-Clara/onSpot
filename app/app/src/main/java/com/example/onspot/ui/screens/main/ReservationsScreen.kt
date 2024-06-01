package com.example.onspot.ui.screens.main

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.example.onspot.ui.components.AboutYouTab
import com.example.onspot.ui.components.BottomNavigationBar
import com.example.onspot.ui.components.CustomTabView
import com.example.onspot.ui.components.CustomTopBar
import com.example.onspot.ui.components.SettingsTab
import kotlinx.coroutines.launch

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun ReservationsScreen(
    navController: NavController
) {
    var selectedItemIndex by rememberSaveable { mutableStateOf(2) }
    var selectedTabIndex by rememberSaveable { mutableStateOf(0) }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Scaffold(
            topBar = {
                CustomTopBar(title = "Reservations",)
            },
            bottomBar = {
                BottomNavigationBar(
                    navController = navController,
                    selectedItemIndex = selectedItemIndex,
                    onItemSelected = { selectedItemIndex = it}
                )
            }
        ) { paddingValues ->
            Column(modifier = Modifier.padding(paddingValues)) {
                CustomTabView(
                    tabs = listOf("Your posts", "Your requests"),
                    selectedTabIndex = selectedTabIndex,
                    onTabSelected = { selectedTabIndex = it }
                )
                when (selectedTabIndex) {
                    0 -> {
                    }
                    1 -> {  }
                }
            }
        }
    }
}