package com.example.onspot.ui.screens.secondary

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteOutline
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.onspot.navigation.Screens
import com.example.onspot.ui.components.CustomAlertDialog
import com.example.onspot.ui.components.CustomButton
import com.example.onspot.ui.components.CustomTopBar
import com.example.onspot.ui.theme.RegularFont
import com.example.onspot.utils.Resource
import com.example.onspot.viewmodel.VehicleViewModel
import kotlinx.coroutines.launch

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun VehicleDetailsScreen(
    navController: NavController,
    id: String,
    vehicleViewModel: VehicleViewModel = viewModel()
) {
    val vehicleDetails by vehicleViewModel.vehicleDetails.collectAsState()

    var licensePlate by rememberSaveable { mutableStateOf("") }
    var country by rememberSaveable { mutableStateOf("") }
    var make by rememberSaveable { mutableStateOf("") }
    var model by rememberSaveable { mutableStateOf("") }
    var year by rememberSaveable { mutableStateOf("") }
    var color by rememberSaveable { mutableStateOf("") }

    var showDeleteConfirmationDialog by rememberSaveable { mutableStateOf(false) }

    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    val deleteVehicleState = vehicleViewModel.deleteVehicleState.collectAsState(initial = null)

    LaunchedEffect(key1 = id) {
        vehicleViewModel.fetchVehicleDetails(id)
    }

    when (vehicleDetails) {
        is Resource.Loading -> {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                CircularProgressIndicator()
            }
        }
        is Resource.Success -> {
            if (licensePlate.isEmpty() && country.isEmpty()) {
                vehicleDetails.data?.let { vehicle ->
                    licensePlate = vehicle.licensePlate
                    country = vehicle.country
                    make = vehicle.make
                    model = vehicle.model
                    year = vehicle.year.toString()
                    color = vehicle.color
                }
            }
        }
        is Resource.Error -> {
            LaunchedEffect(key1 = true) {
                Toast.makeText(context, "Error fetching vehicle details", Toast.LENGTH_LONG).show()
            }
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Scaffold(
            topBar = {
                CustomTopBar(
                    title = "Vehicle details",
                    onBackClick = { navController.popBackStack() },
                    icon = Icons.Default.DeleteOutline,
                    onIconClick = { showDeleteConfirmationDialog = true }
                )
            },
            bottomBar = {
                CustomButton(
                    onClick = {
                        scope.launch {
                            navController.navigate(Screens.UserProfileScreen.route)
                        }
                    },
                    buttonText = "OK",
                    enabled = true,
                    modifier = Modifier.padding(bottom = 30.dp)
                )
            }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 30.dp, vertical = 30.dp),
            ) {
                Text(
                    text = "This is your vehicle matriculated with the number $licensePlate.",
                    fontSize = 20.sp,
                    fontFamily = RegularFont,
                    fontStyle = FontStyle.Italic,
                    modifier = Modifier.padding(bottom = 10.dp)
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "•", fontSize = 24.sp, modifier = Modifier.padding(end = 8.dp))
                    Text(
                        text = "Country: $country",
                        fontFamily = RegularFont,
                        fontSize = 15.sp,
                    )
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "•", fontSize = 24.sp, modifier = Modifier.padding(end = 8.dp))
                    Text(
                        text = "Make: $make",
                        fontFamily = RegularFont,
                        fontSize = 15.sp,
                    )
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "•", fontSize = 24.sp, modifier = Modifier.padding(end = 8.dp))
                    Text(
                        text = "Model: $model",
                        fontFamily = RegularFont,
                        fontSize = 15.sp,
                    )
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "•", fontSize = 24.sp, modifier = Modifier.padding(end = 8.dp))
                    Text(
                        text = "Manufacturing year: $year",
                        fontFamily = RegularFont,
                        fontSize = 15.sp,
                    )
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "•", fontSize = 24.sp, modifier = Modifier.padding(end = 8.dp))
                    Text(
                        text = "Color: $color",
                        fontFamily = RegularFont,
                        fontSize = 15.sp,
                    )
                }
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            if (deleteVehicleState.value?.isLoading == true) {
                CircularProgressIndicator()
            }
        }
        if (showDeleteConfirmationDialog) {
            CustomAlertDialog(
                title = "Delete Confirmation",
                text = "Are you sure you want to delete this vehicle?",
                confirmButtonText = "Yes",
                dismissButtonText = "No",
                onConfirm = {
                    scope.launch {
                        vehicleViewModel.deleteVehicle(id)
                        showDeleteConfirmationDialog = false
                    }
                },
                onDismiss = { showDeleteConfirmationDialog = false }
            )
        }
        LaunchedEffect(key1 = deleteVehicleState.value?.isSuccess) {
            scope.launch {
                if (deleteVehicleState.value?.isSuccess?.isNotEmpty() == true) {
                    val success = deleteVehicleState.value?.isSuccess
                    navController.navigate(Screens.UserProfileScreen.route)
                    Toast.makeText(context, "$success", Toast.LENGTH_LONG).show()
                }
            }
        }
        LaunchedEffect(key1 = deleteVehicleState.value?.isError) {
            scope.launch {
                if (deleteVehicleState.value?.isError?.isNotEmpty() == true) {
                    val error = deleteVehicleState.value?.isError
                    Toast.makeText(context, "$error", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}