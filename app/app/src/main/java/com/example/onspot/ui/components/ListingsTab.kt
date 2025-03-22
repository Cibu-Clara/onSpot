package com.example.onspot.ui.components

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.onspot.R
import com.example.onspot.data.model.ListingDetails
import com.example.onspot.data.model.RequestDetails
import com.example.onspot.data.model.Reservation
import com.example.onspot.navigation.Screens
import com.example.onspot.ui.theme.RegularFont
import com.example.onspot.ui.theme.lightPurple
import com.example.onspot.ui.theme.purple
import com.example.onspot.ui.theme.red
import com.example.onspot.utils.Resource
import com.example.onspot.viewmodel.ReservationViewModel
import com.example.onspot.viewmodel.SearchViewModel
import kotlinx.coroutines.launch

@Composable
fun ListingsTab(
    navController: NavController,
    reservationViewModel: ReservationViewModel
) {
    val listingDetails by reservationViewModel.listingsDetails.collectAsState()
    val context = LocalContext.current

    when (listingDetails) {
        is Resource.Loading -> {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                CircularProgressIndicator()
            }
        }
        is Resource.Success -> {
            val listings = (listingDetails as Resource.Success<List<ListingDetails>>).data
            ListingsList(listings ?: emptyList(), reservationViewModel, navController)
        }
        is Resource.Error -> {
            LaunchedEffect(key1 = true) {
                Toast.makeText(context, "Error fetching listings", Toast.LENGTH_LONG).show()
            }
        }
    }
}

@Composable
fun ListingsList(listings: List<ListingDetails>, reservationViewModel: ReservationViewModel, navController: NavController) {
    if (listings.isEmpty()) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "You do not have any parking spot",
                fontSize = 20.sp,
                fontFamily = RegularFont,
                color = Color.Gray,
                modifier = Modifier.padding(horizontal = 20.dp)
            )
            Text(
                text = "listings at the moment",
                fontSize = 20.sp,
                fontFamily = RegularFont,
                color = Color.Gray,
            )
        }
    }
    LazyColumn {
        items(listings) { details ->
            ListingCard(details, reservationViewModel, navController)
        }
    }
}

