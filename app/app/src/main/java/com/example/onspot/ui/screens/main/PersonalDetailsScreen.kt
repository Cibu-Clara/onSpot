package com.example.onspot.ui.screens.main

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.onspot.ui.components.CustomTextField
import com.example.onspot.ui.components.CustomTopBar

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun PersonalDetailsScreen(
    navController: NavController
) {
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }

    val focusManager = LocalFocusManager.current

    fun clearFocus() {
        focusManager.clearFocus()
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Scaffold(
            topBar = { CustomTopBar(title = "Personal details") }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable(onClick = { clearFocus() })
                    .padding(horizontal = 30.dp, vertical = 30.dp),
            ) {
                CustomTextField(
                    value = firstName,
                    onValueChange = { firstName = it },
                    label = "First name"
                )
                CustomTextField(
                    value = lastName,
                    onValueChange = { lastName = it },
                    modifier = Modifier.padding(top = 10.dp),
                    label = "Last name"
                )
                CustomTextField(
                    value = email,
                    onValueChange = { email = it },
                    modifier = Modifier.padding(top = 10.dp),
                    label = "Email",
                    isEnabled = false
                )
            }


        }
    }
}