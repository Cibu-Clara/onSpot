package com.example.onspot.ui.screens.auth

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.onspot.navigation.Screens
import com.example.onspot.ui.components.CustomAlertDialog
import com.example.onspot.ui.components.CustomButton
import com.example.onspot.ui.components.CustomPasswordField
import com.example.onspot.ui.components.CustomTopBar
import com.example.onspot.ui.theme.RegularFont
import com.example.onspot.viewmodel.UserProfileViewModel
import kotlinx.coroutines.launch

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun ChangePasswordScreen(
    navController: NavController,
    userProfileViewModel: UserProfileViewModel = viewModel()
) {
    var oldPassword by rememberSaveable { mutableStateOf("") }
    var newPassword by rememberSaveable { mutableStateOf("") }
    var confirmPassword by rememberSaveable { mutableStateOf("") }
    val isButtonEnabled = oldPassword.isNotBlank() && newPassword.isNotBlank() && confirmPassword.isNotBlank()
    var showPasswordMismatchDialog by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val state = userProfileViewModel.changePasswordState.collectAsState(initial = null)

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Scaffold(
            topBar = {
                CustomTopBar(title = "Change your password")
            },
            bottomBar = {
                CustomButton(
                    onClick = {
                        if (newPassword != confirmPassword) {
                            showPasswordMismatchDialog = true
                        } else {
                            scope.launch {
                                userProfileViewModel.changeUserPassword(oldPassword, newPassword)
                            }
                        }
                    },
                    buttonText = "Save",
                    enabled = isButtonEnabled,
                    modifier = Modifier
                        .padding(bottom = 30.dp)
                )
            }
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                Text(
                    text = "Choose a strong password with a mix of letters and numbers, including at least one uppercase — keeping your account safe is essential",
                    modifier = Modifier
                        .padding(30.dp),
                    fontFamily = RegularFont,
                    color = Color.Gray,
                    fontSize = 16.sp
                )
                Spacer(modifier = Modifier.weight(0.4f))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 30.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CustomPasswordField(
                        value = oldPassword,
                        onValueChange = { oldPassword = it },
                        label = "Old password",
                        modifier = Modifier.padding(top = 10.dp)
                    )
                    CustomPasswordField(
                        value = newPassword,
                        onValueChange = { newPassword = it },
                        label = "New password",
                        modifier = Modifier.padding(top = 10.dp)
                    )
                    CustomPasswordField(
                        value = confirmPassword,
                        onValueChange = { confirmPassword = it },
                        label = "Confirm new password",
                        modifier = Modifier.padding(top = 10.dp)
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
            }
        }
    }
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        if (state.value?.isLoading == true) {
            CircularProgressIndicator()
        }
    }
    if (showPasswordMismatchDialog) {
        CustomAlertDialog(
            title = "Password mismatch",
            text = "The passwords you entered do not match. Please try again.",
            onConfirm = {
                confirmPassword = ""
                showPasswordMismatchDialog = false
            },
            onDismiss = { showPasswordMismatchDialog = false }
        )
    }
    LaunchedEffect(key1 = state.value?.isSuccess) {
        scope.launch {
            if (state.value?.isSuccess?.isNotEmpty() == true) {
                val success = state.value?.isSuccess
                Toast.makeText(context, "$success", Toast.LENGTH_LONG).show()
                navController.navigate(Screens.UserProfileScreen.route)
            }
        }
    }
    LaunchedEffect(key1 = state.value?.isError) {
        scope.launch {
            if (state.value?.isError?.isNotEmpty() == true) {
                val error = state.value?.isError
                Toast.makeText(context, "$error", Toast.LENGTH_LONG).show()
            }
        }
    }
}