@Composable
fun ListingCard(
    details: ListingDetails,
    reservationViewModel: ReservationViewModel,
    navController: NavController,
    searchViewModel: SearchViewModel = viewModel()
) {
    var showMoreDetails by rememberSaveable { mutableStateOf(false) }
    var showRequestsDialog by rememberSaveable { mutableStateOf(false) }
    val requestsDetails by reservationViewModel.requestsDetails.collectAsState()
    var requests : List<RequestDetails>? = null
    val context = LocalContext.current
    var showCancelDialog by rememberSaveable { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val deleteMarkerState = reservationViewModel.deleteMarkerState.collectAsState(initial = null)
    val isReserved by remember { mutableStateOf(details.marker.reserved) }

    LaunchedEffect(details.marker.uuid) {
        reservationViewModel.fetchRequestsWithDetails(details.marker.uuid)
    }

    when (requestsDetails) {
        is Resource.Loading -> {}
        is Resource.Success -> {
            requests = (requestsDetails as Resource.Success<List<RequestDetails>>).data ?: emptyList()
        }
        is Resource.Error -> {
            LaunchedEffect(key1 = true) {
                Toast.makeText(context, "Error fetching requests", Toast.LENGTH_LONG).show()
            }
        }
    }

    val reservations by reservationViewModel.reservations.collectAsState()
    var reservationsList: List<Reservation>? = null

    LaunchedEffect(key1 = true) {
        reservationViewModel.fetchReservations()
    }

    when (reservations) {
        is Resource.Loading -> {}
        is Resource.Success -> {
            reservationsList = reservations.data?: emptyList()
        }
        is Resource.Error -> {
            LaunchedEffect(key1 = true) {
                Log.e("FETCH RESERVATIONS", "Error fetching reservations")
            }
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "${details.parkingSpot.address}, ${details.parkingSpot.city}",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                fontFamily = RegularFont,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            Text(
                text = "Available from ${details.marker.startDate} at ${details.marker.startTime} " +
                        "to ${details.marker.endDate} at ${details.marker.endTime}",
                fontSize = 14.sp,
                fontFamily = RegularFont,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            Text(
                text = "Cancel your offer",
                fontSize = 14.sp,
                fontFamily = RegularFont,
                color = purple,
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .clickable { showCancelDialog = true }
                    .padding(bottom = 4.dp)
            )
            if (!isReserved) {
                Text(
                    text = "View requests",
                    fontSize = 14.sp,
                    fontFamily = RegularFont,
                    color = purple,
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .clickable { showRequestsDialog = true }
                )
            } else {
                Text(
                    text = "Reserved",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = RegularFont,
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showMoreDetails = !showMoreDetails }
                    .padding(top = 5.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                if (showMoreDetails) {
                    Icon(imageVector = Icons.Default.KeyboardArrowUp, contentDescription = "Less details")
                } else {
                    Icon(imageVector = Icons.Default.KeyboardArrowDown, contentDescription = "More details")
                }
            }
            if (showMoreDetails) {
                Column(
                    horizontalAlignment = Alignment.Start
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 5.dp)) {
                        Icon(
                            imageVector = Icons.Outlined.Info,
                            contentDescription = "Info",
                            tint = Color.Gray,
                            modifier = Modifier
                                .padding(end = 10.dp, top = 4.dp)
                                .size(21.dp)
                        )
                        Text(
                            text = "Other users will be able to see these details only once you accept their reservation request.",
                            color = Color.Gray,
                            fontFamily = RegularFont,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                    }
                    Text(
                        text = "Bay number: ${details.parkingSpot.bayNumber}",
                        fontWeight = FontWeight.Bold,
                        fontFamily = RegularFont,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                    if (details.parkingSpot.additionalInfo != "") {
                        Row {
                            Text(
                                text = details.parkingSpot.additionalInfo,
                                fontFamily = RegularFont,
                                fontStyle = FontStyle.Italic,
                                fontSize = 14.sp
                            )
                        }
                    } else {
                        Text(
                            text = "No additional information.",
                            fontFamily = RegularFont,
                            fontStyle = FontStyle.Italic,
                            fontSize = 14.sp
                        )
                    }
                    Box {
                        Image(
                            painter = rememberAsyncImagePainter(details.parkingSpot.photoUrl),
                            contentDescription = "Parking Spot Picture",
                            modifier = Modifier
                                .height(250.dp)
                                .padding(20.dp)
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp)),
                            contentScale = ContentScale.Crop
                        )
                    }
                }
            }

        }
    }
    if (showRequestsDialog) {
        RequestsDialog(
            requests = requests,
            navController = navController,
            reservationViewModel = reservationViewModel,
            searchViewModel = searchViewModel,
            onDismiss = { showRequestsDialog = false },
            onAccept = {
                showRequestsDialog = false
            }
        )
    }
    if (showCancelDialog) {
        CustomAlertDialog(
            title = "Cancel confirmation",
            text = "Are you sure you want to cancel this offer?",
            onConfirm = {
                scope.launch {
                    if (reservationsList != null) {
                        for (r in reservationsList) {
                            if (details.marker.uuid == r.markerId && (r.status == "Accepted" || r.status == "Pending")) {
                                reservationViewModel.updateRequestStatus(r.uuid, "Canceled")
                                searchViewModel.changeVehicleChosen(r.vehicleId, false)
                            }
                        }
                    }
                    reservationViewModel.deleteMarker(details.marker.uuid)
                }
                showCancelDialog = false
            },
            onDismiss = { showCancelDialog = false },
            confirmButtonText = "Yes",
            dismissButtonText = "No"
        )
    }
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        if (deleteMarkerState.value?.isLoading == true) {
            CircularProgressIndicator()
        }
    }
    LaunchedEffect(key1 = deleteMarkerState.value?.isSuccess) {
        scope.launch {
            if (deleteMarkerState.value?.isSuccess?.isNotEmpty() == true) {
                val success = deleteMarkerState.value?.isSuccess
                Toast.makeText(context, "$success", Toast.LENGTH_LONG).show()
            }
        }
    }
    LaunchedEffect(key1 = deleteMarkerState.value?.isError) {
        scope.launch {
            if (deleteMarkerState.value?.isError?.isNotEmpty() == true) {
                val error = deleteMarkerState.value?.isError
                Toast.makeText(context, "$error", Toast.LENGTH_LONG).show()
            }
        }
    }
}


