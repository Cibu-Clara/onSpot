package com.example.onspot.ui.components

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.onspot.R
import com.example.onspot.ui.theme.purple
import com.example.onspot.viewmodel.OfferViewModel
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.launch
import com.example.onspot.utils.getAddressLatLng
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState

@Composable
fun ParkingMapSearch(
    offerViewModel: OfferViewModel,
    placesClient: PlacesClient,
    modifier: Modifier = Modifier
) {
    val defaultCoordinates = LatLng(46.7712, 23.6236)
    var mapType by remember { mutableStateOf(MapType.NORMAL) }
    val mapProperties by remember(mapType) { mutableStateOf(MapProperties(mapType = mapType)) }
    val uiSettings by remember { mutableStateOf(MapUiSettings(zoomControlsEnabled = true)) }

    val markers = remember { mutableStateListOf<LatLng>() }

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(defaultCoordinates, 12f)
    }

    PlaceSearchBar(
        placesClient = placesClient,
        offerViewModel = offerViewModel,
        onSuggestionSelected = { latLng ->
            cameraPositionState.position = CameraPosition.fromLatLngZoom(latLng, 17f)
        }
    )
    GoogleMap(
        modifier = modifier.fillMaxSize(),
        properties = mapProperties,
        uiSettings = uiSettings,
        cameraPositionState = cameraPositionState
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
            .padding(start = 16.dp, top = 78.dp)
            .size(40.dp),
        containerColor = MaterialTheme.colorScheme.secondary
    ) {
        Icon(
            imageVector = Icons.Default.Layers,
            contentDescription = "Toggle Map Type"
        )
    }
}

@Composable
fun ParkingMapOffer(
    offerViewModel: OfferViewModel,
    placesClient: PlacesClient,
    parkingSpotAddress: String,
    showMap: MutableState<Boolean>,
    showConfirmation: MutableState<Boolean>,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    val defaultCoordinates = LatLng(46.7712, 23.6236)
    var mapType by remember { mutableStateOf(MapType.NORMAL) }
    val mapProperties by remember(mapType) { mutableStateOf(MapProperties(mapType = mapType)) }
    val uiSettings by remember { mutableStateOf(MapUiSettings(zoomControlsEnabled = true)) }

    val addMarkerState = offerViewModel.addMarkerState.collectAsState(initial = null)
    var showDialog by remember { mutableStateOf(false) }

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(defaultCoordinates, 12f)
    }
    var markerPosition by remember { mutableStateOf(cameraPositionState.position.target) }

    LaunchedEffect(key1 = parkingSpotAddress) {
        val location = getAddressLatLng(context, parkingSpotAddress)
        val zoom = if (location != defaultCoordinates) 17f else 0f
        cameraPositionState.position = CameraPosition.fromLatLngZoom(location, zoom)
    }

    Box(modifier = modifier.fillMaxSize()) {
        PlaceSearchBar(
            placesClient = placesClient,
            offerViewModel = offerViewModel,
            autocompleteAddress = parkingSpotAddress,
            onSuggestionSelected = { latLng ->
                cameraPositionState.position = CameraPosition.fromLatLngZoom(latLng, 17f)
            }
        )
        GoogleMap(
            modifier = Modifier.matchParentSize(),
            properties = mapProperties,
            uiSettings = uiSettings,
            cameraPositionState = cameraPositionState
        )
        Image(
            painter = painterResource(id = R.drawable.parking_pin),
            contentDescription = "Parking Spot Marker",
            modifier = Modifier
                .align(Alignment.Center)
                .size(50.dp)
        )
        Button(
            onClick = { showDialog = true },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = purple,
                contentColor = Color.White
            )
        ) {
            Text(text = "Confirm", fontWeight = FontWeight.Medium)
        }
    }
    if (showDialog) {
        CustomAlertDialog(
            title = "Confirm parking spot position",
            text = "Are you sure this is the correct location of your parking spot?",
            confirmButtonText = "Yes",
            dismissButtonText = "No",
            onConfirm = {
                scope.launch {
                    offerViewModel.finalizeMarker(markerPosition.latitude, markerPosition.longitude)
                }
                showDialog = false
            },
            onDismiss = { showDialog = false }
        )
    }
    FloatingActionButton(
        onClick = {
            mapType = if (mapType == MapType.NORMAL) MapType.HYBRID else MapType.NORMAL
        },
        modifier = Modifier
            .padding(start = 16.dp, top = 78.dp)
            .size(40.dp),
        containerColor = MaterialTheme.colorScheme.secondary
    ) {
        Icon(
            imageVector = Icons.Default.Layers,
            contentDescription = "Toggle Map Type"
        )
    }
    LaunchedEffect(cameraPositionState) {
        snapshotFlow { cameraPositionState.isMoving }.collect { isMoving ->
            if (!isMoving) {
                markerPosition = cameraPositionState.position.target
            }
        }
    }
    if (addMarkerState.value?.isLoading == true) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            CircularProgressIndicator()
        }
    }
    LaunchedEffect(key1 = addMarkerState.value?.isSuccess) {
        scope.launch {
            if (addMarkerState.value?.isSuccess?.isNotEmpty() == true) {
                val success = addMarkerState.value?.isSuccess
                Log.i("CREATE MARKER", "$success")
                showMap.value = false
                showConfirmation.value = true
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