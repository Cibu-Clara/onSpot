package com.example.parkingspots.ui.screens.main

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.parkingspots.ui.components.BottomNavigationBar
import com.example.parkingspots.ui.components.CustomTabView
import com.example.parkingspots.ui.components.CustomTopBar
import com.example.parkingspots.ui.theme.purple

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun ProfileScreen(
    navController: NavController
) {
    var selectedItemIndex by rememberSaveable { mutableStateOf(4) }
    var selectedTabIndex by rememberSaveable { mutableStateOf(0) }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Scaffold(
            topBar = {
                CustomTopBar(title = "Your profile")
            },
            bottomBar = {
                BottomNavigationBar(
                    navController = navController,
                    selectedItemIndex = selectedItemIndex,
                    onItemSelected = { selectedItemIndex = it }
                )
            }
        ) {
            Column {
                CustomTabView(
                    tabs = listOf("About you", "Settings"),
                    selectedTabIndex = selectedTabIndex,
                    onTabSelected = { selectedTabIndex = it }
                )
                when (selectedTabIndex) {
                    0 -> {
                        // TODO: About You tab content
                    }
                    1 -> {
                        SettingsList(navController)
                    }
                }
            }
        }
    }
}

@Composable
fun SettingsList(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val settingsOptions = listOf("Change password", "Delete account", "Log out")
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 15.dp, vertical = 15.dp),
    ) {
        settingsOptions.forEachIndexed() { index, option ->
            Row(
                modifier = Modifier
                    .clickable { onOptionSelected(option, navController) }
                    .padding(bottom = 8.dp)
            ) {
                Text(
                    text = option,
                    fontSize = 20.sp,
                    color = if (option == "Delete account" || option == "Log out") purple else Color.DarkGray,
                    modifier = Modifier.weight(0.9f)
                )
                if (index < settingsOptions.size - 2) {
                    Icon(
                        imageVector = Icons.Default.ArrowForwardIos,
                        contentDescription = null,
                        modifier = Modifier.weight(0.1f),
                        tint = Color.DarkGray
                    )
                }
            }
            if (index < settingsOptions.size - 2) {
                Divider()
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

private fun onOptionSelected(option: String, navController: NavController) {
    when (option) {
        "Change password" -> {
            navController.navigate("change_password_route")
        }

        "Delete account" -> {
        }

        "Log out" -> {
            navController.navigate("login_route") {
                popUpTo("profile_route") { inclusive = true }
            }
        }
    }
}