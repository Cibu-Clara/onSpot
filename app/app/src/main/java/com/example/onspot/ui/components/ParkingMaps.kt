package com.example.onspot.ui.components

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import android.widget.Toast
import android.graphics.Canvas
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.onspot.R
import com.example.onspot.data.model.Marker
import com.example.onspot.ui.theme.purple
import com.example.onspot.utils.Resource
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
import com.example.onspot.viewmodel.ParkingSpotViewModel
import com.example.onspot.viewmodel.ReservationViewModel
import com.example.onspot.viewmodel.SearchViewModel
import com.example.onspot.viewmodel.UserProfileViewModel
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ParkingMapSearch(
    searchViewModel: SearchViewModel,
    parkingSpotViewModel: ParkingSpotViewModel = viewModel(),
    userProfileViewModel: UserProfileViewModel = viewModel(),
    reservationViewModel: ReservationViewModel = viewModel(),
    placesClient: PlacesClient,
    markersList: List<Marker>,
    modifier: Modifier = Modifier
) {
    val defaultCoordinates = LatLng(46.7712, 23.6236)
    var mapType by remember { mutableStateOf(MapType.NORMAL) }
    val mapProperties by remember(mapType) { mutableStateOf(MapProperties(mapType = mapType)) }
    val uiSettings by remember { mutableStateOf(MapUiSettings(zoomControlsEnabled = true)) }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var mapReady by remember { mutableStateOf(false) }
    var customIcon by remember { mutableStateOf<BitmapDescriptor?>(null) }

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(defaultCoordinates, 7f)
    }
    val addReservationState = reservationViewModel.addReservationState.collectAsState(initial = null)
    val toggleVehicleChosenState = searchViewModel.toggleVehicleChosenState.collectAsState(initial = null)

    var filterDialogVisible by remember { mutableStateOf(false) }
    var isFilterApplied by remember { mutableStateOf(false) }
    val startDate = remember { mutableStateOf<LocalDate?>(null) }
    val startTime = remember { mutableStateOf<LocalTime?>(null) }
    val endDate = remember { mutableStateOf<LocalDate?>(null) }
    val endTime = remember { mutableStateOf<LocalTime?>(null) }
    val currentUserId = Firebase.auth.currentUser?.uid

    var selectedMarker by remember { mutableStateOf<Marker?>(null) }
    val parkingSpotDetails by parkingSpotViewModel.parkingSpotDetails.collectAsState()
    val reservations = reservationViewModel.reservations.collectAsState().value
    var filteredMarkers by remember { mutableStateOf(markersList) }

    val bottomSheetState = rememberModalBottomSheetState()
    val chooseVehicleSheetState = rememberModalBottomSheetState()
    var showVehicleOptionsSheet by remember { mutableStateOf(false) }
    var showDialog by remember { mutableStateOf(false) }

    fun updateFilteredMarkers() {
        val now = LocalDateTime.now()
        filteredMarkers = markersList.filter { marker ->
            val markerStartDateTime = LocalDateTime.of(LocalDate.parse(marker.startDate), LocalTime.parse(marker.startTime))
            val markerEndDateTime = LocalDateTime.of(LocalDate.parse(marker.endDate), LocalTime.parse(marker.endTime))

            val startDateTime = if (startDate.value != null && startTime.value != null) {
                LocalDateTime.of(startDate.value, startTime.value)
            } else null
            val endDateTime = if (endDate.value != null && endTime.value != null) {
                LocalDateTime.of(endDate.value, endTime.value)
            } else null

            val isIntervalValid = (startDateTime == null || markerStartDateTime <= startDateTime) &&
                    (endDateTime == null || endDateTime <= markerEndDateTime)

            val isNotReserved = !marker.reserved
            val isNotCurrentUser = marker.userId != currentUserId
            val isEndDateTimeValid = markerEndDateTime.isAfter(now)

            val userReservations = reservations.data?.filter { it.userId == currentUserId }
            val isReservedByUser = userReservations?.any { it.markerId == marker.uuid } == true

            isEndDateTimeValid && isNotReserved && isNotCurrentUser && isIntervalValid && !isReservedByUser
        }
    }

    LaunchedEffect(key1 = reservations, key2 = markersList) {
        updateFilteredMarkers()
    }

    LaunchedEffect(key1 = mapReady) {
        if (mapReady) {
            val density = context.resources.displayMetrics.density
            val iconWidth = (50 * density).toInt()  // 50dp
            val iconHeight = (50 * density).toInt() // 50dp
            customIcon = bitmapDescriptorFromImage(context, R.drawable.parking_pin, iconWidth, iconHeight)
        }
    }

    LaunchedEffect(key1 = selectedMarker) {
        selectedMarker?.let { marker ->
            parkingSpotViewModel.fetchParkingSpotDetails(marker.parkingSpotId)
            userProfileViewModel.fetchOtherUserDetails(marker.userId)
            scope.launch {
                bottomSheetState.show()
            }
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        PlaceSearchBar(
            placesClient = placesClient,
            searchViewModel = searchViewModel,
            onSuggestionSelected = { latLng ->
                cameraPositionState.position = CameraPosition.fromLatLngZoom(latLng, 17f)
            },
            showFilterDialog = { filterDialogVisible = true }
        )
        GoogleMap(
            properties = mapProperties,
            uiSettings = uiSettings,
            cameraPositionState = cameraPositionState,
            onMapLoaded = {
                mapReady = true
            }
        ) {
            if (customIcon != null) {
                filteredMarkers.forEach { marker ->
                    Marker(
                        state = MarkerState(position = LatLng(marker.latitude, marker.longitude)),
                        title = "Parking Spot",
                        snippet = "Tap to view details",
                        icon = customIcon,
                        onClick = {
                            selectedMarker = marker
                            true
                        }
                    )
                }
            }
        }
        if (isFilterApplied) {
            Button(
                onClick = {
                    startDate.value = null
                    startTime.value = null
                    endDate.value = null
                    endTime.value = null
                    isFilterApplied = false
                    updateFilteredMarkers()
                },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = purple,
                    contentColor = Color.White
                )
            ) {
                Text(text = "Remove filters", fontWeight = FontWeight.Medium)
            }
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
    if (filterDialogVisible) {
        FilterDialog(
            onDismiss = { filterDialogVisible = false },
            onApplyFilter = { sDate, sTime, eDate, eTime ->
                startDate.value = sDate
                startTime.value = sTime
                endDate.value = eDate
                endTime.value = eTime
                filterDialogVisible = false
                isFilterApplied = true
                updateFilteredMarkers()
            }
        )
    }
    if (selectedMarker != null) {
        when (parkingSpotDetails) {
            is Resource.Loading -> {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                    CircularProgressIndicator()
                }
            }
            is Resource.Success -> {
                val parkingSpot = parkingSpotDetails.data
                if (parkingSpot != null) {
                    ParkingSpotDetailsBottomSheet(
                        parkingSpot = parkingSpot,
                        marker = selectedMarker!!,
                        sheetState = bottomSheetState,
                        onDismiss = {
                            selectedMarker = null
                            scope.launch {
                                bottomSheetState.hide()
                            }
                        },
                        onReserve = {
                            scope.launch {
                                bottomSheetState.hide()
                            }
                            scope.launch {
                                chooseVehicleSheetState.show()
                                showVehicleOptionsSheet = true
                            }
                        },
                        userProfileViewModel = userProfileViewModel
                    )
                    if (showVehicleOptionsSheet) {
                        val id by rememberSaveable { mutableStateOf(UUID.randomUUID().toString()) }
                        val vehicleId = rememberSaveable { mutableStateOf("") }
                        val isVehicleChosen = rememberSaveable { mutableStateOf(false) }

                        VehicleOptionsBottomSheet(
                            searchViewModel = searchViewModel,
                            vehicleId = vehicleId,
                            isVehicleChosen = isVehicleChosen,
                            marker = selectedMarker!!,
                            startDate = startDate,
                            startTime = startTime,
                            endDate = endDate,
                            endTime = endTime,
                            sheetState = chooseVehicleSheetState,
                            onDismiss = {
                                scope.launch {
                                    selectedMarker = null
                                    startDate.value = null
                                    startTime.value = null
                                    endDate.value = null
                                    endTime.value = null
                                    chooseVehicleSheetState.hide()
                                    showVehicleOptionsSheet = false
                                }
                            },
                            onConfirm = {
                                if (isVehicleChosen.value) {
                                    showDialog = true
                                } else {
                                    scope.launch {
                                        searchViewModel.toggleVehicleChosen(vehicleId.value)
                                        reservationViewModel.addReservation(
                                            id = id,
                                            status = "Pending",
                                            startDate = startDate.value.toString(),
                                            startTime = startTime.value.toString(),
                                            endDate = endDate.value.toString(),
                                            endTime = endTime.value.toString(),
                                            userId = currentUserId!!,
                                            markerId = selectedMarker!!.uuid,
                                            parkingSpotId = selectedMarker!!.parkingSpotId,
                                            vehicleId = vehicleId.value
                                        )
                                        selectedMarker = null
                                        startDate.value = null
                                        startTime.value = null
                                        endDate.value = null
                                        endTime.value = null
                                        showVehicleOptionsSheet = false
                                        updateFilteredMarkers()
                                    }
                                }
                            }
                        )
                    }
                }
            }
            is Resource.Error -> {
                LaunchedEffect(key1 = true) {
                    Toast.makeText(context, "Error fetching parking spot details", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
    if (showDialog) {
        CustomAlertDialog(
            title = "Error",
            text = "You are already using this vehicle in another reservation.",
            onConfirm = { showDialog = false },
            onDismiss = { showDialog = false }
        )
    }
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
        if (addReservationState.value?.isLoading == true || toggleVehicleChosenState.value?.isLoading == true) {
            CircularProgressIndicator()
        }
    }
    LaunchedEffect(key1 = addReservationState.value?.isSuccess) {
        if (addReservationState.value?.isSuccess?.isNotEmpty() == true) {
            updateFilteredMarkers()
            val success = addReservationState.value?.isSuccess
            Toast.makeText(context, "$success", Toast.LENGTH_LONG).show()
        }
    }
    LaunchedEffect(key1 = addReservationState.value?.isError) {
        if (addReservationState.value?.isError?.isNotEmpty() == true) {
            val error = addReservationState.value?.isError
            Toast.makeText(context, "$error", Toast.LENGTH_LONG).show()
        }
    }
    LaunchedEffect(key1 = toggleVehicleChosenState.value?.isSuccess) {
        if (toggleVehicleChosenState.value?.isSuccess?.isNotEmpty() == true) {
            val success = toggleVehicleChosenState.value?.isSuccess
            Log.i("TOGGLE VEHICLE CHOSEN", "$success")
        }
    }
    LaunchedEffect(key1 = toggleVehicleChosenState.value?.isError) {
        if (toggleVehicleChosenState.value?.isError?.isNotEmpty() == true) {
            val error = toggleVehicleChosenState.value?.isError
            Toast.makeText(context, "$error", Toast.LENGTH_LONG).show()
            Log.e("TOGGLE VEHICLE CHOSEN", "$error")
        }
    }
}

fun bitmapDescriptorFromImage(context: Context, vectorResId: Int, width: Int, height: Int): BitmapDescriptor? {
    val vectorDrawable = ContextCompat.getDrawable(context, vectorResId)?.mutate() ?: return null
    val drawable = DrawableCompat.wrap(vectorDrawable).apply {
        setBounds(0, 0, width, height)
    }

    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    drawable.draw(canvas)

    return BitmapDescriptorFactory.fromBitmap(bitmap)
}

@Composable
fun ParkingMapOffer(
    offerViewModel: OfferViewModel,
    placesClient: PlacesClient,
    parkingSpotAddress: String,
    parkingSpotCity: String,
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
    val address = "$parkingSpotAddress, $parkingSpotCity"

    LaunchedEffect(key1 = address) {
        val location = getAddressLatLng(context, address)
        val zoom = if (location != defaultCoordinates) 17f else 0f
        cameraPositionState.position = CameraPosition.fromLatLngZoom(location, zoom)
    }

    Box(modifier = modifier.fillMaxSize()) {
        PlaceSearchBarOffer(
            placesClient = placesClient,
            offerViewModel = offerViewModel,
            autocompleteAddress = address,
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