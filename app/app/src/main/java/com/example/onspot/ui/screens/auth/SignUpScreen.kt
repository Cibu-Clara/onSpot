package com.example.onspot.ui.screens.auth

import android.app.Activity
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.onspot.R
import com.example.onspot.viewmodel.SignUpViewModel
import com.example.onspot.navigation.Screens
import com.example.onspot.ui.components.CustomAlertDialog
import com.example.onspot.ui.components.CustomButton
import com.example.onspot.ui.components.CustomClickableText
import com.example.onspot.ui.components.CustomPasswordField
import com.example.onspot.ui.components.CustomTextField
import com.example.onspot.ui.components.DividerWithText
import com.example.onspot.ui.theme.RegularFont
import com.example.onspot.ui.theme.purple
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import kotlinx.coroutines.launch

@Composable
fun SignUpScreen(
    navController: NavController,
    signUpViewModel: SignUpViewModel = viewModel()
) {
    var firstName by rememberSaveable { mutableStateOf("") }
    var lastName by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var confirmPassword by rememberSaveable { mutableStateOf("") }

    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val signUpState = signUpViewModel.signUpState.collectAsState(initial = null)
    val googleSignUpState = signUpViewModel.googleSignUpState.collectAsState(initial = null)

    var showPasswordMismatchDialog by remember { mutableStateOf(false) }
    val isButtonEnabled = firstName.isNotBlank() && lastName.isNotBlank() && email.isNotBlank() && password.isNotBlank() && confirmPassword.isNotBlank()

    val focusManager = LocalFocusManager.current
    fun clearFocus() { focusManager.clearFocus() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .clickable (onClick = { clearFocus() })
            .padding(start = 30.dp, end = 30.dp, top = 30.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Hi there!",
            fontWeight = FontWeight.Bold,
            fontSize = 40.sp,
            color = purple,
            modifier = Modifier
                .align(Alignment.Start)
                .padding(bottom = 25.dp)
        )
        Text(
            text = "Enter your credentials to register",
            fontWeight = FontWeight.Medium,
            fontSize = 15.sp,
            color = Color.Gray,
            fontFamily = RegularFont
        )
        CustomTextField(
            value = firstName,
            onValueChange = { firstName = it },
            label = "First Name",
            modifier = Modifier.padding(top = 10.dp)
        )
        CustomTextField(
            value = lastName,
            onValueChange = { lastName = it },
            label = "Last Name",
            modifier = Modifier.padding(top = 10.dp)
        )
        CustomTextField(
            value = email,
            onValueChange = { email = it },
            label = "Email",
            keyboardType = KeyboardType.Email,
            modifier = Modifier.padding(top = 10.dp)
        )
        CustomPasswordField(
            value = password,
            onValueChange = { password = it },
            label = "Password",
            modifier = Modifier.padding(top = 10.dp)
        )
        CustomPasswordField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = "Confirm Password",
            modifier = Modifier.padding(top = 10.dp)
        )
        CustomButton(
            onClick = {
                if (password != confirmPassword) {
                    showPasswordMismatchDialog = true
                } else {
                    scope.launch {
                       signUpViewModel.registerUser(firstName = firstName, lastName = lastName, email = email, password = password)
                    }
                }
            },
            buttonText = "Sign Up",
            enabled = isButtonEnabled
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            if (signUpState.value?.isLoading == true || googleSignUpState.value?.isLoading == true) {
                CircularProgressIndicator()
            }
        }
        DividerWithText(text = "or connect with")
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            val googleSignUpLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.StartActivityForResult(),
                onResult = { result ->
                    if (result.resultCode == Activity.RESULT_OK) {
                        signUpViewModel.handleGoogleSignUpResult(result.data)
                    }
                }
            )
            IconButton(onClick = {
                val googleSignUpClient = GoogleSignIn.getClient(
                    context,
                    GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestIdToken("849364024341-nmtblhft9pj1b98e5tvdvtcbtrec2lf9.apps.googleusercontent.com")
                        .requestEmail()
                        .requestProfile()
                        .build()
                )
                val signInIntent = googleSignUpClient.signInIntent
                googleSignUpLauncher.launch(signInIntent)
            }) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_google),
                    contentDescription = "Google Icon",
                    modifier = Modifier.size(50.dp),
                    tint = Color.Unspecified
                )
            }
        }
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 20.dp),
        verticalArrangement = Arrangement.Bottom,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {
            CustomClickableText(
                text1 = "Already having an account? ",
                text2 = "Sign in",
                onClick = { navController.navigate(Screens.SignInScreen.route) })
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
    LaunchedEffect(key1 = signUpState.value?.isSuccess) {
        scope.launch {
            if (signUpState.value?.isSuccess?.isNotEmpty() == true) {
                val success = signUpState.value?.isSuccess
                Toast.makeText(context, "$success", Toast.LENGTH_LONG).show()
                navController.navigate(Screens.SearchScreen.route)
            }
        }
    }
    LaunchedEffect(key1 = signUpState.value?.isError) {
        scope.launch {
            if (signUpState.value?.isError?.isNotEmpty() == true) {
                val error = signUpState.value?.isError
                Toast.makeText(context, "$error", Toast.LENGTH_LONG).show()
            }
        }
    }
    LaunchedEffect(key1 = googleSignUpState.value?.isSuccess) {
        scope.launch {
            if (googleSignUpState.value?.isSuccess?.isNotEmpty() == true) {
                val success = googleSignUpState.value?.isSuccess
                Toast.makeText(context, "$success", Toast.LENGTH_LONG).show()
                navController.navigate(Screens.SearchScreen.route)
            }
        }
    }
    LaunchedEffect(key1 = googleSignUpState.value?.isError) {
        scope.launch {
            if (googleSignUpState.value?.isError?.isNotEmpty() == true) {
                val error = googleSignUpState.value?.isError
                Toast.makeText(context, "$error", Toast.LENGTH_LONG).show()
            }
        }
    }
}