@Composable
fun RequestsDialog(
    requests: List<RequestDetails>?,
    navController: NavController,
    searchViewModel: SearchViewModel,
    reservationViewModel: ReservationViewModel,
    onDismiss: () -> Unit,
    onAccept: () -> Unit
) {
    val selectedRequest by reservationViewModel.selectedRequest.collectAsState()
    val updateReservationStatus = reservationViewModel.updateReservationStatus.collectAsState(initial = null)
    val acceptReservationState = reservationViewModel.acceptReservationState.collectAsState(initial = null)
    val rejectReservationState = reservationViewModel.rejectReservationState.collectAsState(initial = null)
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = {
            Text(
                text = "Reservation requests",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = Color.Black
            )
        },
        text = {
            RequestsList(
                requests = requests ?: emptyList(),
                navController = navController,
                selectedRequest = selectedRequest,
                onSelect = { reservationViewModel.selectRequest(it) },
                onReject = {
                    reservationViewModel.rejectRequest(it)
                    searchViewModel.changeVehicleChosen(it.vehicle.uuid, false)
                }
            )
        },
        confirmButton = {
            Button(
                onClick = {
                    selectedRequest?.let {
                        reservationViewModel.acceptRequest(it)
                        reservationViewModel.changeMarkerReserved(it.reservation.markerId, true)
                    }
                    onAccept()
                },
                enabled = selectedRequest != null
            ) {
                Text("Accept request")
            }
        },
        dismissButton = {
            Button(
                onClick = {
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.LightGray,
                    contentColor = Color.Black
                )
            ) {
                Text("Cancel")
            }
        }
    )
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        if (updateReservationStatus.value?.isLoading == true || acceptReservationState.value?.isLoading == true || rejectReservationState.value?.isLoading == true) {
            CircularProgressIndicator()
        }
    }
    LaunchedEffect(key1 = updateReservationStatus.value?.isSuccess) {
        scope.launch {
            if (updateReservationStatus.value?.isSuccess?.isNotEmpty() == true) {
                val success = updateReservationStatus.value?.isSuccess
                Toast.makeText(context, "$success", Toast.LENGTH_LONG).show()
            }
        }
    }
    LaunchedEffect(key1 = updateReservationStatus.value?.isError) {
        scope.launch {
            if (updateReservationStatus.value?.isError?.isNotEmpty() == true) {
                val error = updateReservationStatus.value?.isError
                Toast.makeText(context, "$error", Toast.LENGTH_LONG).show()
            }
        }
    }
    LaunchedEffect(key1 = acceptReservationState.value?.isSuccess) {
        scope.launch {
            if (acceptReservationState.value?.isSuccess?.isNotEmpty() == true) {
                val success = acceptReservationState.value?.isSuccess
                Toast.makeText(context, "$success", Toast.LENGTH_LONG).show()
            }
        }
    }
    LaunchedEffect(key1 = acceptReservationState.value?.isError) {
        scope.launch {
            if (acceptReservationState.value?.isError?.isNotEmpty() == true) {
                val error = acceptReservationState.value?.isError
                Toast.makeText(context, "$error", Toast.LENGTH_LONG).show()
            }
        }
    }
    LaunchedEffect(key1 = rejectReservationState.value?.isSuccess) {
        scope.launch {
            if (rejectReservationState.value?.isSuccess?.isNotEmpty() == true) {
                val success = rejectReservationState.value?.isSuccess
                Toast.makeText(context, "$success", Toast.LENGTH_LONG).show()
            }
        }
    }
    LaunchedEffect(key1 = rejectReservationState.value?.isError) {
        scope.launch {
            if (rejectReservationState.value?.isError?.isNotEmpty() == true) {
                val error = rejectReservationState.value?.isError
                Toast.makeText(context, "$error", Toast.LENGTH_LONG).show()
            }
        }
    }
}

