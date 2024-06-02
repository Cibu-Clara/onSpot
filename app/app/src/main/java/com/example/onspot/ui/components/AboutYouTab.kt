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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.toUpperCase
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.onspot.R
import com.example.onspot.data.model.ParkingSpot
import com.example.onspot.data.model.User
import com.example.onspot.data.model.Vehicle
import com.example.onspot.navigation.Screens
import com.example.onspot.ui.theme.RegularFont
import com.example.onspot.ui.theme.lightPurple
import com.example.onspot.ui.theme.purple
import com.example.onspot.utils.Resource
import com.example.onspot.viewmodel.UserProfileViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun AboutYouTab(
    navController: NavController,
    userProfileViewModel: UserProfileViewModel,
    showBottomSheet: () -> Unit
) {
    val combinedState by userProfileViewModel.combinedLoadState.collectAsState()
    val changeProfilePictureState = userProfileViewModel.changeProfilePictureState.collectAsState(initial = null)
    val deleteProfilePictureState = userProfileViewModel.deleteProfilePictureState.collectAsState(initial = null)

    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        item {
            when (combinedState) {
                is Resource.Loading -> {
                    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                        CircularProgressIndicator()
                    }
                }
                is Resource.Success -> {
                    val (user, parkingSpots, vehicles) = combinedState.data!!
                    UserInfo(navController, user, parkingSpots, vehicles, showBottomSheet)
                }
                is Resource.Error -> {
                    val errorMessage = combinedState.message
                    LaunchedEffect(key1 = combinedState) {
                        Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
                    }
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                if (changeProfilePictureState.value?.isLoading == true || deleteProfilePictureState.value?.isLoading == true) {
                    CircularProgressIndicator()
                }
            }
            LaunchedEffect(key1 = changeProfilePictureState.value?.isSuccess) {
                scope.launch {
                    if (changeProfilePictureState.value?.isSuccess?.isNotEmpty() == true) {
                        val success = changeProfilePictureState.value?.isSuccess
                        Toast.makeText(context, "$success", Toast.LENGTH_LONG).show()
                    }
                }
            }
            LaunchedEffect(key1 = changeProfilePictureState.value?.isError) {
                scope.launch {
                    if (changeProfilePictureState.value?.isError?.isNotEmpty() == true) {
                        val error = changeProfilePictureState.value?.isError
                        Toast.makeText(context, "$error", Toast.LENGTH_LONG).show()
                    }
                }
            }
            LaunchedEffect(key1 = deleteProfilePictureState.value?.isSuccess) {
                scope.launch {
                    if (deleteProfilePictureState.value?.isSuccess?.isNotEmpty() == true) {
                        val success = deleteProfilePictureState.value?.isSuccess
                        Toast.makeText(context, "$success", Toast.LENGTH_LONG).show()
                    }
                }
            }
            LaunchedEffect(key1 = deleteProfilePictureState.value?.isError) {
                scope.launch {
                    if (deleteProfilePictureState.value?.isError?.isNotEmpty() == true) {
                        val error = deleteProfilePictureState.value?.isError
                        Toast.makeText(context, "$error", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }
}

@Composable
fun UserInfo(
    navController: NavController,
    user: User,
    parkingSpots: List<ParkingSpot>,
    vehicles: List<Vehicle>,
    showBottomSheet: () -> Unit
) {
    val signUpDate = user.creationTimestamp.let { timestamp ->
        val date = Date(timestamp)
        SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(date)
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        UserInfoRow(
            navController = navController,
            firstName = user.firstName,
            signUpDate = signUpDate,
            profilePictureUrl = user.profilePictureUrl,
            showBottomSheet = showBottomSheet
        )
        ParkingSpots(navController = navController, parkingSpots = parkingSpots)
        Vehicles(navController = navController, vehicles = vehicles)
    }
}

@Composable
fun UserInfoRow(
    navController: NavController,
    firstName: String,
    signUpDate: String,
    profilePictureUrl: String,
    showBottomSheet: () -> Unit
) {
    Column(
        modifier = Modifier.padding(horizontal = 10.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = firstName,
                    fontWeight = FontWeight.Bold,
                    fontFamily = RegularFont,
                    fontSize = 27.sp,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                Text(
                    text = "member since $signUpDate",
                    fontFamily = RegularFont,
                    color = Color.Gray
                )
                Row (
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "★",
                        fontSize = 18.sp,
                        fontFamily = RegularFont,
                        color = Color.Gray,
                        modifier = Modifier.padding(end = 5.dp)
                    )
                    Text(
                        text = "4.9/5",
                        fontFamily = RegularFont,
                        color = Color.Gray,
                    )
                }
            }
            Box {
                if (profilePictureUrl.isNotEmpty()) {
                    Image(
                        painter = rememberAsyncImagePainter(profilePictureUrl),
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
                            .size(97.dp)
                            .background(lightPurple, CircleShape)
                            .clip(CircleShape)
                            .border(2.dp, Color.LightGray, CircleShape),
                        contentScale = ContentScale.Crop
                    )
                }
                IconButton(
                    onClick = { showBottomSheet() },
                    modifier = Modifier
                        .size(30.dp)
                        .clip(CircleShape)
                        .align(Alignment.BottomEnd),
                    colors = IconButtonDefaults.iconButtonColors(Color.LightGray)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_edit),
                        contentDescription = null,
                        modifier = Modifier.size(17.dp),
                        tint = purple
                    )
                }
            }
        }

        // TODO(reviews)

        Text(
            modifier = Modifier
                .padding(top = 10.dp)
                .clip(shape = RoundedCornerShape(10.dp))
                .clickable { navController.navigate(Screens.ReviewsScreen.route) },
            text = "View all reviews",
            fontFamily = RegularFont,
            color = purple
        )
        Text(
            modifier = Modifier
                .padding(bottom = 15.dp, top = 10.dp)
                .clip(shape = RoundedCornerShape(10.dp))
                .clickable { navController.navigate(Screens.PersonalDetailsScreen.route) },
            text = "Edit personal details",
            fontFamily = RegularFont,
            color = purple
        )
        HorizontalDivider()
    }
}

