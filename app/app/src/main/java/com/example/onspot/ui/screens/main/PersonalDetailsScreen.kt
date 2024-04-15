package com.example.onspot.ui.screens.main

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.clickable
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
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.onspot.ui.components.CustomButton
import com.example.onspot.ui.components.CustomTextField
import com.example.onspot.ui.components.CustomTopBar
import com.example.onspot.utils.Resource
import com.example.onspot.viewmodel.UserProfileViewModel

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun PersonalDetailsScreen(
    navController: NavController,
    userProfileViewModel: UserProfileViewModel = viewModel(),
) {
    val userDetails by userProfileViewModel.userDetails.collectAsState()
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var isButtonEnabled by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val focusManager = LocalFocusManager.current

    fun clearFocus() {
        focusManager.clearFocus()
    }

    when (userDetails) {
        is Resource.Loading -> {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                CircularProgressIndicator()
            }
        }
        is Resource.Success -> {
            firstName = userDetails.data!!.firstName
            lastName = userDetails.data!!.lastName
            email = userDetails.data!!.email
        }
        is Resource.Error -> {
            LaunchedEffect(key1 = true) {
                Toast.makeText(context, "Error fetching user details", Toast.LENGTH_LONG).show()
            }
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Scaffold(
            topBar = { CustomTopBar(title = "Personal details") },
            bottomBar = {
                CustomButton(
                    onClick = {},
                    buttonText = "Save",
                    enabled = isButtonEnabled,
                    modifier = Modifier
                        .padding(bottom = 30.dp)
                )
            }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable(onClick = { clearFocus() })
                    .padding(horizontal = 30.dp, vertical = 30.dp),
            ) {
                CustomTextField(
                    value = firstName,
                    onValueChange = {
                        firstName = it
                        isButtonEnabled = true
                    },
                    label = "First name"
                )
                CustomTextField(
                    value = lastName,
                    onValueChange = {
                        lastName = it
                        isButtonEnabled = true
                    },
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