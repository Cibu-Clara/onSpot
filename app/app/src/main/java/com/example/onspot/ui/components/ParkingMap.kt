package com.example.onspot.ui.components

import android.content.Context
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

@Composable
fun ParkingMap(
    modifier: Modifier = Modifier,
    isMarkingEnabled: Boolean
) {
    var showDialog by remember { mutableStateOf(false) }
    var selectedLatLng by remember { mutableStateOf(LatLng(46.7712, 23.6236)) }

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
}