@Composable
fun ParkingSpots(
    navController: NavController,
    parkingSpots: List<ParkingSpot>
) {
    Column(
        modifier = Modifier
            .padding(horizontal = 10.dp, vertical = 15.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = "Your parking spots",
            fontWeight = FontWeight.Bold,
            fontFamily = RegularFont,
            fontSize = 20.sp,
            modifier = Modifier.padding(bottom = 15.dp)
        )
        ParkingSpotsList(navController = navController, parkingSpots = parkingSpots)
        IconWithText(
            text = "Register a parking spot",
            isVerified = false,
            onAddAction = { navController.navigate(Screens.AddParkingSpotScreen.route) }
        )
        HorizontalDivider()
    }
}

@Composable
fun ParkingSpotsList(
    navController: NavController,
    parkingSpots: List<ParkingSpot>
) {
    Column {
        for (spot in parkingSpots){
            ParkingSpotListItem(
                address = spot.address,
                bayNumber = spot.bayNumber,
                onItemClick = {
                    val route = Screens.ParkingSpotDetailsScreen.createRoute(spot.uuid)
                    navController.navigate(route)
                }
            )
        }
    }
}

@Composable
fun ParkingSpotListItem(
    address: String,
    bayNumber: Int,
    onItemClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape = RoundedCornerShape(10.dp))
            .clickable(onClick = onItemClick)
            .padding(bottom = 15.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = address,
                fontSize = 16.sp
            )
            Text(
                text = "Parking spot no. $bayNumber",
                color = Color.Gray
            )
        }
        Icon(
            imageVector = Icons.Default.ArrowForward,
            contentDescription = "Go to details",
            modifier = Modifier.size(24.dp)
        )
    }
}

@Composable
fun Vehicles(
    navController: NavController,
    vehicles: List<Vehicle>
) {
    Column(
        modifier = Modifier
            .padding(horizontal = 10.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = "Your vehicles",
            fontWeight = FontWeight.Bold,
            fontFamily = RegularFont,
            fontSize = 20.sp,
            modifier = Modifier.padding(bottom = 15.dp)
        )
        VehiclesList(navController = navController, vehicles = vehicles)
        IconWithText(
            text = "Register a vehicle",
            isVerified = false,
            onAddAction = { navController.navigate(Screens.AddVehicleScreen.route) }
        )
    }
}

@Composable
fun VehiclesList(
    navController: NavController,
    vehicles: List<Vehicle>
) {
    Column {
        for (vehicle in vehicles){
            VehicleListItem(
                model = vehicle.make + " " + vehicle.model,
                color = vehicle.color,
                onItemClick = {
                     val route = Screens.VehicleDetailsScreen.createRoute(vehicle.uuid)
                     navController.navigate(route)
                }
            )
        }
    }
}

@Composable
fun VehicleListItem(
    model: String,
    color: String,
    onItemClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape = RoundedCornerShape(10.dp))
            .clickable(onClick = onItemClick)
            .padding(bottom = 15.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = model.uppercase(Locale.ROOT),
                fontSize = 16.sp
            )
            Text(
                text = color,
                color = Color.Gray
            )
        }
        Icon(
            imageVector = Icons.Default.ArrowForward,
            contentDescription = "Go to details",
            modifier = Modifier.size(24.dp)
        )
    }
}