@Composable
fun RequestsList(
    requests: List<RequestDetails>,
    navController: NavController,
    selectedRequest: RequestDetails?,
    onSelect: (RequestDetails) -> Unit,
    onReject: (RequestDetails) -> Unit
) {
    val pendingRequests = remember(requests) {
        derivedStateOf {
            requests.filter { it.reservation.status == "Pending" }
        }
    }

    if (pendingRequests.value.isEmpty()) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "You do not have any reservation",
                fontSize = 14.sp,
                fontFamily = RegularFont,
                color = Color.Gray,
                modifier = Modifier.padding(horizontal = 20.dp)
            )
            Text(
                text = "requests at the moment",
                fontSize = 14.sp,
                fontFamily = RegularFont,
                color = Color.Gray,
            )
        }
    } else {
        LazyColumn {
            items(pendingRequests.value) { request ->
                RequestItem(
                    requestDetails = request,
                    navController = navController,
                    isSelected = request == selectedRequest,
                    onSelect = { onSelect(request) },
                    onReject = { onReject(request) }
                )
            }
        }
    }
}

@Composable
fun RequestItem(
    requestDetails: RequestDetails,
    navController: NavController,
    isSelected: Boolean,
    onSelect: (RequestDetails) -> Unit,
    onReject: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .background(color = if (isSelected) Color.LightGray else Color.Transparent)
            .border(1.dp, Color.LightGray)
            .clickable { onSelect(requestDetails) }
    ) {
        Column(
            horizontalAlignment = Alignment.Start
        ) {
            Box(modifier = Modifier.padding(10.dp)) {
                if (requestDetails.user.profilePictureUrl.isNotEmpty()) {
                    Image(
                        painter = rememberAsyncImagePainter(requestDetails.user.profilePictureUrl),
                        contentDescription = "Profile Picture",
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape)
                            .border(2.dp, Color.LightGray, CircleShape),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Image(
                        painter = painterResource(id = R.drawable.ic_user_picture),
                        contentDescription = "Default Profile Picture",
                        modifier = Modifier
                            .size(60.dp)
                            .background(lightPurple, CircleShape)
                            .clip(CircleShape)
                            .border(2.dp, Color.LightGray, CircleShape),
                        contentScale = ContentScale.Crop
                    )
                }
            }
        }
        Column(
            modifier = Modifier.width(150.dp)
        ) {
            Text(
                text = requestDetails.user.firstName,
                fontWeight = FontWeight.Bold,
                fontFamily = RegularFont,
                fontSize = 14.sp,
                modifier = Modifier
                    .padding(top = 10.dp)
            )
            Row (
                modifier = Modifier.padding(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "★",
                    fontSize = 15.sp,
                    fontFamily = RegularFont,
                    color = Color.DarkGray,
                    modifier = Modifier.padding(end = 5.dp)
                )
                Text(
                    text = "${requestDetails.user.rating}",
                    fontFamily = RegularFont,
                    color = Color.DarkGray,
                    fontSize = 12.sp,
                )
            }
            Text(
                text = "View reviews",
                fontFamily = RegularFont,
                fontSize = 12.sp,
                color = purple,
                modifier = Modifier
                    .clip(shape = RoundedCornerShape(10.dp))
                    .clickable {
                        val route = Screens.ReviewsScreen.createRoute(requestDetails.user.uuid)
                        navController.navigate(route)
                    },
            )
            Text(
                text = "From: ${requestDetails.reservation.startDate} ${requestDetails.reservation.startTime}",
                fontFamily = RegularFont,
                fontSize = 12.sp
            )
            Text(
                text = "To: ${requestDetails.reservation.endDate} ${requestDetails.reservation.endTime}",
                fontFamily = RegularFont,
                fontSize = 12.sp,
                modifier = Modifier.padding(bottom = 10.dp)
            )
        }
        Column(
            horizontalAlignment = Alignment.End
        ) {
            IconButton(onClick = onReject) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Reject",
                    tint = red
                )
            }
        }
    }
}