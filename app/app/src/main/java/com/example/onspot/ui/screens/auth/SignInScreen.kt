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
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.onspot.R
import com.example.onspot.viewmodel.SignInViewModel
import com.example.onspot.navigation.Screens
import com.example.onspot.ui.components.CustomButton
import com.example.onspot.ui.components.CustomClickableText
import com.example.onspot.ui.components.CustomInputDialog
import com.example.onspot.ui.components.CustomPasswordField
import com.example.onspot.ui.components.CustomTextField
import com.example.onspot.ui.components.DividerWithText
import com.example.onspot.ui.theme.RegularFont
import com.example.onspot.ui.theme.purple
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import kotlinx.coroutines.launch

@Composable
fun SignInScreen(
    navController: NavController,
    signInViewModel: SignInViewModel = viewModel()
) {
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }

    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val signInState = signInViewModel.signInState.collectAsState(initial = null)
    val googleSignInState = signInViewModel.googleSignInState.collectAsState(initial = null)
    val resetPasswordState = signInViewModel.resetPasswordState.collectAsState(initial = null)

    val isButtonEnabled = email.isNotBlank() && password.isNotBlank()
    var showEmailConfirmationDialog by rememberSaveable { mutableStateOf(false) }

    Text(
        text = "Welcome back!",
        fontWeight = FontWeight.Bold,
        fontSize = 40.sp,
        color = purple,
        modifier = Modifier.padding(start = 30.dp, top = 60.dp)
    )
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 30.dp, end = 30.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Enter your credentials to log in",
            fontWeight = FontWeight.Medium,
            fontSize = 15.sp,
            color = Color.Gray,
            fontFamily = RegularFont,
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
        Row (
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 7.dp),
            horizontalArrangement = Arrangement.End
        ) {
            Text(
                text = "Forgot your password?",
                color = Color.Gray,
                modifier = Modifier.clickable { showEmailConfirmationDialog = true }
            )
        }
        CustomButton(
            onClick = {
                scope.launch {
                    signInViewModel.loginWithEmailAndPassword(email, password)
                }
            },
            buttonText = "Sign In",
            enabled = isButtonEnabled
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            if (signInState.value?.isLoading == true || googleSignInState.value?.isLoading == true || resetPasswordState.value?.isLoading == true) {
                CircularProgressIndicator()
            }
        }
        DividerWithText(text = "or connect with")
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            val googleSignInLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.StartActivityForResult(),
                onResult = { result ->
                    if (result.resultCode == Activity.RESULT_OK) {
                        signInViewModel.handleGoogleSignInResult(result.data)
                    }
                }
            )
            IconButton(onClick = {
                val googleSignInClient = GoogleSignIn.getClient(
                    context,
                    GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestIdToken("849364024341-nmtblhft9pj1b98e5tvdvtcbtrec2lf9.apps.googleusercontent.com")
                        .requestEmail()
                        .requestProfile()
                        .build()
                )
                val signInIntent = googleSignInClient.signInIntent
                googleSignInLauncher.launch(signInIntent)
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
                text1 = "Not having an account yet? ",
                text2 = "Sign up",
                onClick =  { navController.navigate(Screens.SignUpScreen.route) }
            )
        }
    }
    if (showEmailConfirmationDialog) {
        CustomInputDialog(
            title = "Reset Password",
            text = "Enter the email address associated with your account. You will receive a link to reset your password.",
            label = "Email",
            confirmButtonText = "Send",
            onConfirm = {
                scope.launch {
                    signInViewModel.sendPasswordResetEmail(it)
                }
            },
            onDismiss = {
                showEmailConfirmationDialog = false
            },
            keyboardType = KeyboardType.Email,
        )
    }
    LaunchedEffect(key1 = signInState.value?.isSuccess) {
        scope.launch {
            if (signInState.value?.isSuccess?.isNotEmpty() == true) {
                val success = signInState.value?.isSuccess
                Toast.makeText(context, "$success", Toast.LENGTH_LONG).show()
                navController.navigate(Screens.SearchScreen.route)
            }
        }
    }
    LaunchedEffect(key1 = signInState.value?.isError) {
        scope.launch {
            if (signInState.value?.isError?.isNotEmpty() == true) {
                val error = signInState.value?.isError
                Toast.makeText(context, "$error", Toast.LENGTH_LONG).show()
            }
        }
    }

    LaunchedEffect(key1 = googleSignInState.value?.isSuccess) {
        scope.launch {
            if (googleSignInState.value?.isSuccess?.isNotEmpty() == true) {
                val success = googleSignInState.value?.isSuccess
                Toast.makeText(context, "$success", Toast.LENGTH_LONG).show()
                navController.navigate(Screens.SearchScreen.route)
            }
        }
    }
    LaunchedEffect(key1 = googleSignInState.value?.isError) {
        scope.launch {
            if (googleSignInState.value?.isError?.isNotEmpty() == true) {
                val error = googleSignInState.value?.isError
                Toast.makeText(context, "$error", Toast.LENGTH_LONG).show()
            }
        }
    }

    LaunchedEffect(key1 = resetPasswordState.value?.isSuccess) {
        scope.launch {
            if (resetPasswordState.value?.isSuccess?.isNotEmpty() == true) {
                val success = resetPasswordState.value?.isSuccess
                Toast.makeText(context, "$success", Toast.LENGTH_LONG).show()
                showEmailConfirmationDialog = false
            }
        }
    }
    LaunchedEffect(key1 = resetPasswordState.value?.isError) {
        scope.launch {
            if (resetPasswordState.value?.isError?.isNotEmpty() == true) {
                val error = resetPasswordState.value?.isError
                Toast.makeText(context, "$error", Toast.LENGTH_LONG).show()
            }
        }
    }
}