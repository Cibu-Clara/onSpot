package com.example.onspot.ui.screens.main

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.onspot.navigation.Screens
import com.example.onspot.ui.components.CustomButton
import com.example.onspot.ui.components.CustomTextField
import com.example.onspot.ui.components.CustomTopBar
import com.example.onspot.utils.Resource
import com.example.onspot.viewmodel.UserProfileViewModel
import kotlinx.coroutines.launch

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun PersonalDetailsScreen(
    navController: NavController,
    userProfileViewModel: UserProfileViewModel = viewModel(),
) {
    val userDetails by userProfileViewModel.userDetails.collectAsState()
    val updateUserDetailsState = userProfileViewModel.updateUserDetailsState.collectAsState(initial = null)

    var firstName by rememberSaveable { mutableStateOf("") }
    var lastName by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var hasChanged by remember { mutableStateOf(false) }
    val isButtonEnabled = firstName.isNotBlank() && lastName.isNotBlank() && hasChanged

    val scope = rememberCoroutineScope()
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
            if (firstName.isEmpty() && lastName.isEmpty() && email.isEmpty()) {
                userDetails.data?.let { user ->
                    firstName = user.firstName
                    lastName = user.lastName
                    email = user.email
                }
            }
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
            topBar = { CustomTopBar(title = "Personal details", onBackClick = { navController.popBackStack() })},
            bottomBar = {
                CustomButton(
                    onClick = {
                        scope.launch {
                            userProfileViewModel.updateUserDetails(firstName, lastName)
                        }
                    },
                    buttonText = "Save",
                    enabled = isButtonEnabled,
                    modifier = Modifier.padding(bottom = 30.dp)
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
                        hasChanged = true
                    },
                    label = "First name"
                )
                CustomTextField(
                    value = lastName,
                    onValueChange = {
                        lastName = it
                        hasChanged = true
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
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        if (updateUserDetailsState.value?.isLoading == true) {
            CircularProgressIndicator()
        }
    }
    LaunchedEffect(key1 = updateUserDetailsState.value?.isSuccess) {
        scope.launch {
            if (updateUserDetailsState.value?.isSuccess?.isNotEmpty() == true) {
                val success = updateUserDetailsState.value?.isSuccess
                Toast.makeText(context, "$success", Toast.LENGTH_LONG).show()
                navController.navigate(Screens.UserProfileScreen.route)
            }
        }
    }
    LaunchedEffect(key1 = updateUserDetailsState.value?.isError) {
        scope.launch {
            if (updateUserDetailsState.value?.isError?.isNotEmpty() == true) {
                val error = updateUserDetailsState.value?.isError
                Toast.makeText(context, "$error", Toast.LENGTH_LONG).show()
            }
        }
    }
}