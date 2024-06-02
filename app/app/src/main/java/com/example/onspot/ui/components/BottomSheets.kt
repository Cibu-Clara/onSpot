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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircleOutline
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.WarningAmber
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.onspot.R
import com.example.onspot.data.model.Marker
import com.example.onspot.data.model.ParkingSpot
import com.example.onspot.data.model.Vehicle
import com.example.onspot.navigation.Screens
import com.example.onspot.ui.theme.RegularFont
import com.example.onspot.ui.theme.green
import com.example.onspot.ui.theme.lightPurple
import com.example.onspot.ui.theme.purple
import com.example.onspot.utils.Resource
import com.example.onspot.viewmodel.SearchViewModel
import com.example.onspot.viewmodel.UserProfileViewModel
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.Date
import java.util.Locale


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImageOptionsBottomSheet(
    pictureUrl: String,
    sheetState: SheetState,
    onDismiss: () -> Unit,
    onTakePhoto: () -> Unit,
    onChooseFromGallery: () -> Unit,
    onDeletePhoto: () -> Unit
) {
    ModalBottomSheet(
        sheetState = sheetState,
        onDismissRequest = { onDismiss() }
    ) {
        Column(
            modifier = Modifier.padding(bottom = 16.dp, start = 16.dp, end = 16.dp)
        ) {
            BottomSheetButton(
                text = "Take Photo",
                icon = Icons.Default.CameraAlt,
                contentDescription = "Take Photo",
                textAlign = TextAlign.Start,
                onClick = onTakePhoto
            )
            BottomSheetButton(
                text = "Choose from Gallery",
                icon = Icons.Default.PhotoLibrary,
                contentDescription = "Choose from Gallery",
                textAlign = TextAlign.Start,
                onClick = onChooseFromGallery
            )
            if (pictureUrl.isNotEmpty()) {
                BottomSheetButton(
                    text = "Remove Photo",
                    icon = Icons.Default.Delete,
                    contentDescription = "Remove Photo",
                    onClick = onDeletePhoto,
                    textColor = Color.Red,
                    textAlign = TextAlign.Start,
                    iconColor = Color.Red
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ParkingSpotDetailsBottomSheet(
    parkingSpot: ParkingSpot,
    marker: Marker,
    sheetState: SheetState,
    onDismiss: () -> Unit,
    onReserve: () -> Unit,
    userProfileViewModel: UserProfileViewModel
) {
    val userDetails by userProfileViewModel.userDetails.collectAsState()
    val context = LocalContext.current
    val now by rememberSaveable { mutableStateOf(LocalDateTime.now()) }

    val startDate = LocalDate.parse(marker.startDate)
    val startTime = LocalTime.parse(marker.startTime)
    val endDate = LocalDate.parse(marker.endDate)
    val endTime = LocalTime.parse(marker.endTime)
    val endDateTime = LocalDateTime.of(endDate, endTime)
    val startDateTime = LocalDateTime.of(startDate, startTime)

    val isButtonDisabled = endDateTime.isBefore(now)
    val isCurrentlyAvailable = startDateTime.isBefore(now) && endDateTime.isAfter(now)

    ModalBottomSheet(
        sheetState = sheetState,
        onDismissRequest = { onDismiss() }
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = parkingSpot.address,
                fontFamily = RegularFont,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = purple
            )
            Text(
                text = "${parkingSpot.city}, ${parkingSpot.country}",
                fontFamily = RegularFont,
                fontSize = 20.sp
            )
            if (isCurrentlyAvailable) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 5.dp)) {
                    Text(text = "•", fontSize = 16.sp, modifier = Modifier.padding(end = 4.dp), color = green)
                    Text(
                        text = "Available now",
                        fontFamily = RegularFont,
                        fontSize = 12.sp,
                        color = green
                    )
                }
                Text(
                    text = "until ${marker.endDate} at ${marker.endTime}",
                    fontFamily = RegularFont,
                    color = Color.DarkGray,
                    fontSize = 12.sp
                )
            } else {
                Text(
                    text = "Available from ${marker.startDate} at ${marker.startTime} to ${marker.endDate} at ${marker.endTime}",
                    fontFamily = RegularFont,
                    color = Color.DarkGray,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(top = 5.dp)
                )
            }
            when (userDetails) {
                is Resource.Loading -> {
                    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                        CircularProgressIndicator()
                    }
                }
                is Resource.Success -> {
                    val user = userDetails.data
                    if (user != null) {
                        val signUpDate = user.creationTimestamp.let { timestamp ->
                            val date = Date(timestamp)
                            SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(date)
                        }
                        Row (
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier
                                .padding(horizontal = 20.dp, vertical = 15.dp)
                                .fillMaxWidth(),
                        ) {
                            Column(
                                horizontalAlignment = Alignment.Start
                            ) {
                                Row (
                                    modifier = Modifier.padding(bottom = 4.dp)
                                ){
                                    Text(
                                        text = "Posted by ",
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = RegularFont,
                                        fontSize = 18.sp,
                                    )
                                    Text(
                                        text = user.firstName,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = RegularFont,
                                        fontSize = 18.sp,
                                    )
                                }
                                Text(
                                    text = "member since $signUpDate",
                                    fontFamily = RegularFont,
                                    color = Color.Gray,
                                    modifier = Modifier.padding(bottom = 3.dp)
                                )
                                Row (
                                    modifier = Modifier.padding(bottom = 3.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "★",
                                        fontSize = 18.sp,
                                        fontFamily = RegularFont,
                                        color = Color.DarkGray,
                                        modifier = Modifier.padding(end = 5.dp)
                                    )
                                    Text(
                                        text = "4.9/5",
                                        fontFamily = RegularFont,
                                        color = Color.DarkGray,
                                    )
                                }
                                Text(
                                    text = "View ${user.firstName}'s reviews",
                                    fontFamily = RegularFont,
                                    color = Color.DarkGray,
                                    modifier = Modifier
                                        .clip(shape = RoundedCornerShape(10.dp))
                                        .clickable { },
                                )
                            }
                            Box {
                                if (user.profilePictureUrl.isNotEmpty()) {
                                    Image(
                                        painter = rememberAsyncImagePainter(user.profilePictureUrl),
                                        contentDescription = "Profile Picture",
                                        modifier = Modifier
                                            .size(97.dp)
                                            .clip(CircleShape)
                                            .border(2.dp, Color.LightGray, CircleShape),
                                        contentScale = ContentScale.Crop
                                    )
                                } else {
                                    Image(
                                        painter = painterResource(id = R.drawable.ic_user_picture),
                                        contentDescription = "Default Profile Picture",
                                        modifier = Modifier
                                            .size(80.dp)
                                            .background(lightPurple, CircleShape)
                                            .clip(CircleShape)
                                            .border(2.dp, Color.LightGray, CircleShape),
                                        contentScale = ContentScale.Crop
                                    )
                                }
                            }
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Outlined.Info,
                                contentDescription = "Info",
                                tint = Color.Gray,
                                modifier = Modifier
                                    .padding(start = 20.dp, end = 15.dp)
                                    .size(21.dp)
                            )
                            Text(
                                text = "More details will be provided after ${user.firstName} accepts your reservation request.",
                                fontFamily = RegularFont,
                                color = Color.Gray,
                                fontSize = 13.sp,
                                modifier = Modifier.padding(end = 20.dp)
                            )
                        }
                        Button(
                            onClick = onReserve,
                            enabled = ! isButtonDisabled,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = purple,
                                contentColor = Color.White
                            ),
                            modifier = Modifier.padding(top = 10.dp)
                        ) {
                            Text(
                                text = "Reserve parking spot",
                                fontFamily = RegularFont
                            )
                        }
                    }
                }
                is Resource.Error -> {
                    LaunchedEffect(key1 = true) {
                        Toast.makeText(context, "Error fetching user details", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VehicleOptionsBottomSheet(
    searchViewModel: SearchViewModel,
    sheetState: SheetState,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    val vehicles by searchViewModel.vehicles.collectAsState()
    lateinit var vehiclesList : List<Vehicle>

    val context = LocalContext.current

    var vehicleId by rememberSaveable { mutableStateOf("") }

    when (vehicles) {
        is Resource.Loading -> {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                CircularProgressIndicator()
            }
        }
        is Resource.Success -> {
            vehiclesList = vehicles.data!!
        }
        is Resource.Error -> {
            LaunchedEffect(key1 = true) {
                Toast.makeText(context, "Error fetching vehicles", Toast.LENGTH_LONG).show()
            }
        }
    }

    ModalBottomSheet(
        sheetState = sheetState,
        onDismissRequest = { onDismiss() }
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                modifier = Modifier.padding(start = 20.dp, end = 20.dp, bottom = 10.dp),
                text = "Which of your vehicles would you like to park there?",
                fontSize = 15.sp,
                fontFamily = RegularFont
            )
            DropDownMenuVehicles(
                label = "Select one of your vehicles",
                options = vehiclesList,
                onTextSelected = { vehicleId = it.uuid }
            )
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(
                    containerColor = purple,
                    contentColor = Color.White
                ),
                modifier = Modifier.padding(top = 10.dp)
            ) {
                Text(
                    text = "Confirm vehicle and reserve",
                    fontFamily = RegularFont
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeletePhotoBottomSheet(
    sheetState: SheetState,
    onDismiss: () -> Unit,
    onDeletePhoto: () -> Unit,
    onCancel: () -> Unit
) {
    ModalBottomSheet(
        sheetState = sheetState,
        onDismissRequest = { onDismiss() }
    ) {
        Column(
            modifier = Modifier.padding(bottom = 16.dp, start = 16.dp, end = 16.dp)
        ) {
            BottomSheetButton(
                text = "Remove Photo",
                contentDescription = "Remove Photo",
                onClick = onDeletePhoto,
                textColor = Color.Red,
                textAlign = TextAlign.Center,
                iconColor = Color.Red
            )
            BottomSheetButton(
                text = "Cancel",
                contentDescription = "Cancel",
                textAlign = TextAlign.Center,
                onClick = onCancel
            )
        }
    }
}

@Composable
fun BottomSheetButton(
    text: String,
    icon: ImageVector? = null,
    contentDescription: String?,
    onClick: () -> Unit,
    textColor: Color = MaterialTheme.colorScheme.primary,
    textAlign: TextAlign,
    iconColor: Color = MaterialTheme.colorScheme.primary
) {
    TextButton(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            Text(
                text = text,
                color = textColor,
                modifier = Modifier.weight(1f),
                textAlign = textAlign
            )
            Spacer(modifier = Modifier.width(16.dp))
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = contentDescription,
                    tint = iconColor,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}