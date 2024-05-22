package com.example.onspot.ui.components

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.onspot.navigation.Screens
import com.example.onspot.viewmodel.OfferViewModel
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.launch

@Composable
fun ParkingMapOffer(
    offerViewModel: OfferViewModel,
    modifier: Modifier = Modifier
) {
    val addMarkerState = offerViewModel.addMarkerState.collectAsState(initial = null)

    var showDialog by remember { mutableStateOf(false) }
    var selectedLatLng by remember { mutableStateOf(LatLng(46.7712, 23.6236)) }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current


    var mapType by remember { mutableStateOf(MapType.NORMAL) }
    val mapProperties by remember(mapType) { mutableStateOf(MapProperties(mapType = mapType)) }
    val markers = remember { mutableStateListOf<LatLng>() }
    val uiSettings by remember { mutableStateOf(MapUiSettings(zoomControlsEnabled = true)) }
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(46.7712, 23.6236), 12f)
    }

    GoogleMap(
        modifier = modifier.fillMaxSize(),
        properties = mapProperties,
        uiSettings = uiSettings,
        cameraPositionState = cameraPositionState,
        onMapClick = { latLng ->
            selectedLatLng = latLng
            showDialog = true
        }
    ) {
        markers.forEach { latLng ->

            Marker(
                state = MarkerState(position = latLng),
                title = "Parking Spot",
                snippet = "Tap to view details"
            )
        }
    }

    FloatingActionButton(
        onClick = {
            mapType = if (mapType == MapType.NORMAL) MapType.HYBRID else MapType.NORMAL
        },
        modifier = Modifier
            .padding(16.dp)
            .size(40.dp),
        containerColor = MaterialTheme.colorScheme.secondary
    ) {
        Icon(
            imageVector = Icons.Default.Layers,
            contentDescription = "Toggle Map Type"
        )
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Add Parking Spot") },
            text = { Text("Do you want to add a parking spot at this location?") },
            confirmButton = {
                Button(onClick = {
                    // addParkingSpotToFirebase(latLng)
                    markers.add(selectedLatLng)
                    scope.launch {
                        offerViewModel.finalizeMarker(selectedLatLng.latitude, selectedLatLng.longitude)
                    }
                    showDialog = false
                }) {
                    Text("Confirm")
                }
            },
            dismissButton = {
                Button(onClick = { showDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        if (addMarkerState.value?.isLoading == true) {
            CircularProgressIndicator()
        }
    }
    LaunchedEffect(key1 = addMarkerState.value?.isSuccess) {
        scope.launch {
            if (addMarkerState.value?.isSuccess?.isNotEmpty() == true) {
                val success = addMarkerState.value?.isSuccess
                Log.i("CREATE MARKER", "$success")
                //navController.navigate(Screens.UserProfileScreen.route)
            }
        }
    }
    LaunchedEffect(key1 = addMarkerState.value?.isError) {
        scope.launch {
            if (addMarkerState.value?.isError?.isNotEmpty() == true) {
                val error = addMarkerState.value?.isError
                Toast.makeText(context, "$error", Toast.LENGTH_LONG).show()
            }
        }
    }
}