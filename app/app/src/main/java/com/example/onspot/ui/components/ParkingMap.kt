package com.example.onspot.ui.components

import android.content.Context
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

@Composable
fun ParkingMap(
    modifier: Modifier = Modifier
) {
    var showDialog by remember { mutableStateOf(false) }
    var selectedLatLng by remember { mutableStateOf(LatLng(46.7712, 23.6236)) }
    val context = LocalContext.current

    val mapProperties by remember { mutableStateOf(MapProperties()) }
    val markers = remember { mutableStateListOf<LatLng>() }
    val uiSettings by remember { mutableStateOf(MapUiSettings(zoomControlsEnabled = true)) }
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(46.7712, 23.6236), 10f)
    }

    GoogleMap(
        modifier = modifier.fillMaxSize(),
        properties = mapProperties,
        uiSettings = uiSettings,
        cameraPositionState = cameraPositionState,
        onMapLongClick = { latLng ->
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

    if (showDialog) {
        ShowAddSpotDialog(context = context, latLng = selectedLatLng, onDismiss = { showDialog = false }) {
            markers.add(selectedLatLng) // Add marker when the dialog is confirmed
        }
    }
}


@Composable
fun ShowAddSpotDialog(context: Context, latLng: LatLng, onDismiss: () -> Unit, onConfirm: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Parking Spot") },
        text = { Text("Do you want to add a parking spot at this location?") },
        confirmButton = {
            Button(onClick = {
                // addParkingSpotToFirebase(latLng)
                onConfirm()
                onDismiss()
            }) {
                Text("Confirm")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}