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
import com.example.onspot.viewmodel.VehicleViewModel
import kotlinx.coroutines.launch
import java.util.UUID

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun AddVehicleScreen(
    navController: NavController,
    vehicleViewModel: VehicleViewModel = viewModel()
) {
    val id by rememberSaveable { mutableStateOf(UUID.randomUUID().toString()) }
    var licensePlate by rememberSaveable { mutableStateOf("") }
    var country by rememberSaveable { mutableStateOf("") }
    var make by rememberSaveable { mutableStateOf("") }
    var model by rememberSaveable { mutableStateOf("") }
    var year by rememberSaveable { mutableStateOf("") }
    var color by rememberSaveable { mutableStateOf("") }

    val isAddButtonEnabled = licensePlate.isNotBlank() && country.isNotBlank() && make.isNotBlank()
            && model.isNotBlank() && year.isNotBlank() && color.isNotBlank()

    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    fun clearFocus() { focusManager.clearFocus() }

    val addVehicleState = vehicleViewModel.addVehicleState.collectAsState(initial = null)

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Scaffold(
            topBar = { CustomTopBar(
                title = "Add a vehicle",
                onBackClick = {
                    navController.popBackStack()
                })
            },
            bottomBar = {
                CustomButton(
                    onClick = {
                        scope.launch {
                            vehicleViewModel.addVehicle(
                                id = id,
                                licensePlate = licensePlate,
                                country = country,
                                make = make,
                                model = model,
                                year = year.toInt(),
                                color = color
                            )
                        }
                    },
                    buttonText = "Add",
                    enabled = isAddButtonEnabled,
                    modifier = Modifier.padding(bottom = 30.dp)
                )
            }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable(onClick = { clearFocus() })
            ) {
                Text(
                    text = "Please provide the required details for identifying your vehicle.",
                    modifier = Modifier
                        .padding(30.dp),
                    fontFamily = RegularFont,
                    color = Color.Gray,
                    fontSize = 16.sp
                )
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 30.dp)
                ) {
                    CustomTextField(
                        value = licensePlate,
                        onValueChange = { licensePlate = it },
                        label = "License plate number",
                        maxLines = 1
                    )
                    CustomTextField(
                        value = country,
                        onValueChange = { country = it },
                        label = "Country",
                        maxLines = 1
                    )
                    CustomTextField(
                        value = make,
                        onValueChange = { make = it },
                        label = "Make",
                        maxLines = 1
                    )
                    CustomTextField(
                        value = model,
                        onValueChange = { model = it },
                        label = "Model",
                        maxLines = 1
                    )
                    CustomTextField(
                        value = year,
                        onValueChange = { year = it },
                        label = "Manufacturing year",
                        maxLines = 1,
                        keyboardType = KeyboardType.Number,
                        modifier = Modifier.padding(top = 10.dp)
                    )
                    CustomTextField(
                        value = color,
                        onValueChange = { color = it },
                        label = "Color",
                        maxLines = 1
                    )
                }
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            if (addVehicleState.value?.isLoading == true) {
                CircularProgressIndicator()
            }
        }
        LaunchedEffect(key1 = addVehicleState.value?.isSuccess) {
            scope.launch {
                if (addVehicleState.value?.isSuccess?.isNotEmpty() == true) {
                    val success = addVehicleState.value?.isSuccess
                    Toast.makeText(context, "$success", Toast.LENGTH_LONG).show()
                    navController.navigate(Screens.UserProfileScreen.route)
                }
            }
        }
        LaunchedEffect(key1 = addVehicleState.value?.isError) {
            scope.launch {
                if (addVehicleState.value?.isError?.isNotEmpty() == true) {
                    val error = addVehicleState.value?.isError
                    Toast.makeText(context, "$error", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}