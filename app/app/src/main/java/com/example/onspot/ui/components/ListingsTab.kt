package com.example.onspot.ui.components

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
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
import coil.compose.rememberAsyncImagePainter
import com.example.onspot.R
import com.example.onspot.data.model.ListingDetails
import com.example.onspot.data.model.RequestDetails
import com.example.onspot.ui.theme.RegularFont
import com.example.onspot.ui.theme.lightPurple
import com.example.onspot.ui.theme.purple
import com.example.onspot.ui.theme.red
import com.example.onspot.utils.Resource
import com.example.onspot.viewmodel.ReservationViewModel

@Composable
fun ListingsTab(
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
            ListingsList(listings ?: emptyList(), reservationViewModel)
        }
        is Resource.Error -> {
            LaunchedEffect(key1 = true) {
                Toast.makeText(context, "Error fetching listings", Toast.LENGTH_LONG).show()
            }
        }
    }
}

@Composable
fun ListingsList(listings: List<ListingDetails>, reservationViewModel: ReservationViewModel) {
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
            ListingCard(details, reservationViewModel)
        }
    }
}

@Composable
fun ListingCard(details: ListingDetails, reservationViewModel: ReservationViewModel) {
    var showMoreDetails by rememberSaveable { mutableStateOf(false) }
    var showRequestsDialog by rememberSaveable { mutableStateOf(false) }
    val requestsDetails by reservationViewModel.requestsDetails.collectAsState()
    var requests : List<RequestDetails>? = null
    val context = LocalContext.current

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
                    .clickable { }
                    .padding(bottom = 4.dp)
            )
            if (!details.marker.reserved) {
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
                    text = "Reserved by someone",
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
            reservationViewModel = reservationViewModel,
            onDismiss = { showRequestsDialog = false },
            onAccept = {
                // reservationViewModel.acceptRequest(reservationViewModel.selectedRequest.value?.reservation?.uuid ?: "", details.marker.uuid)
                showRequestsDialog = false
            }
        )
    }
}


@Composable
fun RequestsDialog(
    requests: List<RequestDetails>?,
    reservationViewModel: ReservationViewModel,
    onDismiss: () -> Unit,
    onAccept: () -> Unit
) {
    val selectedRequest by reservationViewModel.selectedRequest.collectAsState()

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
                selectedRequest = selectedRequest,
                onSelect = { reservationViewModel.selectRequest(it) },
                onReject = {
                    //reservationViewModel.rejectRequest(it.reservation.uuid)
                }
            )
        },
        confirmButton = {
            Button(
                onClick = onAccept,
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
}

@Composable
fun RequestsList(
    requests: List<RequestDetails>,
    selectedRequest: RequestDetails?,
    onSelect: (RequestDetails) -> Unit,
    onReject: (RequestDetails) -> Unit
) {
    if (requests.isEmpty()) {
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
    }
    LazyColumn {
        items(requests) { request ->
            RequestItem(
                requestDetails = request,
                isSelected = request == selectedRequest,
                onSelect = { onSelect(request) },
                onReject = { onReject(request) }
            )
        }
    }
}

@Composable
fun RequestItem(
    requestDetails: RequestDetails,
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
                    .clickable { },
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