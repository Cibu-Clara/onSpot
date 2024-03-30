package com.example.onspot.ui.screens.main

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.onspot.navigation.Screens
import com.example.onspot.ui.components.BottomNavigationBar
import com.example.onspot.ui.components.CustomAlertDialog
import com.example.onspot.ui.components.CustomInputDialog
import com.example.onspot.ui.components.CustomTabView
import com.example.onspot.ui.components.CustomTopBar
import com.example.onspot.ui.theme.purple
import com.example.onspot.viewmodel.UserProfileViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import kotlinx.coroutines.launch

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun UserProfileScreen(
    navController: NavController,
    userProfileViewModel: UserProfileViewModel = viewModel()
) {
    var selectedItemIndex by rememberSaveable { mutableStateOf(4) }
    var selectedTabIndex by rememberSaveable { mutableStateOf(0) }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Scaffold(
            topBar = { CustomTopBar(title = "Your profile") },
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
                    1 -> { SettingsList(navController, userProfileViewModel) }
                }
            }
        }
    }
}

@Composable
fun SettingsList(
    navController: NavController,
    userProfileViewModel: UserProfileViewModel,
    modifier: Modifier = Modifier
) {
    val settingsOptions = listOf("Change password", "Delete account", "Log out")

    var showLogoutDialog by rememberSaveable { mutableStateOf(false) }
    var showDeleteAccountDialog by rememberSaveable { mutableStateOf(false) }
    var showPasswordConfirmationDialog by rememberSaveable { mutableStateOf(false) }
    var showAuthenticationWarningDialog by rememberSaveable { mutableStateOf(false) }
    var warningMessage by rememberSaveable { mutableStateOf("") }

    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val deleteAccountState = userProfileViewModel.deleteAccountState.collectAsState(initial = null)
    val logoutState = userProfileViewModel.logoutState.collectAsState(initial = null)

    val googleSignInClient = GoogleSignIn.getClient(context, GoogleSignInOptions.DEFAULT_SIGN_IN)

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 15.dp, vertical = 15.dp),
    ) {
        settingsOptions.forEachIndexed { index, option ->
            Row(
                modifier = Modifier
                    .clickable {
                        when (option) {
                            "Change password" -> {
                                userProfileViewModel.navigateForPasswordChange { canChangePass, message ->
                                    if (canChangePass) {
                                        navController.navigate(Screens.ChangePasswordScreen.route)
                                    } else {
                                        warningMessage = message ?: "Please try again."
                                        showAuthenticationWarningDialog = true
                                    }
                                }
                            }
                            "Delete account" -> {
                                userProfileViewModel.verifyAuthProvider { isEmailAuthenticated ->
                                    if (isEmailAuthenticated) {
                                        showPasswordConfirmationDialog = true
                                    } else {
                                        showDeleteAccountDialog = true
                                    }
                                }
                            }
                            "Log out" -> { showLogoutDialog = true }
                        }
                    }
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
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            if (deleteAccountState.value?.isLoading == true || logoutState.value?.isLoading == true) {
                CircularProgressIndicator()
            }
        }
    }
    if (showLogoutDialog) {
        CustomAlertDialog(
            title = "Sign out confirmation",
            text = "Are you sure you want to log out?",
            confirmButtonText = "Yes",
            dismissButtonText = "No",
            onConfirm = {
                userProfileViewModel.logoutUser(googleSignInClient)
                showLogoutDialog = false
            },
            onDismiss = { showLogoutDialog = false }
        )
    }
    if (showPasswordConfirmationDialog) {
        CustomInputDialog(
            title = "Password Confirmation",
            text = "You need to enter your password in order to proceed with this action.",
            label = "Password",
            onConfirm = { enteredPassword ->
                userProfileViewModel.verifyPassword(enteredPassword) {isVerified ->
                    if (isVerified) {
                        showPasswordConfirmationDialog = false
                        showDeleteAccountDialog = true
                    } else {
                        Toast.makeText(context, "Incorrect password. Please try again.", Toast.LENGTH_LONG).show()
                    }
                }
            },
            onDismiss = {
                showPasswordConfirmationDialog = false
            },
            visualTransformation = PasswordVisualTransformation(),
            keyboardType = KeyboardType.Password,
        )
    }
    if (showDeleteAccountDialog) {
        CustomAlertDialog(
            title = "Delete account confirmation",
            text = "Are you sure you want to permanently delete your account? This action cannot be undone!",
            confirmButtonText = "Yes",
            dismissButtonText = "No",
            onConfirm = {
                scope.launch {
                    userProfileViewModel.deleteUserAccount(googleSignInClient)
                    showDeleteAccountDialog = false
                }
            },
            onDismiss = { showDeleteAccountDialog = false }
        )
    }
    if (showAuthenticationWarningDialog) {
        CustomAlertDialog(
            title = "Cannot perform action",
            text = warningMessage,
            onConfirm = { showAuthenticationWarningDialog = false },
            onDismiss = { showAuthenticationWarningDialog = false }
        )
    }
    LaunchedEffect(key1 = deleteAccountState.value?.isSuccess) {
        scope.launch {
            if (deleteAccountState.value?.isSuccess?.isNotEmpty() == true) {
                val success = deleteAccountState.value?.isSuccess
                navController.navigate(Screens.OpeningScreen.route)
                Toast.makeText(context, "$success", Toast.LENGTH_LONG).show()
            }
        }
    }
    LaunchedEffect(key1 = deleteAccountState.value?.isError) {
        scope.launch {
            if (deleteAccountState.value?.isError?.isNotEmpty() == true) {
                val error = deleteAccountState.value?.isError
                Toast.makeText(context, "$error", Toast.LENGTH_LONG).show()
            }
        }
    }
    LaunchedEffect(key1 = logoutState.value?.isSuccess) {
        scope.launch {
            if (logoutState.value?.isSuccess?.isNotEmpty() == true) {
                navController.navigate(Screens.SignInScreen.route)
            }
        }
    }
    LaunchedEffect(key1 = logoutState.value?.isError) {
        scope.launch {
            if (logoutState.value?.isError?.isNotEmpty() == true) {
                val error = logoutState.value?.isError
                Toast.makeText(context, "$error", Toast.LENGTH_LONG).show()
            }
        }
    }
}