package com.example.onspot.ui.screens.secondary

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.onspot.navigation.Screens
import com.example.onspot.ui.components.CustomButton
import com.example.onspot.ui.components.CustomTextField
import com.example.onspot.ui.components.CustomTopBar
import com.example.onspot.ui.theme.RegularFont
import com.example.onspot.viewmodel.ParkingSpotViewModel
import kotlinx.coroutines.launch

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun AddParkingSpotScreen(
    navController: NavController,
    parkingSpotViewModel: ParkingSpotViewModel = viewModel()
) {
    var address by rememberSaveable { mutableStateOf("") }
    var number by rememberSaveable { mutableStateOf("") }
    val isButtonEnabled = address.isNotBlank() && number != ""

    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    fun clearFocus() { focusManager.clearFocus() }

    val addParkingSpotState = parkingSpotViewModel.addParkingSpotState.collectAsState(initial = null)

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Scaffold(
            topBar = { CustomTopBar(title = "Add a parking spot", onBackClick = { navController.popBackStack() }) },
            bottomBar = {
                CustomButton(
                    onClick = {
                        scope.launch {
                            parkingSpotViewModel.addParkingSpot(address = address, number = number.toInt())
                        }
                    },
                    buttonText = "Add",
                    enabled = isButtonEnabled,
                    modifier = Modifier.padding(bottom = 30.dp)
                )
            }
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                Text(
                    text = "Please provide the required details to identify your parking spot, including a document to attest" +
                            " your ownership or legal right to use the space, such as a rental agreement or purchase contract. ",
                    modifier = Modifier
                        .padding(30.dp),
                    fontFamily = RegularFont,
                    color = Color.Gray,
                    fontSize = 16.sp
                )
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .clickable(onClick = { clearFocus() })
                        .padding(horizontal = 30.dp)
                ) {
                    CustomTextField(
                        value = address,
                        onValueChange = { address = it },
                        label = "Full address"
                    )
                    CustomTextField(
                        value = number,
                        onValueChange = { number = it },
                        label = "Number of the parking spot",
                        keyboardType = KeyboardType.Number,
                        modifier = Modifier.padding(top = 10.dp)
                    )
                }
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            if (addParkingSpotState.value?.isLoading == true) {
                CircularProgressIndicator()
            }
        }
        LaunchedEffect(key1 = addParkingSpotState.value?.isSuccess) {
            scope.launch {
                if (addParkingSpotState.value?.isSuccess?.isNotEmpty() == true) {
                    val success = addParkingSpotState.value?.isSuccess
                    Toast.makeText(context, "$success", Toast.LENGTH_LONG).show()
                    navController.navigate(Screens.UserProfileScreen.route)
                }
            }
        }
        LaunchedEffect(key1 = addParkingSpotState.value?.isError) {
            scope.launch {
                if (addParkingSpotState.value?.isError?.isNotEmpty() == true) {
                    val error = addParkingSpotState.value?.isError
                    Toast.makeText(context, "$error", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}